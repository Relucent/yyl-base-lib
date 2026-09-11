package com.github.relucent.base.common.thread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Assert;
import org.junit.Test;

import com.github.relucent.base.common.concurrent.GlobalThreadPool;

public class GlobalThreadPoolTest {

    /**
     * 验证工作线程使用 NamedThreadFactory，线程名以 "global-pool-" 开头，便于 jstack / 监控溯源。
     */
    @Test
    public void testThreadNamePrefix() throws InterruptedException {
        final String[] captured = new String[1];
        CountDownLatch latch = new CountDownLatch(1);
        GlobalThreadPool.getInstance().execute(() -> {
            captured[0] = Thread.currentThread().getName();
            latch.countDown();
        });
        latch.await();
        Assert.assertNotNull("线程名未捕获", captured[0]);
        Assert.assertTrue("线程名应以 global-pool- 开头, 实际: " + captured[0], captured[0].startsWith("global-pool-"));
    }

    /**
     * 验证任务被真实并发执行（最大并发 >= 2），而非被无限池或串行化限制。
     */
    @Test
    public void testTasksExecuteConcurrently() throws InterruptedException {
        final int taskCount = 16;
        final AtomicInteger current = new AtomicInteger(0);
        final AtomicInteger maxConcurrent = new AtomicInteger(0);
        final CountDownLatch start = new CountDownLatch(1);
        final CountDownLatch done = new CountDownLatch(taskCount);

        for (int i = 0; i < taskCount; i++) {
            GlobalThreadPool.getInstance().execute(() -> {
                try {
                    start.await();
                    int now = current.incrementAndGet();
                    maxConcurrent.accumulateAndGet(now, Math::max);
                    Thread.sleep(80);
                    current.decrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }
        // 让所有任务先就位，再同时放行以制造并发窗口
        Thread.sleep(200);
        start.countDown();
        done.await();
        Assert.assertTrue("期望最大并发 >= 2, 实际: " + maxConcurrent.get(), maxConcurrent.get() >= 2);
    }

    /**
     * 验证队列 + 线程数达上限时采用 CallerRunsPolicy：新任务由调用方线程降级同步执行，
     * 既不抛 RejectedExecutionException，也不丢失任务，全部执行完成。
     */
    @Test
    public void testNoRejectedExceptionUnderLoad() throws InterruptedException {
        final int taskCount = 2000;
        final AtomicLong completed = new AtomicLong(0);
        final CountDownLatch done = new CountDownLatch(taskCount);

        for (int i = 0; i < taskCount; i++) {
            GlobalThreadPool.getInstance().submit(() -> {
                completed.incrementAndGet();
                done.countDown();
            });
        }

        // 若触发拒绝且抛异常，submit 调用本身就会失败；CallerRuns 下由调用方线程同步执行一部分
        boolean finished = done.await(30, java.util.concurrent.TimeUnit.SECONDS);
        Assert.assertTrue("部分任务未完成（可能被丢弃）", finished);
        Assert.assertEquals("任务执行总数不符", taskCount, completed.get());
    }
}
