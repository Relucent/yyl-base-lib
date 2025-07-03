package com.github.relucent.base.common.function;

/**
 * 支持抛异常的 Function 接口
 * @param <T> 入参类型
 * @param <R> 出参类型
 */
@FunctionalInterface
public interface ThrowingFunction<T, R> {
    /**
     * 执行操作
     * @param t 参数
     * @return 执行结果
     * @throws Exception 执行出现异常
     */
    R apply(T t) throws Exception;
}