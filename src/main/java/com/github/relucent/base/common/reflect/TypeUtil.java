package com.github.relucent.base.common.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.github.relucent.base.common.lang.ArrayUtil;

/**
 * 类型工具类
 */
public class TypeUtil {

    TypeUtil() {
    }

    /**
     * 获得给定类型的第一个泛型参数
     * @param type 被检查的类型
     * @return {@link Type}，可能为{@code null}
     */
    public static Type getTypeArgument(Type type) {
        return getTypeArgument(type, 0);
    }

    /**
     * 获得给定类型的泛型参数
     * @param type 被检查的类型
     * @param index 泛型参数索引，从0开始。
     * @return 泛型参数的类型
     */
    public static Type getTypeArgument(Type type, int index) {
        final Type[] typeArguments = getTypeArguments(type);
        if (typeArguments != null && index < typeArguments.length) {
            return typeArguments[index];
        }
        return null;
    }

    /**
     * 获得指定类型中所有泛型参数类型
     * @param type 指定类型
     * @return 所有泛型参数类型
     */
    public static Type[] getTypeArguments(Type type) {
        final ParameterizedType parameterizedType = getParameterizedType(type);
        return (null == parameterizedType) ? null : parameterizedType.getActualTypeArguments();
    }

    /**
     * 获得类型{@link Type}的参数化类型{@link ParameterizedType}<br>
     * {@link ParameterizedType}用于获取当前类或父类中泛型参数化后的类型<br>
     * 一般用于获取泛型参数具体的参数类型，例如：
     * 
     * <pre>
     * class A&lt;T&gt;
     * class B extends A&lt;String&gt;
     * </pre>
     * 
     * @param type {@link Type}
     * @return {@link ParameterizedType}
     */
    public static ParameterizedType getParameterizedType(Type type) {
        if (type == null) {
            return null;
        }
        if (type instanceof ParameterizedType) {
            return (ParameterizedType) type;
        }
        if (type instanceof Class) {
            final Class<?> clazz = (Class<?>) type;
            Type genericSuper = clazz.getGenericSuperclass();
            // 如果类没有父类，默认取第一个实现接口的泛型Type
            if (genericSuper == null || Object.class.equals(genericSuper)) {
                final Type[] genericInterfaces = clazz.getGenericInterfaces();
                if (ArrayUtil.isNotEmpty(genericInterfaces)) {
                    genericSuper = genericInterfaces[0];
                }
            }
            return getParameterizedType(genericSuper);
        }
        return null;
    }
}
