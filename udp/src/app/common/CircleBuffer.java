package app.common;

import java.util.NoSuchElementException;


public class CircleBuffer {
        private byte [] buf;
        private int first;
        private int last;
        private final int size;
        private final int step;
        private int currentsize;

    public CircleBuffer(int sizeByte, int sizeofPack){
        this.buf =  new byte[sizeByte*sizeofPack];
        this.size = sizeofPack;
        this.first = 0;
        this.last = -1;
        this.step = sizeByte;
        this.currentsize = 0;
    }

    public boolean isEmpty(){
        return (currentsize==0);
    }
    public boolean isFull(){
        return (currentsize==size);
    }

    public byte[] removeFirst(){
        if (!this.isEmpty()) {
            currentsize--;
            int temp = first;
            if (first == 0)
                first = size - 1;
            else
                first--;
            byte[] byt = new byte[step];
            System.arraycopy(buf, temp, byt, 0, step);
            return byt;
        } else {
            throw new NoSuchElementException();
        }
    }

    public byte[] getFirst(){
        if (!this.isEmpty()) {
            byte[] byt = new byte[step];
            System.arraycopy(buf, first, byt, 0, step);
            return byt;
        } else {
            throw new NoSuchElementException();
        }
    }

    public void putLast(byte [] el){
        if (!this.isFull()) {
                if (last == size - 1)
                    last = 0;
                else
                    last++;
            currentsize++;
            System.arraycopy(el, 0, buf, last, el.length);

        } else throw new ArrayIndexOutOfBoundsException();
    }

    public int size(){
        return currentsize;
    }
}
