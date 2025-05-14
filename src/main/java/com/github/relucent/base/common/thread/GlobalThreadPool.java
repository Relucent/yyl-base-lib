package com.github.relucent.base.common.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.relucent.base.common.logging.Logger;

/**
 * 全局公共线程池<br>
 * 此线程池是一个无限线程池，即加入的线程不等待任何线程。
 */
public class GlobalThreadPool {

    // ==============================Fields===========================================
    private final Logger logger = Logger.getLogger(getClass());
    private final AtomicBoolean shutdownFlag = new AtomicBoolean(false);
    private final ExecutorService threadPool;

    // ==============================Construction=====================================
    /**
     * 获得公共线程池
     * @return 时间格式化程序实例
     */
    public static GlobalThreadPool getInstance() {
        return Holder.INSTANCE;
    }

    /** 单例模式用于延迟初始化 */
    private static class Holder {
        static final GlobalThreadPool INSTANCE = new GlobalThreadPool();
    }

    // ==============================Constructors=====================================
    /**
     * 构造函数
     */
    private GlobalThreadPool() {
        // 定义线程池
        threadPool = new ThreadPoolExecutor(//
                0, Integer.MAX_VALUE, //
                5L, TimeUnit.SECONDS, //
                new SynchronousQueue<>(), //
                Executors.defaultThreadFactory(), //
                new AbortPolicy()//
        );
        // 注册关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                GlobalThreadPool.this.shutdown();
            }
        }));
    }

    // ==============================Methods==========================================
    /**
     * 执行任务
     * @param task 执行的任务
     */
    public void execute(Runnable task) {
        threadPool.execute(task);
    }

    /**
     * 执行任务，并返回异步结果。<br>
     * Future代表一个异步执行的操作，通过get()方法可以获得操作的结果，如果异步操作还没有完成，则，get()会使当前线程阻塞
     * @param task 执行的任务
     * @return 异步结果（{@link Future}）
     */
    public Future<?> submit(Runnable task) {
        return threadPool.submit(task);
    }

    /**
     * 执行任务，并返回异步结果。<br>
     * Future代表一个异步执行的操作，通过get()方法可以获得操作的结果，如果异步操作还没有完成，则，get()会使当前线程阻塞
     * @param <T>  任务返回的结果类型
     * @param task 执行的任务
     * @return 异步结果（{@link Future}）
     */
    public <T> Future<T> submit(Callable<T> task) {
        return threadPool.submit(task);
    }

    /**
     * 优雅关闭
     */
    protected synchronized void shutdown() {
        if (shutdownFlag.get()) {
            return;
        }
        shutdownFlag.set(true);
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(30, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
                if (!threadPool.awaitTermination(30, TimeUnit.SECONDS)) {
                    logger.error("GlobalThreadPool did not terminate");
                }
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
