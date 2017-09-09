package app.client;

import app.concurrent.Channel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileWriter implements Runnable {

    private FileOutputStream fos = null;

    private Channel<byte[]> channel;

    public FileWriter(Channel<byte[]> channel, String filename)
    {
        try {
            fos = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.channel = channel;
    }

    @Override
    public void run() {
        try {
            while(true)
            {
                byte[] d = channel.get();
                        fos.write(d);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("Writer has downed");
        }
        finally {
            try {
                fos.close();
                System.out.println("EOWrite");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
