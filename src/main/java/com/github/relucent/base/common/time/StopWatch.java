package com.github.relucent.base.common.time;

import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.github.relucent.base.common.constant.StringConstant;

/**
 * 简单的秒表，允许对许多任务进行计时，显示每个指定任务的总运行时间和运行时间。<br>
 * 比如：可以记录多段代码耗时时间，然后一次性打印（StopWatch提供了一个prettyString()函数用于按照指定格式打印出耗时）<br>
 * 注：<br>
 * 1. 此对象不是为线程安全而设计的。 <br>
 * 2. 此类通常用于在概念验证工作和开发过程中验证性能，而不是作为生产应用程序的一部分。<br>
 * 
 * <pre>
 * StopWatch stopWatch = new StopWatch("任务名称");
 *
 * // 任务1
 * stopWatch.start("任务01");
 * Thread.sleep(1000);
 * stopWatch.stop();
 *
 * // 任务2
 * stopWatch.start("任务02");
 * Thread.sleep(2000);
 * stopWatch.stop();
 *
 * // 打印出耗时
 * Console.log(sw.prettyPrint());
 *
 * </pre>
 */
public class StopWatch {

	// ==============================Fields===========================================
	/** 秒表唯一标识，用于多个秒表对象的区分 */
	private final String id;
	/** 保留任务列表 */
	private boolean keepTaskList = true;
	/** 任务信息列表 */
	private final List<TaskInfo> taskList = new LinkedList<>();
	/** 当前任务的开始时间 */
	private long startTimeNanos;
	/** 当前任务的名称 */
	private String currentTaskName;
	/** 最后一次任务信息 */
	private TaskInfo lastTaskInfo;
	/** 任务个数 */
	private int taskCount;
	/** 总运行时间 */
	private long totalTimeNanos;

	// ==============================Constructors=====================================
	/**
	 * 构造函数， 不会启动任何任务
	 */
	public StopWatch() {
		this(StringConstant.EMPTY);
	}

	/**
	 * 用给定的ID构造一个新的秒表对象， 使用ID可以方便的区分多个秒表的输出。。
	 * @param id 秒表的标识符
	 */
	public StopWatch(String id) {
		this.id = id;
	}

	// ==============================Methods==========================================
	/**
	 * 获取秒表标识
	 * @return 秒表ID
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * 设置是否在停止后保留任务，{@code false} 表示停止运行后不保留任务。 默认是{@code true}。
	 * @param keepTaskList 是否在停止后保留任务
	 */
	public void setKeepTaskList(boolean keepTaskList) {
		this.keepTaskList = keepTaskList;
	}

	/**
	 * 开始一项未命名的任务<br>
	 * 如果调用 {@link #stop()} 或计时方法而不首先调用此方法，则结果是未定义的。<br>
	 * @see #start(String)
	 * @see #stop()
	 */
	public void start() throws IllegalStateException {
		start(StringConstant.EMPTY);
	}

	/**
	 * 开始一个命名任务<br>
	 * 如果调用 {@link #stop()} 或计时方法而不首先调用此方法，则结果是未定义的。<br>
	 * @param taskName 任务名称
	 * @see #start()
	 * @see #stop()
	 */
	public void start(String taskName) throws IllegalStateException {
		if (this.currentTaskName != null) {
			throw new IllegalStateException("Can't start StopWatch: it's already running");
		}
		this.currentTaskName = taskName;
		this.startTimeNanos = System.nanoTime();
	}

	/**
	 * 停止当前任务<br>
	 * 如果调用计时方法时没有调用至少一对 {@code start()} / {@code stop()} 方法，则结果是未定义的。<br>
	 * @see #start()
	 * @see #start(String)
	 * @throws IllegalStateException 任务没有开始
	 */
	public void stop() throws IllegalStateException {
		if (this.currentTaskName == null) {
			throw new IllegalStateException("Can't stop StopWatch: it's not running");
		}
		long lastTime = System.nanoTime() - this.startTimeNanos;
		this.totalTimeNanos += lastTime;
		this.lastTaskInfo = new TaskInfo(this.currentTaskName, lastTime);
		if (this.keepTaskList) {
			this.taskList.add(this.lastTaskInfo);
		}
		++this.taskCount;
		this.currentTaskName = null;
	}

