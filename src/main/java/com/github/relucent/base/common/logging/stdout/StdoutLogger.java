package com.github.relucent.base.common.logging.stdout;

import com.github.relucent.base.common.logging.AbstractLogger;

public class StdoutLogger extends AbstractLogger {

	@Override
	public boolean isTraceEnabled() {
		return true;
	}

	@Override
	public boolean isDebugEnabled() {
		return true;
	}

	@Override
	public boolean isInfoEnabled() {
		return true;
	}

	@Override
	public boolean isWarnEnabled() {
		return true;
	}

	@Override
	public boolean isErrorEnabled() {
		return true;
	}

	@Override
	public boolean isFatalEnabled() {
		return true;
	}

	@Override
	public void trace(String message) {
		System.out.println(message);
	}

	@Override
	public void debug(String message) {
		System.out.println(message);
	}

	@Override
	public void info(String message) {
		System.out.println(message);
	}

	@Override
	public void warn(String message) {
		System.out.println(message);
	}

	@Override
	public void error(String message) {
		System.out.println(message);
	}

	@Override
	public void fatal(String message) {
		System.out.println(message);
	}

	@Override
	public void trace(String message, Throwable throwable) {
		System.out.println(message);
		throwable.printStackTrace(System.out);
	}

	@Override
	public void debug(String message, Throwable throwable) {
		System.out.println(message);
		throwable.printStackTrace(System.out);
	}

	@Override
	public void info(String message, Throwable throwable) {
		System.out.println(message);
		throwable.printStackTrace(System.out);
	}

	@Override
	public void warn(String message, Throwable throwable) {
		System.out.println(message);
		throwable.printStackTrace(System.out);
	}

	@Override
	public void error(String message, Throwable throwable) {
		System.out.println(message);
		throwable.printStackTrace(System.out);
	}

	@Override
	public void fatal(String message, Throwable throwable) {
		System.out.println(message);
		throwable.printStackTrace(System.out);
	}
}
