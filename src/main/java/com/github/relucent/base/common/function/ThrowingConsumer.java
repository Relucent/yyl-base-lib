package com.github.relucent.base.common.function;

/**
 * 支持抛异常的 Consumer 接口
 * @param <T> 参数类型
 */
@FunctionalInterface
public interface ThrowingConsumer<T> {
    /**
     * 执行操作
     * @param t 参数
     * @throws Exception 执行出现异常
     */
    void accept(T t) throws Exception;
}