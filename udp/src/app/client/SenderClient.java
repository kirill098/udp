package app.client;

import app.common.Support;
import app.concurrent.Channel;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


public class SenderClient implements Runnable {

    private Channel<byte[]> channel;

    private DatagramSocket socket = null;

    private int notmyport;

    public SenderClient(int notmyport, Channel<byte[]> channel) {
        this.channel = channel;
        this.notmyport = notmyport;
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void run() {

        DatagramPacket packet;
        try {
            byte[] data = null;
            while (true) {

                data = channel.get();
                System.out.println("Sending " + Support.byteToInt(data, 0, 4) + " ticket");
                packet = new DatagramPacket(data, data.length, InetAddress.getLoopbackAddress(), notmyport);
                socket.send(packet);

            }
        } catch (InterruptedException e){
            System.out.println("Sender has downed");
            socket.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
