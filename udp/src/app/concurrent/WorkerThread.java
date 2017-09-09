package app.concurrent;


public class WorkerThread implements Runnable{
    private final Thread thread;
    private final ThreadPool boss;
    private final Object lock = new Object();
    private volatile boolean isActive;
    private Runnable currentTask;

    public WorkerThread(ThreadPool boss) {
        this.boss = boss;
        this.thread = new Thread(this);
        this.isActive = true;
        thread.start();
    }

    @Override
    public void run() {
        synchronized (lock) {
            while (isActive) {
                while (currentTask == null)
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        if (!isActive)
                            return;
                    }
                try {
                    if (!isActive)
                        return;
                    currentTask.run();
                } catch (RuntimeException e){
                    e.printStackTrace();
                } finally {
                    //System.out.println(thread.getName() + "is over");
                    currentTask = null;
                    try {
                        boss.onTaskCompleted(this);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void execute(Runnable task) {
        if (task == null)
            throw new IllegalArgumentException("must be not null");
        synchronized (lock) {
            if (currentTask != null)
                throw new IllegalStateException("PANIC");
            currentTask = task;
            lock.notify();
        }
    }

    public void stop() {
        isActive = false;
        thread.interrupt();
    }
}

