package app.common;


import java.nio.ByteBuffer;


public class Support {

    public static final int START = -1;

    public final static int DATASIZE = 10000;
    public final static int SNDCHANNELSIZE = 10;
    public final static int DATACHANNELSIZE = 10;
    public final static int TIMEOUT = 3000;
    public final static byte [] LOL = {0};

    static public byte[] intToByte(int data) {
        return ByteBuffer.allocate(4).putInt(data).array();
    }
    static public int byteToInt(byte[] data, int offset, int length) {
        return ByteBuffer.wrap(data, offset, length).getInt();
    }
    static public byte[] longToByte(long data) {
        return ByteBuffer.allocate(8).putLong(data).array();
    }
    static public long byteToLong(byte[] data, int offset, int length) {
        return ByteBuffer.wrap(data, offset, length).getLong();
    }

}
