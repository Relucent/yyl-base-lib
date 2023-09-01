package com.github.relucent.base.common.reflect;

import java.lang.reflect.Type;

/**
 * 包含与原始类型及其相关的静态实用程序方法相应的包装器类型。
 */
public class Primitives {

    private Primitives() {
    }

    /**
     * 判断类型是否是原始类型
     * @see java.lang.Boolean#TYPE
     * @see java.lang.Character#TYPE
     * @see java.lang.Byte#TYPE
     * @see java.lang.Short#TYPE
     * @see java.lang.Integer#TYPE
     * @see java.lang.Long#TYPE
     * @see java.lang.Float#TYPE
     * @see java.lang.Double#TYPE
     * @see java.lang.Void#TYPE
     * @param type 类型
     * @return 如果此类型是原始类型，则返回{@code true}
     */
    public static boolean isPrimitive(Type type) {
        return type instanceof Class<?> && ((Class<?>) type).isPrimitive();
    }

    /**
     * 判断类型是否是原始类型的包装类型
     * @param type 类型
     * @return 如果{@code type} 是九个原始类型任意一个的包装类型，则返回 {@code true}
     * @see Class#isPrimitive
     */
    public static boolean isWrapperType(Type type) {
        return type == Integer.class//
                || type == Float.class//
                || type == Byte.class//
                || type == Double.class//
                || type == Long.class//
                || type == Character.class//
                || type == Boolean.class//
                || type == Short.class//
                || type == Void.class;//
    }

    /**
     * 包装类型，如果{@code type} 是原始类型，则返回其对应的包装类型；否则返回{@code type}本身。
     * 
     * <pre>
     *     wrap(int.class) == Integer.class
     *     wrap(Integer.class) == Integer.class
     *     wrap(String.class) == String.class
     * </pre>
     * 
     * @param type 类型
     * @return 如果{@code type} 是原始类型，则返回其对应的包装类型；否则返回{@code type}本身。
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> wrap(Class<T> type) {
        if (type == int.class) {
            return (Class<T>) Integer.class;
        }
        if (type == float.class) {
            return (Class<T>) Float.class;
        }
        if (type == byte.class) {
            return (Class<T>) Byte.class;
        }
        if (type == double.class) {
            return (Class<T>) Double.class;
        }
        if (type == long.class) {
            return (Class<T>) Long.class;
        }
        if (type == char.class) {
            return (Class<T>) Character.class;
        }
        if (type == boolean.class) {
            return (Class<T>) Boolean.class;
        }
        if (type == short.class) {
            return (Class<T>) Short.class;
        }
        if (type == void.class) {
            return (Class<T>) Void.class;
        }
        return type;
    }

    /**
     * Returns the corresponding primitive type of {@code type} if it is a wrapper type; otherwise returns {@code type} itself. Idempotent.
     * 
     * <pre>
     *     unwrap(Integer.class) == int.class
     *     unwrap(int.class) == int.class
     *     unwrap(String.class) == String.class
     * </pre>
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> unwrap(Class<T> type) {
        if (type == Integer.class)
            return (Class<T>) int.class;
        if (type == Float.class)
            return (Class<T>) float.class;
        if (type == Byte.class)
            return (Class<T>) byte.class;
        if (type == Double.class)
            return (Class<T>) double.class;
        if (type == Long.class)
            return (Class<T>) long.class;
        if (type == Character.class)
            return (Class<T>) char.class;
        if (type == Boolean.class)
            return (Class<T>) boolean.class;
        if (type == Short.class)
            return (Class<T>) short.class;
        if (type == Void.class)
            return (Class<T>) void.class;
        return type;
    }
}
