package com.github.relucent.base.common.convert;

/**
 * 类型转换异常
 */
@SuppressWarnings("serial")
public class ConvertException extends RuntimeException {

    public ConvertException(String message) {
        super(message);
    }

    public ConvertException(Exception cause) {
        super(cause);
    }

    public ConvertException(String message, Exception cause) {
        super(message, cause);
    }

    public static ConvertException wrap(Exception e) {
        return e instanceof ConvertException ? (ConvertException) e : new ConvertException(e.getMessage(), e);
    }
}
