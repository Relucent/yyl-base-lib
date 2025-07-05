package com.github.relucent.base.common.lang;

import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 工厂类，用于生成函数式接口的“无操作（No-op）”实例。<br>
 * 主要用途：<br>
 * 1、提供常用函数式接口的内置 No-op 实例，如 Runnable、Callable、Supplier、Function、Consumer 等<br>
 * 2、支持任意自定义函数式接口，通过动态代理（Proxy）生成 No-op 实例<br>
 * 3、在测试或默认对象填充场景下，可以安全使用，无需实现具体逻辑<br>
 * 示例：
 * 
 * <pre>
 * Runnable r = NoOpFunctionFactory.getNoOpInstance(Runnable.class); // 内置实例
 * MyFunctionalInterface fn = NoOpFunctionFactory.getNoOpInstance(MyFunctionalInterface.class); // 动态 Proxy
 * </pre>
 * <p>
 * <li>只有函数式接口（单抽象方法接口）才会生成 No-op 实例，非函数式接口返回 null。
 */
class NoOpFunctionFactory {

    /** 内置 No-op 实例 */
    private static final Map<Class<?>, Object> BUILTIN_NOOPS = new HashMap<>();
    static {
        BUILTIN_NOOPS.put(Runnable.class, new NoOpRunnable());
        BUILTIN_NOOPS.put(Callable.class, new NoOpCallable<>());
        BUILTIN_NOOPS.put(Supplier.class, new NoOpSupplier<>());
        BUILTIN_NOOPS.put(Function.class, new NoOpFunction<>());
        BUILTIN_NOOPS.put(Consumer.class, new NoOpConsumer<>());
    }

    /**
     * 根据函数式接口类型获取 No-op 实例。 <br>
     * 1. 如果是内置接口，直接返回内置 No-op 实例 <br>
     * 2. 如果是函数式接口但不在内置列表，动态生成 Proxy 实例 <br>
     * 3. 如果无法处理，返回 null<br>
     * @param <T> 函数实例类型
     * @param fi  函数类型
     * @return 函数实现类型
     */
    @SuppressWarnings("unchecked")
    public static <T> T getNoOpInstance(Class<T> fi) {
        if (!isFunctionalInterface(fi)) {
            return null;
        }

        // 1. 内置 No-op 实例
        for (Map.Entry<Class<?>, Object> entry : BUILTIN_NOOPS.entrySet()) {
            if (entry.getKey().isAssignableFrom(fi)) {
                return (T) entry.getValue();
            }
        }

        // 2. 动态生成 Proxy
        return createNoOpProxy(fi);
    }

    /**
     * 判断给定接口是否为函数式接口（Functional Interface）
     * @param type 对象类型
     * @return 判断是否是
     */
    public static boolean isFunctionalInterface(Class<?> type) {
        // 1. 必须是接口
        if (type == null || !type.isInterface()) {
            return false;
        }

        int abstractMethodCount = 0;

        // 2. 遍历所有方法
        for (java.lang.reflect.Method m : type.getMethods()) {
            // 忽略继承自 Object 的方法
            if (m.getDeclaringClass() == Object.class)
                continue;

            // 统计抽象方法
            if (Modifier.isAbstract(m.getModifiers())) {
                abstractMethodCount++;
            }
        }

        // 3. 抽象方法数量正好为 1，则为函数式接口
        return abstractMethodCount == 1;
    }

    /**
     * 动态生成 Proxy 实例
     * @param <T> 函数实例类型
     * @param fi  函数类型
     * @return 函数实现类型
     */
    @SuppressWarnings("unchecked")
    private static <T> T createNoOpProxy(Class<T> fi) {
        return (T) Proxy.newProxyInstance(fi.getClassLoader(), new Class[] { fi }, (proxy, method, args) -> {
            Class<?> returnType = method.getReturnType();
            if (returnType.isPrimitive()) {
                return ClassUtil.getDefaultValue(returnType);
            }
            return null;
        });
    }

    // ==============================InnerClass=======================================
    public static class NoOpRunnable implements Runnable {
        public void run() {
        }
    }

    public static class NoOpCallable<V> implements Callable<V> {
        public V call() {
            return null;
        }
    }

    public static class NoOpSupplier<T> implements Supplier<T> {
        public T get() {
            return null;
        }
    }

    public static class NoOpFunction<T, R> implements Function<T, R> {
        public R apply(T t) {
            return null;
        }
    }

    public static class NoOpConsumer<T> implements Consumer<T> {
        public void accept(T t) {
        }
    }
}
