package com.github.relucent.base.common.logging.log4j;

import org.apache.log4j.Level;

import com.github.relucent.base.common.logging.AbstractLogger;

/**
 * Log4jLogger
 */
public class Log4jLogger extends AbstractLogger {

	private static final String CALLER_FQCN = Log4jLogger.class.getName();
	private org.apache.log4j.Logger logger;

	public Log4jLogger(Class<?> clazz) {
		this.logger = org.apache.log4j.Logger.getLogger(clazz);
	}

	public Log4jLogger(String name) {
		this.logger = org.apache.log4j.Logger.getLogger(name);
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
		return logger.isEnabledFor(Level.WARN);
	}

	@Override
	public boolean isErrorEnabled() {
		return logger.isEnabledFor(Level.ERROR);
	}

	@Override
	public boolean isFatalEnabled() {
		return logger.isEnabledFor(Level.FATAL);
	}

	@Override
	public void trace(String message) {
		logger.log(CALLER_FQCN, Level.TRACE, message, null);
	}

	@Override
	public void debug(String message) {
		logger.log(CALLER_FQCN, Level.DEBUG, message, null);
	}

	@Override
	public void info(String message) {
		logger.log(CALLER_FQCN, Level.INFO, message, null);
	}

	@Override
	public void warn(String message) {
		logger.log(CALLER_FQCN, Level.WARN, message, null);
	}

	@Override
	public void error(String message) {
		logger.log(CALLER_FQCN, Level.ERROR, message, null);
	}

	@Override
	public void fatal(String message) {
		logger.log(CALLER_FQCN, Level.FATAL, message, null);
	}

	@Override
	public void trace(String message, Throwable throwable) {
		logger.log(CALLER_FQCN, Level.TRACE, message, throwable);
	}

	@Override
	public void debug(String message, Throwable throwable) {
		logger.log(CALLER_FQCN, Level.DEBUG, message, throwable);
	}

	@Override
	public void info(String message, Throwable throwable) {
		logger.log(CALLER_FQCN, Level.INFO, message, throwable);
	}

	@Override
	public void warn(String message, Throwable throwable) {
		logger.log(CALLER_FQCN, Level.WARN, message, throwable);
	}

	@Override
	public void error(String message, Throwable throwable) {
		logger.log(CALLER_FQCN, Level.ERROR, message, throwable);
	}

	@Override
	public void fatal(String message, Throwable throwable) {
		logger.log(CALLER_FQCN, Level.FATAL, message, throwable);
	}
}
