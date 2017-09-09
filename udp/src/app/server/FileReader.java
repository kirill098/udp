package app.server;

import app.concurrent.Channel;
import app.common.Support;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class FileReader implements Runnable {

    private FileInputStream fis = null;

    private Channel<byte[]> channel;

    private Channel<byte[]> starter;

    private long numOfPacket;

    public FileReader(Channel<byte[]> channel, String filename, Channel<byte[]> starter)
    {
        try {
            fis = new FileInputStream(filename);
            numOfPacket = new File(filename).length();
            numOfPacket = numOfPacket/ Support.DATASIZE + ((numOfPacket% Support.DATASIZE == 0)?0:1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.starter = starter;
        this.channel = channel;
    }

    @Override
    public void run() {
        try {

            starter.get();

            channel.put(Support.longToByte(numOfPacket));
            System.out.println("Num of Packet = " + numOfPacket);
            while(true)
            {
                byte[] obj = new byte [Support.DATASIZE];
                int len = fis.read(obj);

                if(len == -1)
                {
                    fis.close();
                    System.out.println("EOF");
                    System.out.println("Reader has downed");
                    return;
                }
                channel.put(obj);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();

        }
    }
}
