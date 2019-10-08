package com.github.relucent.base.util.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 泛型工具类。
 */
public class GenericsUtil {

    /**
     * 获得定义类时声明父类的第一个泛型参数的类型
     * @param clazz 对象类
     * @return 第一个泛型参数的类型
     */
    public static Class<?> getSuperClassGenricType(Class<?> clazz) {
        return getSuperClassGenricType(clazz, 0);
    }

    /**
     * 获得定义类时声明父类的第泛型参数的类型
     * @param clazz 对象类
     * @param index 泛型参数索引，从0开始。
     * @return 泛型参数的类型
     */
    public static Class<?> getSuperClassGenricType(Class<?> clazz, int index) {
        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }

        return (Class<?>) params[index];
    }

    /**
     * 获得定义类时声明的第一个泛型参数的类型
     * @param clazz 对象类
     * @return 泛型参数的类型
     */
    public static Class<?> getGenericClass(Class<?> clazz) {
        return getGenericClass(clazz, 0);
    }

    /**
     * 获得定义类时声明的泛型参数的类型
     * @param clazz 对象类
     * @param index 泛型参数索引，从0开始。
     * @return 泛型参数的类型
     */
    public static Class<?> getGenericClass(Class<?> clazz, int index) {
        Type genType = clazz.getGenericSuperclass();

        if (genType instanceof ParameterizedType) {
            Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

            if ((params != null) && (params.length >= (index - 1))) {
                return (Class<?>) params[index];
            }
        }

        return null;
    }
}
