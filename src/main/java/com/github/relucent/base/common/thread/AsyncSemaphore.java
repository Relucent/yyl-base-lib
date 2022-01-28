package com.github.relucent.base.common.thread;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class AsyncSemaphore {

    private final AtomicInteger counter;
    private final Queue<Runnable> commandQueues = new ConcurrentLinkedQueue<>();

    public AsyncSemaphore(int permits) {
        counter = new AtomicInteger(permits);
    }

    public boolean tryAcquire(long timeoutMillis) {
        CountDownLatch latch = new CountDownLatch(1);
        Runnable runnable = () -> latch.countDown();
        acquire(runnable);
        try {
            return latch.await(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public int queueSize() {
        return commandQueues.size();
    }

    public void removeQueue() {
        commandQueues.clear();
    }

    public void acquire(Runnable command) {
        commandQueues.add(command);
        tryRun();
    }

    private void tryRun() {
        if (counter.decrementAndGet() >= 0) {
            Runnable command = commandQueues.poll();
            if (command == null) {
                counter.incrementAndGet();
                return;
            }
            command.run();
        } else {
            if (counter.incrementAndGet() > 0) {
                tryRun();
            }
        }
    }

    public int getCounter() {
        return counter.get();
    }

    public void release() {
        counter.incrementAndGet();
        tryRun();
    }

    @Override
    public String toString() {
        return "value:" + counter + ":queue:" + queueSize();
    }
}
