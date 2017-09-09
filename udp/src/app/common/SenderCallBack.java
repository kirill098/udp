package app.common;


public interface SenderCallBack {
    public byte[] tryTowait() throws InterruptedException;
    public byte[] senderCallBack(byte[] buf, int counter) throws InterruptedException;
}
