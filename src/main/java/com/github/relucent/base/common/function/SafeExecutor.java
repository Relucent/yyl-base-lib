package com.github.relucent.base.common.function;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

import com.github.relucent.base.common.exception.ExceptionUtil;
import com.github.relucent.base.common.logging.Logger;

/**
 * 可控异常的执行器
 * @param <T> 函数返回类型
 */
public class SafeExecutor<T> {

    private static final Logger LOGGER = Logger.getLogger(SafeExecutor.class);

    private final Callable<T> action;

    private SafeExecutor(Callable<T> action) {
        this.action = action;
    }

    /**
     * 创建执行器
     * @param <T>    函数返回类型
     * @param action 执行的函数
     * @return 执行器
     */
    public static <T> SafeExecutor<T> of(Callable<T> action) {
        return new SafeExecutor<>(action);
    }

    /**
     * 创建无返回值执行器
     * @param action 执行的函数
     * @return 执行器
     */
    public static SafeExecutor<Void> of(Runnable action) {
        return new SafeExecutor<>(() -> {
            action.run();
            return null;
        });
    }

    /**
     * 执行并忽略异常
     * @return 函数执行结果，异常时返回{@code null}
     */
    public T runIgnoreException() {
        try {
            return action.call();
        } catch (Exception e) {
            LOGGER.warn("SafeExecutor IGNORE: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 执行并自定义处理异常
     * @param handler 异常处理方法
     * @return 函数执行结果，异常时返回{@code null}
     */
    public T runHandleException(Consumer<Exception> handler) {
        try {
            return action.call();
        } catch (Exception e) {
            if (handler != null) {
                handler.accept(e);
            } else {
                LOGGER.warn("SafeExecutor HANDLE: {} {}", e.getMessage(), ExceptionUtil.getStackTraceAsString(e));
            }
            return null;
        }
    }

    /**
     * 执行并抛出异常
     * @return 函数执行结果
     * @throws RuntimeException 函数异常时候抛出
     */
    public T runThrowException() throws RuntimeException {
        try {
            return action.call();
        } catch (Exception e) {
            throw ExceptionUtil.propagate(e);
        }
    }
}