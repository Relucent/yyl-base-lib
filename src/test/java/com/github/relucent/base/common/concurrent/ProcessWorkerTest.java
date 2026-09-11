package com.github.relucent.base.common.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Assert;
import org.junit.Test;

public class ProcessWorkerTest {

	/**
	 * shutdown() 必须能真正停止 worker 线程。<br>
	 */
	@Test
	public void testShutdownStopsWorker() throws InterruptedException {
		BlockingQueue<String> queue = new LinkedBlockingQueue<>();
		ProcessWorker<String> worker = new ProcessWorker<>("test", () -> {
			try {
				return queue.take();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return null;
			}
		}, x -> {
			// noop
		});
		Thread thread = new Thread(worker);
		thread.start();
		// 让 worker 至少进入 RUNNING（compareAndSet 已通过，可能仍处在 startup delay sleep）
		ThreadUtil.sleepQuietly(100);
		worker.shutdown();
		thread.join(2000);
		Assert.assertFalse("worker must terminate after shutdown()", thread.isAlive());
	}
}