	/**
	 * 确定此秒表当前是否正在运行
	 * @return 秒表当前是否正在运行
	 * @see #currentTaskName()
	 */
	public boolean isRunning() {
		return (this.currentTaskName != null);
	}

	/**
	 * 获取当前任务名
	 * @return 当前任务名
	 * @see #isRunning()
	 */
	public String currentTaskName() {
		return this.currentTaskName;
	}

	/**
	 * 获取最后任务的花费时间（纳秒）
	 * @return 任务的花费时间（纳秒）
	 * @see #getLastTaskTimeMillis()
	 * @throws IllegalStateException 无任务
	 */
	public long getLastTaskTimeNanos() throws IllegalStateException {
		if (this.lastTaskInfo == null) {
			throw new IllegalStateException("No tasks run: can't get last task interval");
		}
		return this.lastTaskInfo.getTimeNanos();
	}

	/**
	 * 获取最后任务的花费时间（毫秒）
	 * @return 任务的花费时间（毫秒）
	 * @see #getLastTaskTimeNanos()
	 * @throws IllegalStateException 无任务
	 */
	public long getLastTaskTimeMillis() throws IllegalStateException {
		if (this.lastTaskInfo == null) {
			throw new IllegalStateException("No tasks run: can't get last task interval");
		}
		return this.lastTaskInfo.getTimeMillis();
	}

	/**
	 * 获取最后的任务名
	 * @return 任务名
	 * @throws IllegalStateException 无任务
	 */
	public String getLastTaskName() throws IllegalStateException {
		if (this.lastTaskInfo == null) {
			throw new IllegalStateException("No tasks run: can't get last task name");
		}
		return this.lastTaskInfo.getTaskName();
	}

	/**
	 * 获取最后的任务信息
	 * @return 任务信息，包括任务名和花费时间
	 * @throws IllegalStateException 无任务
	 */
	public TaskInfo getLastTaskInfo() throws IllegalStateException {
		if (this.lastTaskInfo == null) {
			throw new IllegalStateException("No tasks run: can't get last task info");
		}
		return this.lastTaskInfo;
	}

	/**
	 * 获取所有任务的总花费时间（纳秒）
	 * @return 所有任务的总花费时间（纳秒）
	 * @see #getTotalTimeMillis()
	 * @see #getTotalTimeSeconds()
	 */
	public long getTotalTimeNanos() {
		return this.totalTimeNanos;
	}

	/**
	 * 获取所有任务的总花费时间（毫秒）
	 * @return 所有任务的总花费时间（毫秒）
	 * @see #getTotalTimeNanos()
	 * @see #getTotalTimeSeconds()
	 */
	public long getTotalTimeMillis() {
		return nanosToMillis(this.totalTimeNanos);
	}

	/**
	 * 获取所有任务的总花费时间（秒）
	 * @return 所有任务的总花费时间（秒）
	 * @see #getTotalTimeNanos()
	 * @see #getTotalTimeMillis()
	 */
	public double getTotalTimeSeconds() {
		return nanosToSeconds(this.totalTimeNanos);
	}

	/**
	 * 获取任务数
	 * @return 任务数
	 */
	public int getTaskCount() {
		return this.taskCount;
	}

	/**
	 * 获取任务信息列表
	 * @return 任务信息列表
	 */
	public TaskInfo[] getTaskInfo() {
		if (!this.keepTaskList) {
			throw new UnsupportedOperationException("Task info is not being kept!");
		}
		return this.taskList.toArray(new TaskInfo[0]);
	}

