package com.github.relucent.base.common.logging.log4j2;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.spi.ExtendedLoggerWrapper;

import com.github.relucent.base.common.logging.AbstractLogger;

public class Log4j2AbstractLoggerImpl extends AbstractLogger {

	private final ExtendedLoggerWrapper logger;
	private final String fqcn;
	private final Marker marker;

	Log4j2AbstractLoggerImpl(org.apache.logging.log4j.spi.AbstractLogger abstractLogger, String fqcn, Marker marker) {
		this.logger = new ExtendedLoggerWrapper(abstractLogger, abstractLogger.getName(), abstractLogger.getMessageFactory());
		this.fqcn = fqcn;
		this.marker = marker;
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
		return logger.isFatalEnabled();
	}

	@Override
	public void trace(String message) {
		logger.logIfEnabled(fqcn, Level.TRACE, marker, message);
	}

	@Override
	public void debug(String message) {
		logger.logIfEnabled(fqcn, Level.DEBUG, marker, message);
	}

	@Override
	public void info(String message) {
		logger.logIfEnabled(fqcn, Level.INFO, marker, message);
	}

	@Override
	public void warn(String message) {
		logger.logIfEnabled(fqcn, Level.WARN, marker, message);
	}

	@Override
	public void error(String message) {
		logger.logIfEnabled(fqcn, Level.ERROR, marker, message);
	}

	@Override
	public void fatal(String message) {
		logger.logIfEnabled(fqcn, Level.FATAL, marker, message);
	}

	@Override
	public void trace(String message, Throwable throwable) {
		logger.logIfEnabled(fqcn, Level.TRACE, marker, message, throwable);
	}

	@Override
	public void debug(String message, Throwable throwable) {
		logger.logIfEnabled(fqcn, Level.DEBUG, marker, message, throwable);
	}

	@Override
	public void info(String message, Throwable throwable) {
		logger.logIfEnabled(fqcn, Level.INFO, marker, message, throwable);
	}

	@Override
	public void warn(String message, Throwable throwable) {
		logger.logIfEnabled(fqcn, Level.WARN, marker, message, throwable);
	}

	@Override
	public void error(String message, Throwable throwable) {
		logger.logIfEnabled(fqcn, Level.ERROR, marker, message, throwable);
	}

	@Override
	public void fatal(String message, Throwable throwable) {
		logger.logIfEnabled(fqcn, Level.FATAL, marker, message, throwable);
	}
}
