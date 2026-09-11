package com.github.relucent.base.common.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.relucent.base.common.logging.Logger;

/**
 * 全局公共线程池<br>
 * 这是一个有上限的通用线程池：核心线程数随 CPU 自适应，最大线程数固定为 200，任务队列有界（1024）。
 * 当线程数与队列均达上限时，新提交的任务由调用方线程降级同步执行（CallerRunsPolicy），避免任务丢失或无限创建线程。
 */
public class GlobalThreadPool {

	// ==============================Fields===========================================
	/** 线程池最大线程数上限 */
	private static final int MAXIMUM_POOL_SIZE = 200;
	/** 任务队列容量 */
	private static final int QUEUE_CAPACITY = 1024;

	private final Logger logger = Logger.getLogger(getClass());
	private final AtomicBoolean shutdownFlag = new AtomicBoolean(false);
	private final ExecutorService threadPool;

	// ==============================Construction=====================================
	/**
	 * 获得公共线程池
	 * @return 全局公共线程池实例
	 */
	public static GlobalThreadPool getInstance() {
		return Holder.INSTANCE;
	}

	/** 单例模式用于延迟初始化 */
	private static class Holder {
		static final GlobalThreadPool INSTANCE = new GlobalThreadPool();
	}

	// ==============================Constructors=====================================
	/**
	 * 构造函数
	 */
	private GlobalThreadPool() {
		// 核心线程数随 CPU 核数自适应（IO 密集启发值），至少 4 个
		int corePoolSize = Math.max(4, Runtime.getRuntime().availableProcessors() * 2);
		// 定义线程池：有界线程数 + 有界队列 + 命名线程 + 满负载降级同步执行
		threadPool = new ThreadPoolExecutor(//
				corePoolSize, MAXIMUM_POOL_SIZE, //
				60L, TimeUnit.SECONDS, //
				new LinkedBlockingQueue<Runnable>(QUEUE_CAPACITY), //
				new NamedThreadFactory("global-pool", false, new GlobalPoolExceptionHandler()), //
				new CallerRunsPolicy()//
		);
		// 允许核心线程超时回收，空闲时不长期占用资源
		((ThreadPoolExecutor) threadPool).allowCoreThreadTimeOut(true);
		// 注册关闭钩子
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				GlobalThreadPool.this.shutdown();
			}
		}));
	}

	// ==============================Methods==========================================
	/**
	 * 执行任务
	 * @param task 执行的任务
	 */
	public void execute(Runnable task) {
		threadPool.execute(task);
	}

	/**
	 * 执行任务，并返回异步结果。<br>
	 * Future代表一个异步执行的操作，通过get()方法可以获得操作的结果，如果异步操作还没有完成，则，get()会使当前线程阻塞
	 * @param task 执行的任务
	 * @return 异步结果（{@link Future}）
	 */
	public Future<?> submit(Runnable task) {
		return threadPool.submit(task);
	}

	/**
	 * 执行任务，并返回异步结果。<br>
	 * Future代表一个异步执行的操作，通过get()方法可以获得操作的结果，如果异步操作还没有完成，则，get()会使当前线程阻塞
	 * @param <T>  任务返回的结果类型
	 * @param task 执行的任务
	 * @return 异步结果（{@link Future}）
	 */
	public <T> Future<T> submit(Callable<T> task) {
		return threadPool.submit(task);
	}

	/**
	 * 优雅关闭
	 */
	protected synchronized void shutdown() {
		if (shutdownFlag.get()) {
			return;
		}
		shutdownFlag.set(true);
		threadPool.shutdown();
		try {
			if (!threadPool.awaitTermination(30, TimeUnit.SECONDS)) {
				threadPool.shutdownNow();
				if (!threadPool.awaitTermination(30, TimeUnit.SECONDS)) {
					logger.error("GlobalThreadPool did not terminate");
				}
			}
		} catch (InterruptedException e) {
			threadPool.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}

	// ==============================InnerClass=======================================
	/** 全局池工作线程未捕获异常处理器：记录到库内 Logger，便于问题溯源 */
	private static class GlobalPoolExceptionHandler implements Thread.UncaughtExceptionHandler {
		private final Logger logger = Logger.getLogger(GlobalPoolExceptionHandler.class);

		@Override
		public void uncaughtException(Thread t, Throwable e) {
			logger.error("Uncaught exception in global-pool thread [" + t.getName() + "]", e);
		}
	}

}
