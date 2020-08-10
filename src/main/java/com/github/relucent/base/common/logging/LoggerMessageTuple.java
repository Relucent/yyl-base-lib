package com.github.relucent.base.common.logging;

public class LoggerMessageTuple {

	private final String message;
	private final Throwable throwable;

	public LoggerMessageTuple(String message, Throwable throwable) {
		this.message = message;
		this.throwable = throwable;
	}

	public String getMessage() {
		return message;
	}

	public Throwable getThrowable() {
		return throwable;
	}
}
