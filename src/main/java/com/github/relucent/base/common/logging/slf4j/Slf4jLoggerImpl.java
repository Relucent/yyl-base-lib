package com.github.relucent.base.common.logging.slf4j;

import com.github.relucent.base.common.logging.AbstractLogger;

/**
 * Slf4jLogger
 */
public class Slf4jLoggerImpl extends AbstractLogger {

	private final org.slf4j.Logger logger;

	Slf4jLoggerImpl(org.slf4j.Logger logger) {
		this.logger = logger;
	}

	@Override
	public boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}

	@Override
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	@Override
	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	@Override
	public boolean isWarnEnabled() {
		return logger.isWarnEnabled();
	}

	@Override
	public boolean isErrorEnabled() {
		return logger.isErrorEnabled();
	}

	@Override
	public boolean isFatalEnabled() {
		return logger.isErrorEnabled();
	}

	@Override
	public void trace(String message) {
		logger.trace(message);
	}

	@Override
	public void debug(String message) {
		logger.debug(message);
	}

	@Override
	public void info(String message) {
		logger.info(message);
	}

	@Override
	public void warn(String message) {
		logger.warn(message);
	}

	@Override
	public void error(String message) {
		logger.error(message);
	}

	@Override
	public void fatal(String message) {
		logger.error(message);
	}

	@Override
	public void trace(String message, Throwable throwable) {
		logger.trace(message, throwable);
	}

	@Override
	public void debug(String message, Throwable throwable) {
		logger.debug(message, throwable);
	}

	@Override
	public void info(String message, Throwable throwable) {
		logger.info(message, throwable);
	}

	@Override
	public void warn(String message, Throwable throwable) {
		logger.warn(message, throwable);
	}

	@Override
	public void error(String message, Throwable throwable) {
		logger.error(message, throwable);
	}

	@Override
	public void fatal(String message, Throwable throwable) {
		logger.error(message, throwable);
	}

	@Override
	public void trace(String format, Object... args) {
		if (isTraceEnabled()) {
			logger.trace(format, args);
		}
	}

	@Override
	public void debug(String format, Object... args) {
		if (isDebugEnabled()) {
			logger.debug(format, args);
		}
	}

	@Override
	public void info(String format, Object... args) {
		if (isInfoEnabled()) {
			logger.info(format, args);
		}
	}

	@Override
	public void warn(String format, Object... args) {
		if (isWarnEnabled()) {
			logger.warn(format, args);
		}
	}

	@Override
	public void error(String format, Object... args) {
		if (isErrorEnabled()) {
			logger.error(format, args);
		}
	}

	@Override
	public void fatal(String format, Object... args) {
		if (isFatalEnabled()) {
			logger.error(format, args);
		}
	}
}
