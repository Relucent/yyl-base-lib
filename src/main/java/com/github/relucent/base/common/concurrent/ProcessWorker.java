package com.github.relucent.base.common.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.github.relucent.base.common.logging.Logger;

/**
 * 生产者消费者工作类<br>
 * 本类实现一个单消费者线程，用于从外部“拉模式”生产者获取任务（Supplier&lt;T&gt;）并处理（Consumer&lt;T&gt;）。<br>
 * 生产者采用拉模式：Worker 主动调用 supplier.get() 获取任务，生产者本身不阻塞，也可能返回 null 表示当前没有任务。<br>
 * 当没有任务时，Worker 不会盲目消费，而是随机等待一段时间后继续拉取，以降低空轮询压力。<br>
 * 支持安全中断与 shutdown：捕获 InterruptedException 并恢复中断状态，保证线程可以被外部及时停止。<br>
 * 状态管理通过 AtomicReference&lt;WorkerState&gt; 实现，保证线程安全，并防止重复启动。<br>
 */
public class ProcessWorker<T> implements Runnable {

    // ==============================Fields===========================================
    private static final int EMPTY_QUEUE_MAX_AWAIT_SECONDS = 31;
    private final Logger logger = Logger.getLogger(getClass());
    private final AtomicReference<WorkerState> stateReference = new AtomicReference<>(WorkerState.NEW);
    private final String name;
    private final Supplier<T> supplier;
    private final Consumer<T> consumer;

    // ==============================Constructors=====================================
    public ProcessWorker(String name, Supplier<T> supplier, Consumer<T> consumer) {
        this.name = name;
        this.supplier = supplier;
        this.consumer = consumer;
    }

    // ==============================Methods==========================================
    @Override
    public void run() {

        // 检验线程可运行状态
        if (!stateReference.compareAndSet(WorkerState.NEW, WorkerState.RUNNING)) {
            throw new IllegalStateException("Worker already started or terminated.");
        }

        logger.info("Worker {} Thread Started!", name);
        try {
            // 延迟执行(让几个线程首次执行时间错开)
            try {
                TimeUnit.SECONDS.sleep(5L + (long) (Math.random() * 10));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            // 开始处理队列
            while (!Thread.currentThread().isInterrupted() && WorkerState.RUNNING.equals(stateReference.get())) {
                T request = null;
                try {
                    request = supplier.get();
                } catch (Exception e) {
                    if (e instanceof InterruptedException) {
                        return;
                    }
                    logger.error("poll()", e);
                }

                if (request == null) {
                    // wait until new request added
                    waitNewRequest();
                } else {
                    try {
                        process(request);
                    } catch (Exception e) {
                        if (e instanceof InterruptedException) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                        logger.error("process request " + request + " error", e);
                    }
                }
            }
        } finally {
            stateReference.set(WorkerState.TERMINATED);
            logger.info("Worker {} Thread Terminated!", name);
        }
    }

    /**
     * 停止运行
     */
    public void shutdown() {
        // 设置状态
        stateReference.set(WorkerState.INTERRUPTED);
        // 响应阻塞或 sleep 中断
        Thread.currentThread().interrupt();
    }

    /**
     * 处理队列
     * @param element 队列元素
     */
    private void process(T element) {
        try {
            consumer.accept(element);
        } catch (Exception e) {
            logger.error("Worker Process Error", e);
        }
    }

    /**
     * 等待新的请求<br>
     * 使当前线程在接到信号、休眠期满或者被中断之前一直处于等待状态
     */
    private void waitNewRequest() {
        int awaitSeconds = 1 + (int) (Math.random() * EMPTY_QUEUE_MAX_AWAIT_SECONDS); // 1~31
        try {
            logger.debug("Worker {} waitNewRequest({})", name, awaitSeconds);
            TimeUnit.SECONDS.sleep(awaitSeconds);
        } catch (InterruptedException e) {
            logger.warn("Worker " + name + " waitNewRequest - interrupted Error ", e);
            Thread.currentThread().interrupt();
        }
    }

    /** 工作者状态 */
    public enum WorkerState {
        /** 初始 */
        NEW,
        /** 运行中 */
        RUNNING,
        /** 中断 */
        INTERRUPTED,
        /** 终止 */
        TERMINATED;
    }
}
