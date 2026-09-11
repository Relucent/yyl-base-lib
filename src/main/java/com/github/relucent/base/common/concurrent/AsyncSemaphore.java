package com.github.relucent.base.common.concurrent;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

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
     * 注意事项：<br>
     * - acquire() 返回的 CompletableFuture 只有在许可可用时才完成。<br>
     * - release() 必须在任务完成后调用，否则队列中的任务不会继续执行。<br>
     * - 异步信号量不同于传统阻塞 Semaphore，它不会阻塞线程。<br>
 * - 许可长期不足的情况下队列会无限增长，需要考虑资源情况。<br>
 * - 所有对内部状态（许可计数与等待队列）的访问均串行化，避免并发分配错误。
 */
public class AsyncSemaphore {

    /** 当前可用许可数 */
    private int counter;

    /** 等待许可的任务队列，每个 CompletableFuture 表示一个等待任务 */
    private final Queue<CompletableFuture<Void>> listeners = new ConcurrentLinkedQueue<>();

    /**
     * 构造函数
     * @param permits 初始许可数量
     */
    public AsyncSemaphore(int permits) {
        this.counter = permits;
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
     * 清空时会取消队列中正在等待的任务（对应 CompletableFuture 被标记为取消），
     * 使其不再永久挂起，调用方可通过 whenComplete / exceptionally 感知取消。
     */
    public void removeQueue() {
        synchronized (this) {
            CompletableFuture<Void> future;
            while ((future = listeners.poll()) != null) {
                future.cancel(false);
            }
        }
    }

    /**
     * 获取许可
     * @return CompletableFuture，当许可可用时完成
     */
    public CompletableFuture<Void> acquire() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        synchronized (this) {
            listeners.add(future);
            tryRun();
        }
        return future;
    }

    /**
     * 尝试分配许可给队列中的任务（调用方需已持有本对象的监视器锁）。<br>
     * 核心逻辑：<br>
     * 1. 只要还有可用许可（counter &gt; 0）就持续尝试分配。<br>
     * 2. 从队列头部取出一个等待任务，完成其 CompletableFuture 并消耗一个许可。<br>
     * 3. 若该 future 已被取消或提前完成（complete 返回 false），则丢弃并继续处理下一个，不消耗许可。<br>
     * 4. 队列为空或许可不足时退出。<br>
     */
    private void tryRun() {
        while (counter > 0) {
            CompletableFuture<Void> future = listeners.peek();
            if (future == null) {
                return;
            }
            // 取出队头（无论是否成功分配都移除，避免重复处理）
            listeners.poll();
            // 分配许可：成功完成则消耗一个许可；已取消/完成的 future 直接丢弃，继续下一个
            if (future.complete(null)) {
                counter--;
            }
        }
    }

    /**
     * 获取当前可用许可数
     * @return 可用许可数量
     */
    public int getCounter() {
        synchronized (this) {
            return counter;
        }
    }

    /**
     * 释放许可<br>
     * 释放后会尝试分配许可给队列中等待的任务
     */
    public void release() {
        synchronized (this) {
            counter++;
            tryRun();
        }
    }

    @Override
    public String toString() {
        return "value:" + counter + ":queue:" + queueSize();
    }
}
