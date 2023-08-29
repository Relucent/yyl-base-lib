package com.github.relucent.base.common.thread;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

import com.github.relucent.base.common.collection.CollectionUtil;
import com.github.relucent.base.common.exception.GeneralException;
import com.github.relucent.base.common.logging.Logger;

/**
 * 线程工具类，提供一些线程方法
 */
public class ThreadUtil {

    private static final Logger LOG = Logger.getLogger(ThreadUtil.class);

    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected ThreadUtil() {
    }

    /**
     * 在指定的毫秒数内让当前正在执行的线程休眠（暂停执行），此操作受到系统计时器和调度程序精度和准确性的影响。<br>
     * 如果任何线程中断了当前线程，该方法将抛出{@link InterruptedRuntimeException}异常，并且当前线程的中断状态 被清除。
     * @param millis 以毫秒为单位的休眠时间
     */
    public static void sleepUnchecked(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InterruptedRuntimeException(e);
        }
    }

    /**
     * 在指定的毫秒数内让当前正在执行的线程休眠（暂停执行），此操作受到系统计时器和调度程序精度和准确性的影响。<br>
     * 如果任何线程中断了当前线程，该方法将会直接返回，并且当前线程的中断状态会被保留 {@link Thread#interrupt}。<br>
     * @see InterruptedException
     * @param millis 以毫秒为单位的休眠时间
     */
    public static void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 获得当前线程堆栈列表
     * @return 线程堆栈列表
     */
    public static StackTraceElement[] getStackTrace() {
        return Thread.currentThread().getStackTrace();
    }

    /**
     * 异步执行任务，并返回异步结果。<br>
     * Future代表一个异步执行的操作，通过get()方法可以获得操作的结果，如果异步操作还没有完成，则，get()会使当前线程阻塞
     * @param task 执行的任务
     * @return 异步结果（{@link Future}）
     */
    public static Future<?> runAsync(Runnable task) {
        return GlobalThreadPool.getInstance().submit(task);
    }

    /**
     * 并行执行任务，会阻塞当前线程，直到所有任务完成<br>
     * @param tasks 待执行的任务
     * @param permits 并发数
     * @throws InterruptedException 线程发生中断异常
     */
    public void runParallel(Collection<Runnable> tasks, int permits) throws InterruptedException {
        // 没有任务需要执行
        if (CollectionUtil.isEmpty(tasks)) {
            return;
        }
        // 不允许出现空任务
        for (Runnable task : tasks) {
            if (task == null) {
                throw new GeneralException("TASKS_NOT_ALLOWED!");
            }
        }
        final CountDownLatch latch = new CountDownLatch(tasks.size());
        final Semaphore semaphore = new Semaphore(Math.min(1, permits));
        final Queue<Exception> exceptionQueue = new ConcurrentLinkedQueue<>();
        final GlobalThreadPool pool = GlobalThreadPool.getInstance();
        for (final Runnable task : tasks) {
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        semaphore.acquire();
                        try {
                            task.run();
                        } finally {
                            semaphore.release();
                        }
                    } catch (Exception e) {
                        LOG.error("!", e);
                        if (e instanceof InterruptedException) {
                            return;
                        }
                        exceptionQueue.add(e);
                    } finally {
                        latch.countDown();
                    }
                }
            });
        }
        latch.await();
    }

    /**
     * 异步执行任务，并返回异步结果。<br>
     * Future代表一个异步执行的操作，通过get()方法可以获得操作的结果，如果异步操作还没有完成，则，get()会使当前线程阻塞
     * @param <T> 任务返回的结果类型
     * @param task 执行的任务
     * @return 异步结果（{@link Future}）
     */
    public static <T> Future<T> runAsync(Callable<T> task) {
        return GlobalThreadPool.getInstance().submit(task);
    }

    /**
     * 启动一组守护线程任务，并且等待执行结束
     * @param runnable 一组线程任务
     */
    public static void startAndJoinDaemon(Runnable... runnable) {
        Thread[] threads = new Thread[runnable.length];
        for (int i = 0; i < runnable.length; i++) {
            Thread thread = threads[i] = new Thread(runnable[i]);
            thread.setDaemon(true);
            thread.start();
        }
        for (Thread thread : threads) {
            awaitTermination(thread, Long.MAX_VALUE);
        }
    }

    /**
     * 如果异常是中断异常({@link InterruptedException}或{@link InterruptedRuntimeException})，那么恢复中断状态 {@link Thread#interrupt}
     * @param throwable 异常
     */
    public static void keepIfInterrupted(Throwable throwable) {
        if (throwable instanceof InterruptedException || throwable instanceof InterruptedRuntimeException) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 等待线程结束最多{@code millis}毫秒。{@code 0}超时意味着永远等待。
     * @param thread 等待结束的线程
     * @param millis 等待时间(毫秒)
     */
    public static void awaitTermination(Thread thread, long millis) {
        boolean interrupted = false;
        long base = System.currentTimeMillis();
        long now = 0;
        if (millis <= 0) {
            millis = Long.MAX_VALUE;
        }
        try {
            while (thread.isAlive()) {
                long delay = millis - now;
                if (delay <= 0) {
                    break;
                }
                try {
                    thread.join(delay);
                } catch (InterruptedException e) {
                    interrupted = true;
                }
                now = System.currentTimeMillis() - base;
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
