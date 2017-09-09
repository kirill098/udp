package app.server;

import app.common.SenderCallBack;
import app.concurrent.Channel;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


public class SenderServer implements Runnable {
        int counter;

        private Channel<byte[]> channel;

        private SenderCallBack callBack;

        private DatagramSocket socket = null;

        private int notmyport;

        public SenderServer(int notmyport, Channel<byte[]> channel, SenderCallBack callBack) {
            this.channel = channel;
            this.notmyport = notmyport;
            this.callBack = callBack;
            this.counter = -1;
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
                data = callBack.tryTowait();
                if (data == null) {
                    data = channel.get();
                    counter++;
                    data = callBack.senderCallBack(data, counter);
                    System.out.println("Sending " + counter + " packet");
                }
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
