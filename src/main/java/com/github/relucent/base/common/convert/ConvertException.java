package com.github.relucent.base.common.convert;

/**
 * 类型转换异常
 */
public class ConvertException extends RuntimeException {

	/** 序列化版本号 */
	private static final long serialVersionUID = 1L;

	/**
	 * 构造转换异常
	 * @param message 异常信息
	 */
	public ConvertException(String message) {
		super(message);
	}

	/**
	 * 构造转换异常
	 * @param cause 原始异常
	 */
	public ConvertException(Exception cause) {
		super(cause);
	}

	/**
	 * 构造转换异常
	 * @param message 异常信息
	 * @param cause   原始异常
	 */
	public ConvertException(String message, Exception cause) {
		super(message, cause);
	}

	/**
	 * 将异常包装为转换异常；如果参数本身已经是转换异常，则原样返回
	 * @param e 原始异常
	 * @return 转换异常
	 */
	public static ConvertException wrap(Exception e) {
		return e instanceof ConvertException ? (ConvertException) e : new ConvertException(e.getMessage(), e);
	}
}
