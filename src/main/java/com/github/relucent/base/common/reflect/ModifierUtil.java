package com.github.relucent.base.common.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 修饰符工具类
 */
public class ModifierUtil {
    // =================================Constructors===========================================
    /**
     * 工具类私有构造
     */
    protected ModifierUtil() {
    }

    // =================================Methods================================================

    /**
     * 是否是Public类
     * @param clazz 类
     * @return 是否是Public
     */
    public static boolean isPublic(Class<?> clazz) {
        return clazz != null && Modifier.isPublic(clazz.getModifiers());
    }

    /**
     * 是否是Public构造
     * @param constructor 构造
     * @return 是否是Public
     */
    public static boolean isPublic(Constructor<?> constructor) {
        return constructor != null && Modifier.isPublic(constructor.getModifiers());
    }

    /**
     * 是否是Public方法
     * @param method 方法
     * @return 是否是Public
     */
    public static boolean isPublic(Method method) {
        return method != null && Modifier.isPublic(method.getModifiers());
    }

    /**
     * 是否是Public字段
     * @param field 字段
     * @return 是否是Public
     */
    public static boolean isPublic(Field field) {
        return field != null && Modifier.isPublic(field.getModifiers());
    }

    /**
     * 是否是static类
     * @param clazz 类
     * @return 是否是static
     */
    public static boolean isStatic(Class<?> clazz) {
        return clazz != null && Modifier.isStatic(clazz.getModifiers());
    }

    /**
     * 是否是static方法
     * @param method 方法
     * @return 是否是static
     */
    public static boolean isStatic(Method method) {
        return method != null && Modifier.isStatic(method.getModifiers());
    }

    /**
     * 是否是static字段
     * @param field 字段
     * @return 是否是static
     */
    public static boolean isStatic(Field field) {
        return field != null && Modifier.isStatic(field.getModifiers());
    }

    /**
     * 是否是合成类（由java编译器生成的）
     * @param clazz 类
     * @return 是否是合成
     */
    public static boolean isSynthetic(Class<?> clazz) {
        return clazz != null && clazz.isSynthetic();
    }

    /**
     * 是否是合成方法（由java编译器生成的）
     * @param method 方法
     * @return 是否是合成方法
     */
    public static boolean isSynthetic(Method method) {
        return method != null && method.isSynthetic();
    }

    /**
     * 是否是合成字段（由java编译器生成的）
     * @param field 字段
     * @return 是否是合成字段
     */
    public static boolean isSynthetic(Field field) {
        return field != null && field.isSynthetic();
    }

    /**
     * 是否抽象类
     * @param clazz 类
     * @return 是否抽象方法
     */
    public static boolean isAbstract(Class<?> clazz) {
        return clazz != null && Modifier.isAbstract(clazz.getModifiers());
    }

    /**
     * 是否抽象方法
     * @param method 方法
     * @return 是否抽象方法
     */
    public static boolean isAbstract(Method method) {
        return method != null && Modifier.isAbstract(method.getModifiers());
    }

    /**
     * 是否临时性变量字段
     * @param field 字段
     * @return 是否临时性变量字段
     */
    public static boolean isTransient(Field field) {
        return field != null && Modifier.isTransient(field.getModifiers());
    }

    /**
     * 是否临时性变量方法
     * @param method 方法
     * @return 是否临时性变量方法
     */
    public static boolean isTransient(Method method) {
        return method != null && Modifier.isTransient(method.getModifiers());
    }

}
