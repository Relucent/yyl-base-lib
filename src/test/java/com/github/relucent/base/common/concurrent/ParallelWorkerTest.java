package com.github.relucent.base.common.concurrent;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

public class ParallelWorkerTest {

	@Test
	public void testStart() {
		ParallelWorker worker = new ParallelWorker();
		AtomicInteger value = new AtomicInteger(0);
		AtomicInteger count = new AtomicInteger(0);
		Set<String> threadIds = ConcurrentHashMap.newKeySet();
		int size = 1000;
		for (int i = 0; i < size; i++) {
			worker.add(() -> {
				count.incrementAndGet();
				value.incrementAndGet();
				threadIds.add(Thread.currentThread().getName());
			});
			worker.add(() -> {
				count.incrementAndGet();
				value.decrementAndGet();
				threadIds.add(Thread.currentThread().getName());
			});
		}
		worker.run(1000);
		Assert.assertEquals(0, value.get());
		Assert.assertEquals(size << 1, count.get());
		System.out.println(threadIds.size());
	}

	/**
	 * 回归：run(Duration) 必须按超时时间返回，而不是永久等待所有任务完成。 修复前 run(Duration) 误写为 run(Integer.MAX_VALUE, null)，timeout
	 * 被丢弃，latch.await() 无超时永久阻塞。
	 */
	@Test
	public void testRunWithTimeoutReturnsPromptly() throws InterruptedException {
		ParallelWorker worker = new ParallelWorker();
		int size = 5;
		for (int i = 0; i < size; i++) {
			worker.add(() -> ThreadUtil.sleepQuietly(2000));
		}
		long start = System.currentTimeMillis();
		worker.run(Duration.ofMillis(300));
		long elapsed = System.currentTimeMillis() - start;
		Assert.assertTrue("run(timeout) must return near timeout instead of waiting for all tasks", elapsed < 1500);
	}
}
