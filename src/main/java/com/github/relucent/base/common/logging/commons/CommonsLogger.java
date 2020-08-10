package com.github.relucent.base.common.logging.commons;

import com.github.relucent.base.common.logging.AbstractLogger;

public class CommonsLogger extends AbstractLogger {

	private final org.apache.commons.logging.Log log;

	public CommonsLogger(Class<?> clazz) {
		this.log = org.apache.commons.logging.LogFactory.getLog(clazz);
	}

	public CommonsLogger(String name) {
		this.log = org.apache.commons.logging.LogFactory.getLog(name);
	}

	@Override
	public boolean isTraceEnabled() {
		return log.isTraceEnabled();
	}

	@Override
	public boolean isDebugEnabled() {
		return log.isDebugEnabled();
	}

	@Override
	public boolean isInfoEnabled() {
		return log.isInfoEnabled();
	}

	@Override
	public boolean isWarnEnabled() {
		return log.isWarnEnabled();
	}

	@Override
	public boolean isErrorEnabled() {
		return log.isErrorEnabled();
	}

	@Override
	public boolean isFatalEnabled() {
		return log.isFatalEnabled();
	}

	@Override
	public void trace(String message) {
		log.trace(message);
	}

	@Override
	public void debug(String message) {
		log.debug(message);
	}

	@Override
	public void info(String message) {
		log.info(message);
	}

	@Override
	public void warn(String message) {
		log.warn(message);
	}

	@Override
	public void error(String message) {
		log.error(message);
	}

	@Override
	public void fatal(String message) {
		log.fatal(message);
	}

	@Override
	public void trace(String message, Throwable throwable) {
		log.trace(message, throwable);
	}

	@Override
	public void debug(String message, Throwable throwable) {
		log.debug(message, throwable);
	}

	@Override
	public void info(String message, Throwable throwable) {
		log.info(message, throwable);
	}

	@Override
	public void warn(String message, Throwable throwable) {
		log.warn(message, throwable);
	}

	@Override
	public void error(String message, Throwable throwable) {
		log.error(message, throwable);
	}

	@Override
	public void fatal(String message, Throwable throwable) {
		log.fatal(message, throwable);
	}
}
