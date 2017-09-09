package app.server;

import app.common.CircleBuffer;
import app.common.Support;

import java.util.ArrayList;


public class SlidingWidowServer {
    public final static int WINDOWSIZE = 5;

    private int expect;

    private CircleBuffer buffer;
    private ArrayList<Integer> tickets;

    public SlidingWidowServer(){
        expect = 0;
        buffer = new CircleBuffer(Support.DATASIZE + Integer.BYTES,WINDOWSIZE);
        tickets = new ArrayList<Integer>(WINDOWSIZE);
    }

    public boolean isFull() {
        return buffer.isFull();
    }

    public int getExpect(){
        return expect;
    }

    public byte[] getFirstPacket(){
        return buffer.getFirst();
    }

    public void putPacket(byte[] ob){
        synchronized (this) {
            if (!buffer.isFull()) {
                buffer.putLast(ob);
            }
        }
    }

    public void putTicket(int tic){
        synchronized (this) {
                if (tic == expect) {

                    expect++;
                    buffer.removeFirst();
                    this.notifyAll();
                    while ((tickets.size() > 0) && (tickets.get(0) == expect)) {
                        buffer.removeFirst();
                        tickets.remove(0);
                        expect++;
                    }

                } else if (tic > expect)
                    tickets.add(new Integer(tic));
        }
    }
}
