package app.client;

import app.common.ReceiverCallBack;
import app.common.Reciever;
import app.common.Support;
import app.concurrent.Channel;
import app.concurrent.ThreadPool;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ProtocolClient implements ReceiverCallBack, Runnable {

    private Channel<byte[]> sndChannel;
    private Channel<byte[]> dataChannel;

    private ThreadPool threadPool;


    private final int myport;

    private final int notmyport;

    private long numOfpacket;
    private SlidingWidowClient window;

    private final String filename;

    private DatagramSocket socket;

    public ProtocolClient(int myport, int notmyport, String filename) {

        sndChannel = new Channel<byte[]>(Support.SNDCHANNELSIZE);

        dataChannel = new Channel<byte[]>(Support.DATACHANNELSIZE);

        this.myport = myport;
        this.notmyport = notmyport;

        this.filename = filename;

        this.window = new SlidingWidowClient();
    }

    public void receiverCallBack(byte[] data) throws IOException, InterruptedException {
        int number = Support.byteToInt(data, 0, 4);
        if(number == 0){
            System.out.println("Recieving number of packet");
            numOfpacket = Support.byteToInt(data, 8, 4);
            window.incExpect();
            sndChannel.put(Support.intToByte(number));
            System.out.println("Num of Packet = " + numOfpacket);
            return;
        }

        sndChannel.put(Support.intToByte(number));
        System.out.println("Recieving " + number + " packet");
        if(window.isExpectedPacket(number)) {
            byte[] dataforFile = new byte[data.length - 4];
            System.arraycopy(data, 4, dataforFile, 0, dataforFile.length);
            dataChannel.put(dataforFile);

            window.incExpect();
            while ((dataforFile = window.removePacket()) != null) {
                dataChannel.put(dataforFile);
            }
            synchronized (window) {
                if (window.getExpect() > numOfpacket) {
                    stop();
                    socket.close();

                }
            }
        }
        else{
            byte[] dataforFile = new byte[data.length - 4];
            System.arraycopy(data, 4, dataforFile, 0, dataforFile.length);
            window.putPacket(dataforFile, number);
        }
    }

    public void run(){
        try {
            try {
                socket = new DatagramSocket(myport, InetAddress.getLoopbackAddress());
            } catch (SocketException e) {
                e.printStackTrace();
            }


            threadPool = new ThreadPool(3);

            threadPool.execute(new Reciever(this, socket));

            threadPool.execute(new SenderClient(notmyport, sndChannel));
            sndChannel.put(Support.intToByte(Support.START));
            threadPool.execute(new FileWriter(dataChannel, filename));

        } catch (InterruptedException e){
            e.printStackTrace();
            System.exit(-5);
        }
    }

    public void stop(){
        threadPool.stop();

    }
}

