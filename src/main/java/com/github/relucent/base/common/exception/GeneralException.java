package com.github.relucent.base.common.exception;

/**
 * 通用异常类
 * @see RuntimeException
 * @author YYL
 */
@SuppressWarnings("serial")
public class GeneralException extends RuntimeException {

    /** 异常编码 */
    private final Integer code;

    /**
     * 构造函数
     * @param message 异常的详细信息， 可以使用{@link #getMessage()} 方法获取
     */
    public GeneralException(String message) {
        this(0, message);
    }

    /**
     * 构造函数
     * @param message 异常的详细信息， 可以使用{@link #getMessage()} 方法获取
     * @param cause 原因异常，可以通过{@link #getCause()} 方法获取
     */
    public GeneralException(String message, Throwable cause) {
        this(0, message, cause);
    }

    /**
     * 构造函数
     * @param code 异常的编码， 可以使用{@link #getCode()} 方法获取
     * @param message 异常的详细信息， 可以使用{@link #getMessage()} 方法获取
     */
    public GeneralException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 构造函数
     * @param code 异常的编码， 可以使用{@link #getCode()} 方法获取
     * @param message 异常的详细信息， 可以使用{@link #getMessage()} 方法获取
     * @param cause 原因异常，可以通过{@link #getCause()} 方法获取
     */
    public GeneralException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    /**
     * 获得异常的编码
     * @return 异常的编码
     */
    public Integer getCode() {
        return code;
    }
}
