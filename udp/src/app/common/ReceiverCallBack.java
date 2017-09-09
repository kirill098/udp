package app.common;

import java.io.IOException;


public interface ReceiverCallBack {
    public void receiverCallBack(byte[] data) throws IOException, InterruptedException;
}
