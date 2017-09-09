package app.common;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;


public class Reciever implements Runnable {

    int log_counter;

    private DatagramSocket socket = null;

    private ReceiverCallBack callBack;

    public Reciever(ReceiverCallBack callBack, DatagramSocket socket)
    {
        this.callBack = callBack;
        log_counter = 0;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            byte[] buf = new byte[Support.DATASIZE + Integer.BYTES];
            Arrays.fill(buf, (byte) 0);
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            while (true) {

                    socket.receive(packet);
                    callBack.receiverCallBack(packet.getData());



            }
        } catch (InterruptedException e){
            System.out.println("Reciever has downed");
            socket.close();
        } catch (IOException e) {
            System.out.println("Socket closed. Reciever has downed");
        }
    }

}
