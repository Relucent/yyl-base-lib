package com.github.relucent.base.common.function;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.github.relucent.base.common.exception.ExceptionUtil;

/**
 * 方法包装工具类
 */
public class FunctionUtil {

    /**
     * 将 ThrowingFunction 包装为标准 Function，捕获异常并转为 RuntimeException
     * @param <T>      入参类型
     * @param <R>      出参类型
     * @param function 支持抛出异常的Function
     * @return 标准Function
     */
    public static <T, R> Function<T, R> unchecked(ThrowingFunction<T, R> function) {
        return t -> {
            try {
                return function.apply(t);
            } catch (Exception e) {
                throw ExceptionUtil.propagate(e);
            }
        };
    }

    /**
     * 将 ThrowingConsumer 包装为标准 Consumer，捕获异常并转为 RuntimeException
     * @param <T>      参数类型
     * @param consumer 支持抛出异常的Consumer
     * @return 标准 Consumer
     */
    public static <T> Consumer<T> uncheckedConsumer(ThrowingConsumer<T> consumer) {
        return t -> {
            try {
                consumer.accept(t);
            } catch (Exception e) {
                throw ExceptionUtil.propagate(e);
            }
        };
    }

    /**
     * 将 ThrowingSupplier 包装为标准 Supplier，捕获异常并转为 RuntimeException
     * @param <T>      Supplier 提供类型
     * @param supplier 支持抛出异常的Supplier
     * @return 标准Supplier
     */
    public static <T> Supplier<T> uncheckedSupplier(ThrowingSupplier<T> supplier) {
        return () -> {
            try {
                return supplier.get();
            } catch (Exception e) {
                throw ExceptionUtil.propagate(e);
            }
        };
    }

    /**
     * 将 ThrowingRunnable 包装为标准 Runnable，捕获异常并转为 RuntimeException
     * @param runnable 支持抛出异常的Runnable
     * @return 标准Runnable
     */
    public static Runnable uncheckedRunnable(ThrowingRunnable runnable) {
        return () -> {
            try {
                runnable.run();
            } catch (Exception e) {
                throw ExceptionUtil.propagate(e);
            }
        };
    }

    /**
     * 工具类私有构造
     */
    protected FunctionUtil() {
        /* Ignore */
    }
}
