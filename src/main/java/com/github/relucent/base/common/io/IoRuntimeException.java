package com.github.relucent.base.common.io;

/**
 * IO运行时异常，用于对IOException的包装
 */
@SuppressWarnings("serial")
public class IoRuntimeException extends RuntimeException {

    public IoRuntimeException(String message) {
        super(message);
    }

    public IoRuntimeException(Exception cause) {
        super(cause);
    }

    public IoRuntimeException(String message, Exception cause) {
        super(message, cause);
    }

    public static IoRuntimeException wrap(Exception e) {
        return e instanceof IoRuntimeException ? (IoRuntimeException) e : new IoRuntimeException(e.getMessage(), e);
    }
}
