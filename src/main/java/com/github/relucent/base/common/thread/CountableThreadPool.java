package com.github.relucent.base.common.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 可数线程池(可以动态规定线程池尺寸)
 * @author YYL
 */
public class CountableThreadPool {

    private static final int AWAIT_TERMINATION_TIMEOUT_SECONDS = 13;
    private static final int MINIMUM_POOL_SIZE = 1;
    private static final int MAXIMUM_POOL_SIZE = 100;
    private final ExecutorService executorService;
    private final AtomicInteger threadAlive = new AtomicInteger();
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private volatile int poolSize;

    /**
     * 构造线程池(默认线程数为1)
     */
    public CountableThreadPool() {
        this(MINIMUM_POOL_SIZE);
    }

    /**
     * 构造线程池
     * @param poolSize 线程池尺寸
     */
    public CountableThreadPool(int poolSize) {
        this.executorService = Executors.newCachedThreadPool();
        this.setPoolSize(poolSize);
    }

    /**
     * 执行任务
     * @param task 任务
     */
    public void execute(final Runnable task) {
        if (threadAlive.get() >= poolSize) {
            try {
                lock.lock();
                while (threadAlive.get() >= poolSize) {
                    try {
                        condition.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            } finally {
                lock.unlock();
            }
        }
        threadAlive.incrementAndGet();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    task.run();
                } finally {
                    try {
                        lock.lock();
                        threadAlive.decrementAndGet();
                        condition.signal();
                    } finally {
                        lock.unlock();
                    }
                }
            }
        });
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = Math.max(Math.min(poolSize, MAXIMUM_POOL_SIZE), MINIMUM_POOL_SIZE);
    }

    public int getThreadAlive() {
        return threadAlive.get();
    }

    public boolean isShutdown() {
        return executorService.isShutdown();
    }

    public boolean isTerminated() {
        return executorService.isTerminated();
    }

    public void shutdown() {
        executorService.shutdown();
        try {
            executorService.awaitTermination(AWAIT_TERMINATION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        executorService.shutdownNow();
    }
}
