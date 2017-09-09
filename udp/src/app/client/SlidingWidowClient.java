package app.client;

import app.common.CircleBuffer;
import app.common.Support;

import java.util.ArrayList;


public class SlidingWidowClient {
    public final static int WINDOWSIZE = 5;

    private int expect;

    private CircleBuffer buffer;
    private ArrayList<Integer> tickets;

    public SlidingWidowClient(){
        expect = 0;
        buffer = new CircleBuffer(Support.DATASIZE,WINDOWSIZE);
        tickets = new ArrayList<Integer>(WINDOWSIZE);
    }

    public boolean isExpectedPacket(int count){
        return count == expect;
    }

    public int getExpect(){
        return expect;
    }

    public void incExpect(){
        expect++;
    }

    public void putPacket(byte [] x, int y){

            if(y >= expect) {
                buffer.putLast(x);
                tickets.add(y);
            }
    }

    public byte[] removePacket(){
            if(tickets.size() == 0){
                return null;
            }
            else {
                if (tickets.get(0) == expect) {
                    expect++;
                    tickets.remove(0);
                    return buffer.removeFirst();
                } else
                    return null;
            }
    }
}
