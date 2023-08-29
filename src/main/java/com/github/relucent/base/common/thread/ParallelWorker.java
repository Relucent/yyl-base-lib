package com.github.relucent.base.common.thread;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import com.github.relucent.base.common.logging.Logger;

/**
 * 并行工作执行器。<br>
 * 提供多线程并发执行一些任务的方法，可以指定执行的线程数量，以及全部任务完成的超时时间。<br>
 */
public class ParallelWorker {

    // ==============================Fields===========================================
    private final Logger logger = Logger.getLogger(getClass());
    private final Queue<Runnable> queue = new ConcurrentLinkedQueue<>();
    private final AtomicReference<State> state = new AtomicReference<>(State.NEW);

    // ==============================Methods==========================================
    /**
     * 添加任务
     * @param task 任务
     */
    public void add(Runnable task) {
        if (!State.NEW.equals(state.get())) {
            throw new IllegalStateException("Current status is " + state.get().name() + ",status, cannot add task!");
        }
        queue.offer(task);
    }

    /**
     * 开始执行任务
     * @param threads 执行线程数
     * @throws IllegalStateException 如果执行器运行状态错误
     * @throws InterruptedRuntimeException 如果当前线程被中断
     */
    public synchronized void run(int threads) {
        run(threads, null);
    }

    /**
     * 开始执行任务
     * @param timeout 超时时间
     * @throws IllegalStateException 如果执行器运行状态错误
     * @throws InterruptedRuntimeException 如果当前线程被中断
     */
    public synchronized void run(Duration timeout) {
        run(Integer.MAX_VALUE, null);
    }

    /**
     * 开始执行任务
     * @param threads 执行线程数
     * @param timeout 超时时间
     * @throws IllegalStateException 如果执行器运行状态错误
     * @throws InterruptedRuntimeException 如果当前线程被中断
     */
    public synchronized void run(int threads, Duration timeout) {

        if (!state.compareAndSet(State.NEW, State.RUNNING)) {
            throw new IllegalStateException("Current status is " + state.get().name() + ", Cannot execute!");
        }

        try {
            state.set(State.RUNNING);

            if (queue.isEmpty()) {
                return;
            }
            if (queue.size() == 1) {
                queue.poll().run();
                return;
            }
            int size = queue.size();

            CountDownLatch latch = new CountDownLatch(size);
            Semaphore semaphore = new Semaphore(threads);

            GlobalThreadPool pool = GlobalThreadPool.getInstance();
            List<Future<?>> futures = new ArrayList<>();
            for (int i = size; i > 0; i--) {
                futures.add(pool.submit(() -> {
                    while (true) {
                        Runnable task = queue.poll();
                        if (task == null) {
                            return;
                        }
                        try {
                            // 获取一个许可，在提供一个许可前一直将线程阻塞。获取一个许可并立即返回，将可用的许可数减 1
                            semaphore.acquire();
                            try {
                                task.run();
                            } finally {
                                // 释放一个许可，将其返回给信号量。释放一个许可，将可用的许可数增加 1。
                                semaphore.release();
                            }
                        } catch (Exception e) {
                            logger.error("!", e);
                            if (e instanceof InterruptedException) {
                                return;
                            }
                        } finally {
                            latch.countDown();
                        }
                    }
                }));
            }
            try {
                if (timeout == null) {
                    latch.await();
                } else {
                    latch.await(timeout.toMillis(), TimeUnit.MILLISECONDS);
                }
            } catch (InterruptedException e) {
                throw new InterruptedRuntimeException(e);
            } finally {
                cancel(futures);
            }

            queue.clear();

        } finally {
            state.set(State.COMPLETED);
        }
    }

    private static void cancel(List<Future<?>> futures) {
        for (Future<?> future : futures) {
            if (!future.isDone()) {
                future.cancel(true);
            }
        }
    }

    // ==============================InnerClass=======================================
    public enum State {
        /** 尚未启动的状态 */
        NEW,
        /** 执行中的状态 */
        RUNNING,
        /** 结束状态 */
        COMPLETED;
    }
}
