package com.github.relucent.base.common.function;

/**
 * 支持抛异常的 Supplier 接口
 * @param <T> 返回的类型
 */
@FunctionalInterface
public interface ThrowingSupplier<T> {
    /**
     * 获取结果
     * @return 结果
     * @throws Exception 获取结果出现异常
     */
    T get() throws Exception;
}