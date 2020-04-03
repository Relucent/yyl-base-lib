package com.github.relucent.base.common.exception;

/**
 * 通用异常类
 * @see RuntimeException
 * @author YYL
 */
@SuppressWarnings("serial")
public class GeneralException extends RuntimeException {

	/**
	 * 构造函数
	 * @param message 异常的详细信息， 可以使用{@link #getMessage()} 方法获取
	 */
	public GeneralException(String message) {
		super(message);
	}

	/**
	 * 构造函数
	 * @param message 异常的详细信息， 可以使用{@link #getMessage()} 方法获取
	 * @param cause 原因异常，可以通过{@link #getCause()} 方法获取
	 */
	public GeneralException(String message, Throwable cause) {
		super(message, cause);
	}
}
