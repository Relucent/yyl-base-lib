package com.github.relucent.base.common.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
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
		Assert.assertTrue(resumeIsInterrupted.get());
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
	public void testAwaitTermination() throws InterruptedException {
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

	/**
	 * 回归：runParallel(tasks, permits) 必须真正限制并发数。 修复前 permits 被 Math.min(1, permits) 钳死为 1，所有任务串行执行。
	 */
	@Test
	public void testRunParallelRespectsPermits() throws InterruptedException {
		ThreadUtil util = new ThreadUtil() {
		};
		int permits = 2;
		int taskCount = 6;
		java.util.concurrent.atomic.AtomicInteger active = new java.util.concurrent.atomic.AtomicInteger(0);
		java.util.concurrent.atomic.AtomicInteger maxConcurrent = new java.util.concurrent.atomic.AtomicInteger(0);
		List<Runnable> tasks = new ArrayList<>(taskCount);
		for (int i = 0; i < taskCount; i++) {
			tasks.add(() -> {
				int cur = active.incrementAndGet();
				maxConcurrent.accumulateAndGet(cur, Math::max);
				ThreadUtil.sleepQuietly(200);
				active.decrementAndGet();
			});
		}
		util.runParallel(tasks, permits);
		Assert.assertTrue("max concurrent must not exceed permits", maxConcurrent.get() <= permits);
		Assert.assertEquals("max concurrent should equal permits after fix", permits, maxConcurrent.get());
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
