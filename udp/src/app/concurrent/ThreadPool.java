package app.concurrent;

import java.util.LinkedList;


public class ThreadPool {
    private final LinkedList<WorkerThread> allWorkers = new LinkedList<WorkerThread>();
    private final Channel<Runnable> freeWorkers;
    private final Object lock = new Object();
    private final int maxSize;
    private int currentSize;

    public ThreadPool(int maxSize) throws InterruptedException {
        WorkerThread worker;
        this.maxSize = maxSize;
        this.currentSize = 1;
        freeWorkers = new Channel<Runnable>(maxSize);
        worker = new WorkerThread(this);
        allWorkers.addLast(worker);
        freeWorkers.put(worker);
    }

    public void execute(Runnable task) throws InterruptedException {
        if (task == null)
            throw new IllegalArgumentException("must be not null");
        if (freeWorkers.size() <= 0)
            synchronized (lock) {
                if (maxSize > allWorkers.size()){
                    WorkerThread worker = new WorkerThread(this);
                    allWorkers.addLast(worker);
                    freeWorkers.put(worker);
                    currentSize++;
                }
            }
        ((WorkerThread) freeWorkers.get()).execute(task);

    }

    void onTaskCompleted(WorkerThread workerThread) throws InterruptedException {
        freeWorkers.put(workerThread);
    }

    public void stop() {
        for(WorkerThread worker : allWorkers)
            worker.stop();
        System.out.println("threadpool has stopped");
    }

    public boolean isBusy(){
        return freeWorkers.size() <= 0 && maxSize == currentSize;
    }
}
