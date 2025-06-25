package com.github.relucent.base.common.function;

/**
 * 支持抛异常的 Runnable 接口
 */
@FunctionalInterface
public interface ThrowingRunnable {
    /**
     * 运行方法
     * @throws Exception 运行出现异常
     */
    void run() throws Exception;
}