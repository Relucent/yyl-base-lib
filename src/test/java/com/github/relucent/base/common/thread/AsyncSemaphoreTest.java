package com.github.relucent.base.common.thread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

import com.github.relucent.base.common.concurrent.AsyncSemaphore;

public class AsyncSemaphoreTest {

    /**
     * 基本流程：有许可时 acquire 立即完成；release 后许可恢复。
     */
    @Test
    public void testAcquireAndRelease() {
        AsyncSemaphore semaphore = new AsyncSemaphore(1);
        CompletableFuture<Void> future = semaphore.acquire();
        Assert.assertTrue("有许可时 acquire 应立即完成", future.isDone());
        Assert.assertEquals("释放前应无可用许可", 0, semaphore.getCounter());
        semaphore.release();
        Assert.assertEquals("释放后许可应恢复为 1", 1, semaphore.getCounter());
    }

    /**
     * 限流正确性：许可不足时后续 acquire 等待；释放一个许可后等待者被唤醒。
     */
    @Test
    public void testLimitedPermitsWakesWaiter() {
        AsyncSemaphore semaphore = new AsyncSemaphore(2);
        CompletableFuture<Void> f1 = semaphore.acquire(); // 立即完成
        CompletableFuture<Void> f2 = semaphore.acquire(); // 立即完成
        CompletableFuture<Void> f3 = semaphore.acquire(); // 等待

        Assert.assertTrue(f1.isDone());
        Assert.assertTrue(f2.isDone());
        Assert.assertFalse("许可耗尽时 f3 应等待", f3.isDone());

        semaphore.release(); // 唤醒 f3
        Assert.assertTrue("释放后应唤醒等待的 f3", f3.isDone());
        Assert.assertEquals(0, semaphore.getCounter());
    }

    /**
     * 验证 removeQueue 取消等待任务，避免永久挂起（修复前的 CAS 自旋 + clear 竞争会导致永久挂起）。
     */
    @Test
    public void testRemoveQueueCancelsWaiters() {
        AsyncSemaphore semaphore = new AsyncSemaphore(0); // 无可用许可
        CompletableFuture<Void> future = semaphore.acquire(); // 入队等待
        Assert.assertFalse("无许可时 acquire 应等待", future.isDone());

        semaphore.removeQueue();
        Assert.assertTrue("removeQueue 应取消等待任务", future.isCancelled());
        Assert.assertEquals(0, semaphore.queueSize());
    }

    /**
     * 并发压力：批量 acquire 超过许可数产生等待者，独立释放线程持续 release，
     * 验证最终所有任务都被分配、无永久挂起，且许可全部回收。
     */
    @Test
    public void testConcurrentAcquireRelease() throws Exception {
        final int permits = 4;
        final int tasks = 100;
        AsyncSemaphore semaphore = new AsyncSemaphore(permits);
        CompletableFuture<?>[] futures = new CompletableFuture<?>[tasks];
        for (int i = 0; i < tasks; i++) {
            futures[i] = semaphore.acquire();
        }
        // 独立释放线程：释放与 acquire 数量相等的许可，唤醒所有等待者
        Thread releaser = new Thread(() -> {
            for (int i = 0; i < tasks; i++) {
                semaphore.release();
            }
        });
        releaser.start();

        CompletableFuture<Void> all = CompletableFuture.allOf(futures);
        all.get(10, TimeUnit.SECONDS); // 超时则说明存在永久挂起
        Assert.assertEquals("所有任务完成后许可应全部回收", permits, semaphore.getCounter());
    }
}
