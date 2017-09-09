package app.server;

import app.common.ReceiverCallBack;
import app.common.Reciever;
import app.common.SenderCallBack;
import app.common.Support;
import app.concurrent.Channel;
import app.concurrent.ThreadPool;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


public class ProtocolServer implements SenderCallBack, ReceiverCallBack, Runnable {

    private Channel<byte[]> sndChannel;
    private Channel<byte[]> dataChannel;

    private ThreadPool threadPool;

    private final int myport;

    private final int notmyport;

    private int homanyre ;

    private long numOfpacket;
    private SlidingWidowServer window;

    private final String filename;

    private DatagramSocket socket;

    public ProtocolServer(int myport, int notmyport, String filename) {
        sndChannel = new Channel<byte[]>(Support.SNDCHANNELSIZE);

        dataChannel = new Channel<byte[]>(Support.DATACHANNELSIZE);

        this.myport = myport;
        this.notmyport = notmyport;

        this.filename = filename;
        this.homanyre = 0;

        this.window = new SlidingWidowServer();
    }

    public void receiverCallBack(byte[] data) throws IOException, InterruptedException {
        int number = Support.byteToInt(data, 0, 4);
        if(number == -1){
            sndChannel.put(new byte[0]);
            return;
        }
        System.out.println("Recieving " + number + " ticket");
        window.putTicket(number);
        if(window.getExpect() > numOfpacket) {
            stop();
            socket.close();
        }
    }
    public byte[] tryTowait() throws InterruptedException {
        synchronized (window) {
            if (window.isFull()) {
                window.wait(Support.TIMEOUT);
                if(window.isFull()) {
                    System.out.println("Resending " + window.getExpect() + "!!");
                    homanyre++;
                    return window.getFirstPacket();
                }
                else
                    return null;
            }
            else
                return null;

        }
    }
    public byte[] senderCallBack(byte[] buf, int counter) throws InterruptedException {
        if(counter == 0){
            numOfpacket = Support.byteToLong(buf, 0, 8);
        }
        byte[] data = new byte[buf.length + 4];
        System.arraycopy(Support.intToByte(counter), 0, data, 0, 4);
        System.arraycopy(buf, 0, data, 4, buf.length);
        window.putPacket(data);

        return data;
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

            threadPool.execute(new SenderServer(notmyport, dataChannel, this));
            threadPool.execute(new FileReader(dataChannel, filename, sndChannel));

        } catch (InterruptedException e){
            e.printStackTrace();
            System.exit(-5);
        }
    }

    public void stop(){
        threadPool.stop();
        System.out.println("Resendings = " + homanyre);
    }
}
