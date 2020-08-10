package com.github.relucent.base.common.logging;

/**
 * 日志工具抽象类<br>
 */
public abstract class AbstractLogger implements Logger {

	public void trace(String format, Object... args) {
		if (isTraceEnabled()) {
			LoggerMessageTuple tuple = LoggerMessageFormatter.arrayFormat(format, args);
			String message = tuple.getMessage();
			Throwable throwable = tuple.getThrowable();
			if (throwable != null) {
				trace(message, throwable);
			} else {
				trace(message);
			}
		}
	}

	public void debug(String format, Object... args) {
		if (isDebugEnabled()) {
			LoggerMessageTuple tuple = LoggerMessageFormatter.arrayFormat(format, args);
			String message = tuple.getMessage();
			Throwable throwable = tuple.getThrowable();
			if (throwable != null) {
				debug(message, throwable);
			} else {
				debug(message);
			}
		}
	}

	public void info(String format, Object... args) {
		if (isInfoEnabled()) {
			LoggerMessageTuple tuple = LoggerMessageFormatter.arrayFormat(format, args);
			String message = tuple.getMessage();
			Throwable throwable = tuple.getThrowable();
			if (throwable != null) {
				info(message, throwable);
			} else {
				info(message);
			}
		}
	}

	public void warn(String format, Object... args) {
		if (isWarnEnabled()) {
			LoggerMessageTuple tuple = LoggerMessageFormatter.arrayFormat(format, args);
			String message = tuple.getMessage();
			Throwable throwable = tuple.getThrowable();
			if (throwable != null) {
				warn(message, throwable);
			} else {
				warn(message);
			}
		}
	}

	public void error(String format, Object... args) {
		if (isErrorEnabled()) {
			LoggerMessageTuple tuple = LoggerMessageFormatter.arrayFormat(format, args);
			String message = tuple.getMessage();
			Throwable throwable = tuple.getThrowable();
			if (throwable != null) {
				error(message, throwable);
			} else {
				error(message);
			}
		}
	}

	public void fatal(String format, Object... args) {
		if (isFatalEnabled()) {
			LoggerMessageTuple tuple = LoggerMessageFormatter.arrayFormat(format, args);
			String message = tuple.getMessage();
			Throwable throwable = tuple.getThrowable();
			if (throwable != null) {
				fatal(message, throwable);
			} else {
				fatal(message);
			}
		}
	}
}
