package com.github.relucent.base.common.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 异常工具类
 * @author YYL
 */
public class ExceptionHelper {

	/**
	 * 创建通用异常
	 * @param cause 源异常
	 * @return 通用异常
	 */
	public static GeneralException propagate(Throwable cause) {
		if (cause instanceof GeneralException) {
			return (GeneralException) cause;
		}
		return new GeneralException("#", cause);
	}

	/**
	 * 创建未捕获异常
	 * @param message 异常信息
	 * @param cause 源异常
	 * @return 未捕获异常
	 */
	public static GeneralException propagate(String message, Throwable cause) {
		return new GeneralException(message, cause);
	}

	/**
	 * 创建提示异常
	 * @param message 提示消息
	 * @return 提示异常
	 */
	public static PromptException prompt(String message) {
		return new PromptException(message);
	}

	/**
	 * 创建提示异常
	 * @param code 异常编码
	 * @param message 提示消息
	 * @return 提示异常
	 */
	public static PromptException prompt(Integer code, String message) {
		return new PromptException(message);
	}

	/**
	 * 创建通用异常
	 * @param message 异常信息
	 * @return 通用异常
	 */
	public static GeneralException error(String message) {
		return new GeneralException(message);
	}

	/**
	 * 创建通用异常
	 * @param code 异常编码
	 * @param message 异常信息
	 * @return 通用异常
	 */
	public static GeneralException error(Integer code, String message) {
		return new GeneralException(code, message);
	}

	/**
	 * 输出异常堆栈字符串
	 * @param throwable 异常
	 * @return 异常堆栈字符串
	 */
	public static String getStackTraceAsString(Throwable throwable) {
		StringWriter stringWriter = new StringWriter();
		throwable.printStackTrace(new PrintWriter(stringWriter));
		return stringWriter.toString();
	}
}