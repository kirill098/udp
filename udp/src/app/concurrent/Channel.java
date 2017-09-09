package app.concurrent;

import java.util.LinkedList;

public class Channel<T> {
    private final LinkedList<Object> queue = new LinkedList<Object>();
    private final int maxObjects;
    private final Object lock = new Object();

    public Channel(int maxObjects) {
        this.maxObjects = maxObjects;
    }

    public void put(T obj) throws InterruptedException {
        if (obj == null)
            throw new IllegalArgumentException("must be not null");
        synchronized (lock) {
            while (queue.size() == maxObjects)
                    lock.wait();
            queue.addLast(obj);
            lock.notifyAll();
        }
    }

    public T get() throws InterruptedException {
        synchronized (lock) {
            while (queue.isEmpty()) {
                    lock.wait();
            }
            lock.notifyAll();
            return (T) queue.removeFirst();
        }
    }

    public int size() {
        synchronized (lock) {
            return queue.size();
        }
    }
}