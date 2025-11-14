package com.github.relucent.base.common.concurrent;

/**
 * 运行时中断异常<br>
 * 当线程在活动之前或活动期间处于正在等待、休眠或占用状态且该线程被中断时，抛出该异常。<br>
 * @see InterruptedException
 */
@SuppressWarnings("serial")
public class InterruptedRuntimeException extends RuntimeException {

    public InterruptedRuntimeException() {
    }

    public InterruptedRuntimeException(String message) {
        super(message);
    }

    public InterruptedRuntimeException(InterruptedException cause) {
        super(cause.getMessage(), cause);
    }
}
