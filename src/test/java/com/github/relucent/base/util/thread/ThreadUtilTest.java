package com.github.relucent.base.util.thread;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Test;

public class ThreadUtilTest {

    private static final long ALLOWABLE_DELAY_MILLIS = 311;
    private static final long L1_MILLIS = 113;
    private static final long L2_MILLIS = 443;
    private static final long L3_MILLIS = 773;

    @Test
    public void testSleepUnchecked() throws InterruptedException {
        AtomicReference<Throwable> catchReference = new AtomicReference<>();
        AtomicBoolean resumeIsInterrupted = new AtomicBoolean();
        startInterruptJoin(new Runnable() {
            @Override
            public void run() {
                try {
                    ThreadUtil.sleepUnchecked(L1_MILLIS);
                } catch (Throwable e) {
                    catchReference.set(e);
                }
                resumeIsInterrupted.set(Thread.currentThread().isInterrupted());
            }
        });
        Assert.assertNotNull(catchReference.get());
        Assert.assertFalse(resumeIsInterrupted.get());
        Assert.assertTrue(catchReference.get() instanceof InterruptedRuntimeException);
    }

    @Test
    public void testSleepQuietly() throws InterruptedException {
        AtomicReference<Throwable> catchReference = new AtomicReference<>();
        AtomicBoolean resumeIsInterrupted = new AtomicBoolean();
        startInterruptJoin(new Runnable() {
            @Override
            public void run() {
                try {
                    ThreadUtil.sleepQuietly(L1_MILLIS);
                } catch (Throwable e) {
                    catchReference.set(e);
                }
                resumeIsInterrupted.set(Thread.currentThread().isInterrupted());
            }
        });
        Assert.assertNull(catchReference.get());
        Assert.assertTrue(resumeIsInterrupted.get());
    }

    @Test
    public void testStartAndJoinDaemon() throws InterruptedException {
        TaskRunnable task1 = new TaskRunnable(L1_MILLIS);
        TaskRunnable task2 = new TaskRunnable(L2_MILLIS);
        TaskRunnable task3 = new TaskRunnable(L3_MILLIS);
        ThreadUtil.startAndJoinDaemon(task1, task2, task3);
        Assert.assertTrue(task1.termination.get());
        Assert.assertTrue(task1.termination.get());
        Assert.assertTrue(task1.termination.get());
    }

    @Test
    public void testKeepIfInterrupted() throws InterruptedException {
        AtomicBoolean resumeIsInterrupted = new AtomicBoolean();
        Thread main = new Thread() {
            public void run() {
                try {
                    Thread.sleep(L1_MILLIS);
                } catch (InterruptedException e) {
                    ThreadUtil.keepIfInterrupted(e);
                }
                resumeIsInterrupted.set(isInterrupted());
            }
        };
        main.start();
        main.interrupt();
        main.join();
        Assert.assertTrue(resumeIsInterrupted.get());
    }

    @Test
    public void testAwaitTermination1() throws InterruptedException {
        AtomicBoolean innerIsAlive = new AtomicBoolean();
        AtomicBoolean resumeIsInterrupted = new AtomicBoolean();
        AtomicLong elapsedMillis = new AtomicLong();
        startInterruptJoin(new Runnable() {
            public void run() {
                Thread inner = new Thread(new TaskRunnable(L3_MILLIS));
                inner.start();
                long base = System.currentTimeMillis();
                ThreadUtil.awaitTermination(inner);
                elapsedMillis.set(System.currentTimeMillis() - base);
                innerIsAlive.set(inner.isAlive());
                resumeIsInterrupted.set(Thread.currentThread().isInterrupted());
            }
        });
        Assert.assertFalse(innerIsAlive.get());
        Assert.assertTrue(resumeIsInterrupted.get());
        Assert.assertTrue(elapsedMillis.get() - L3_MILLIS <= ALLOWABLE_DELAY_MILLIS);
    }

    @Test
    public void testAwaitTermination2() throws InterruptedException {
        Thread worker = new Thread(new TaskRunnable(L3_MILLIS));
        worker.start();
        long base = System.currentTimeMillis();
        ThreadUtil.awaitTermination(worker, L1_MILLIS);
        long elapsedMillis = System.currentTimeMillis() - base;
        Assert.assertTrue(elapsedMillis - L1_MILLIS <= ALLOWABLE_DELAY_MILLIS);
        Assert.assertTrue(worker.isAlive());
        worker.interrupt();
        worker.join();
        Assert.assertFalse(worker.isAlive());
    }

    private static Thread startInterruptJoin(Runnable runnable) throws InterruptedException {
        Thread thread = new Thread(runnable);
        thread.start();
        thread.interrupt();
        thread.join();
        return thread;
    }

    private static class TaskRunnable implements Runnable {

        private final long millis;
        private final AtomicBoolean termination;

        TaskRunnable(long millis) {
            this.millis = millis;
            this.termination = new AtomicBoolean(false);
        }

        @Override
        public void run() {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                // #Ignore
            }
            termination.set(true);
        }
    }
}