	/**
	 * 获取总运行时间的简短描述
	 * @return 总运行时间的简短描述
	 */
	public String shortSummary() {
		return "StopWatch '" + getId() + "': running time = " + getTotalTimeNanos() + " ns";
	}

	/**
	 * 生成一个字符串，描述所有任务花费时间表<br>
	 * 对于自定义报告，请调用{@link#getTaskInfo()}并直接使用任务信息。
	 * @return 任务花费时间表
	 */
	public String prettyPrint() {
		StringBuilder sb = new StringBuilder(shortSummary());
		sb.append('\n');
		if (!this.keepTaskList) {
			sb.append("No task info kept");
		} else {
			sb.append("---------------------------------------------\n");
			sb.append("ns         %     Task name\n");
			sb.append("---------------------------------------------\n");
			NumberFormat nf = NumberFormat.getNumberInstance();
			nf.setMinimumIntegerDigits(9);
			nf.setGroupingUsed(false);
			NumberFormat pf = NumberFormat.getPercentInstance();
			pf.setMinimumIntegerDigits(3);
			pf.setGroupingUsed(false);
			for (TaskInfo task : getTaskInfo()) {
				sb.append(nf.format(task.getTimeNanos())).append("  ");
				sb.append(pf.format((double) task.getTimeNanos() / getTotalTimeNanos())).append("  ");
				sb.append(task.getTaskName()).append("\n");
			}
		}
		return sb.toString();
	}

	/**
	 * 生成描述所有执行任务的信息字符串
	 * @return 秒表的字符串
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(shortSummary());
		if (this.keepTaskList) {
			for (TaskInfo task : getTaskInfo()) {
				sb.append("; [").append(task.getTaskName()).append("] took ").append(task.getTimeNanos()).append(" ns");
				long percent = Math.round(100.0 * task.getTimeNanos() / getTotalTimeNanos());
				sb.append(" = ").append(percent).append("%");
			}
		} else {
			sb.append("; no task info kept");
		}
		return sb.toString();
	}

	/**
	 * 纳秒转毫秒
	 * @param duration 时长(纳秒)
	 * @return 毫秒
	 */
	private static long nanosToMillis(long duration) {
		return TimeUnit.NANOSECONDS.toMillis(duration);
	}

	/**
	 * 纳秒转秒，保留小数
	 * @param duration 时长(纳秒)
	 * @return 秒
	 */
	private static double nanosToSeconds(long duration) {
		return duration / 1_000_000_000.0;
	}

	/**
	 * 嵌套类，用于保存有关在{@code StopWatch}中执行的一个任务的数据
	 */
	public static final class TaskInfo {

		private final String taskName;
		private final long timeNanos;

		/**
		 * 构造
		 * @param taskName 任务名称
		 * @param timeNanos 花费时间（纳秒）
		 */
		TaskInfo(String taskName, long timeNanos) {
			this.taskName = taskName;
			this.timeNanos = timeNanos;
		}

		/**
		 * 获取任务名
		 * @return 任务名
		 */
		public String getTaskName() {
			return this.taskName;
		}

		/**
		 * 获取任务花费时间（单位：纳秒）
		 * @return 任务花费时间（单位：纳秒）
		 * @see #getTimeMillis()
		 * @see #getTimeSeconds()
		 */
		public long getTimeNanos() {
			return this.timeNanos;
		}

		/**
		 * 获取任务花费时间（单位：毫秒）
		 * @return 任务花费时间（单位：毫秒）
		 * @see #getTimeNanos()
		 * @see #getTimeSeconds()
		 */
		public long getTimeMillis() {
			return nanosToMillis(this.timeNanos);
		}

		/**
		 * 获取任务花费时间（单位：秒）
		 * @return 任务花费时间（单位：秒）
		 * @see #getTimeMillis()
		 * @see #getTimeNanos()
		 */
		public double getTimeSeconds() {
			return nanosToSeconds(this.timeNanos);
		}
	}
}
