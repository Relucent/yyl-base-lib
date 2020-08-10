package com.github.relucent.base.common.thread;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.github.relucent.base.common.logging.Logger;

/**
 * 生产者消费者工作类
 */
public class ProcessWorker<T> implements Runnable {

	// ==============================Fields===========================================
	private static final int EMPTY_QUEUE_MAX_AWAIT_SECONDS = 31;
	private final Logger logger = Logger.getLogger(getClass());
	private final ReentrantLock newRequestLock = new ReentrantLock();
	private final Condition newRequestCondition = newRequestLock.newCondition();
	private final AtomicReference<WorkerState> stateReference = new AtomicReference<>(WorkerState.NEW);
	private final Object lock = new Object();

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
		checkRunningState();
		logger.info("Worker {} Thread Started!", name);
		try {
			// 延迟执行(让几个线程首次执行时间错开)
			try {
				TimeUnit.SECONDS.sleep(5 + (int) (Math.random() * 10));
			} catch (InterruptedException e) {
				return;
			}
			// 开始处理队列
			T: while (!Thread.currentThread().isInterrupted() && WorkerState.RUNNING.equals(stateReference.get())) {
				T request = null;
				try {
					request = supplier.get();
				} catch (Exception e) {
					if (e instanceof InterruptedException) {
						break T;
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
							break T;
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
		stateReference.set(WorkerState.INTERRUPTED);
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
	 * 检验线程可运行状态
	 */
	private void checkRunningState() {
		synchronized (lock) {
			WorkerState state = stateReference.get();
			if (!WorkerState.NEW.equals(state)) {
				throw new IllegalStateException("Worker is already " + state + " !");
			}
			stateReference.set(WorkerState.RUNNING);
		}
	}

	/**
	 * 等待新的请求<br>
	 * 使当前线程在接到信号、休眠期满或者被中断之前一直处于等待状态
	 */
	private void waitNewRequest() {
		int awaitSeconds = 1 + (int) (Math.random() * EMPTY_QUEUE_MAX_AWAIT_SECONDS); // 1~31
		newRequestLock.lock();
		try {
			logger.debug("Worker {} waitNewRequest({})", name, awaitSeconds);
			newRequestCondition.await(awaitSeconds, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logger.warn("Worker " + name + " waitNewRequest - interrupted Error ", e);
		} finally {
			newRequestLock.unlock();
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
