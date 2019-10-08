package com.github.relucent.base.util.exception;

/**
 * 通用异常类
 * @see RuntimeException
 * @author YYL
 */
@SuppressWarnings("serial")
public class GeneralException extends RuntimeException {

    /** 异常类型枚举 */
    private final ErrorType type;
    /** 异常的详细信息 */
    private final String message;

    /**
     * 构造函数
     * @param type 异常类型
     * @param message 异常的详细信息， 可以使用{@link #getMessage()} 方法获取
     */
    public GeneralException(ErrorType type, String message) {
        super(message);
        this.message = message;
        this.type = type;
    }

    /**
     * 构造函数
     * @param type 异常类型， 可以使用{@link #getType()} 方法获取
     * @param message 异常的详细信息， 可以使用{@link #getMessage()} 方法获取
     * @param cause 原因异常，可以通过{@link #getCause()} 方法获取
     */
    public GeneralException(ErrorType type, String message, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.type = type;
    }

    /**
     * 返回此异常的详细消息字符串
     * @return 异常的详细消息字符串
     */
    public String getMessage() {
        return message;
    }

    /**
     * 返回此异常的异常类型
     * @return 异常类型
     */
    public ErrorType getType() {
        return type;
    }
}

