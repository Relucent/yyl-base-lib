package com.github.relucent.base.common.concurrent;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 异步信号量（AsyncSemaphore） <br>
 * 功能：<br>
 * - 控制并发访问许可数量（permits）。<br>
 * - 获取许可是异步的，返回 CompletableFuture。<br>
 * - 释放许可后，会自动分配给等待队列中的任务。<br>
 * 基本用法：<br>
 * 
 * <pre>{@code
 * // 初始化 2 个许可
 * AsyncSemaphore semaphore = new AsyncSemaphore(2);
 * // 异步获取许可并执行任务
 * semaphore.acquire().thenRun(() -> {
 *     // 任务逻辑
 *     System.out.println("Task start");
 *     // 完成任务后释放许可
 *     semaphore.release();
 * });
 * 
 * // 查询等待队列大小
 * int waiting = semaphore.queueSize();
 * 
 * // 查询可用许可数
 * int available = semaphore.getCounter();
 * }</pre>
 * 
 * 注意事项： <br>
 * - acquire() 返回的 CompletableFuture 只有在许可可用时才完成。 <br>
 * - release() 必须在任务完成后调用，否则队列中的任务不会继续执行。 <br>
 * - 异步信号量不同于传统阻塞 Semaphore，它不会阻塞线程。 <br>
 * - 许可长期不足的情况下队列会无限增长，需要考虑资源情况。<br>
 */
public class AsyncSemaphore {

    /** 当前可用许可数 */
    private final AtomicInteger counter;

    /** 等待许可的任务队列，每个 CompletableFuture 表示一个等待任务 */
    private final Queue<CompletableFuture<Void>> listeners = new ConcurrentLinkedQueue<>();

    /**
     * 构造函数
     * @param permits 初始许可数量
     */
    public AsyncSemaphore(int permits) {
        counter = new AtomicInteger(permits);
    }

    /**
     * 获取等待队列大小
     * @return 当前等待许可的任务数量
     */
    public int queueSize() {
        return listeners.size();
    }

    /**
     * 清空等待队列<br>
     * 注意：清空后，队列中未完成的任务将不会获得许可。
     */
    public void removeQueue() {
        listeners.clear();
    }

    /**
     * 获取许可
     * @return CompletableFuture，当许可可用时完成
     */
    public CompletableFuture<Void> acquire() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        listeners.add(future);
        tryRun();
        return future;
    }

    /**
     * 尝试分配许可给队列中的任务<br>
     * 核心逻辑：<br>
     * 1. 尝试获取许可（counter 减一）。<br>
     * 2. 如果有任务等待，完成队列头部的 CompletableFuture。<br>
     * 3. 如果队列为空或许可不足，恢复 counter 并退出。<br>
     * 4. 循环处理连续可分配的许可。<br>
     */
    private void tryRun() {
        while (true) {
            // 尝试获取许可
            if (counter.decrementAndGet() >= 0) {
                // 取队列头部任务
                CompletableFuture<Void> future = listeners.poll();

                // 没有任务，恢复许可
                if (future == null) {
                    counter.incrementAndGet();
                    return;
                }
                // 完成任务，通知调用方
                if (future.complete(null)) {
                    return;
                }
            }
            // 没有许可可分配
            if (counter.incrementAndGet() <= 0) {
                return;
            }
        }
    }

    /**
     * 获取当前可用许可数
     * @return 可用许可数量
     */
    public int getCounter() {
        return counter.get();
    }

    /**
     * 释放许可<br>
     * 释放后会尝试分配许可给队列中等待的任务
     */
    public void release() {
        counter.incrementAndGet();
        tryRun();
    }

    @Override
    public String toString() {
        return "value:" + counter + ":queue:" + queueSize();
    }
}
