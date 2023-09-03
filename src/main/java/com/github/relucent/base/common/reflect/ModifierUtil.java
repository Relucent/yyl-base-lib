package com.github.relucent.base.common.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
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
     * 是否包含{@code public}修饰符
     * @param clazz 类
     * @return 是否包含{@code public}修饰符
     */
    public static boolean isPublic(Class<?> clazz) {
        return clazz != null && Modifier.isPublic(clazz.getModifiers());
    }

    /**
     * 是否包含{@code public}修饰符
     * @param member 类成员，{@code Constructor}、{@code Method}、{@code Field}
     * @return 是否包含{@code public}修饰符
     */
    public static boolean isPublic(Member member) {
        return member != null && Modifier.isPublic(member.getModifiers());
    }

    /**
     * 是否包含{@code private}修饰符
     * @param clazz 类
     * @return 是否包含{@code private}修饰符
     */
    public static boolean isPrivate(Class<?> clazz) {
        return clazz != null && Modifier.isPrivate(clazz.getModifiers());
    }

    /**
     * 是否包含{@code private}修饰符
     * @param member 类成员，{@code Constructor}、{@code Method}、{@code Field}
     * @return 是否包含{@code private}修饰符
     */
    public static boolean isPrivate(Member member) {
        return member != null && Modifier.isPrivate(member.getModifiers());
    }

    /**
     * 是否包含{@code protected}修饰符
     * @param clazz 类
     * @return 是否是共有{@code protected}
     */
    public static boolean isProtected(Class<?> clazz) {
        return clazz != null && Modifier.isProtected(clazz.getModifiers());
    }

    /**
     * 是是否包含{@code protected}修饰符
     * @param member 类成员，{@code Constructor}、{@code Method}、{@code Field}
     * @return 是否包含{@code protected}修饰符
     */
    public static boolean isProtected(Member member) {
        return member != null && Modifier.isProtected(member.getModifiers());
    }

    /**
     * 是否包含{@code static}修饰符
     * @param clazz 类
     * @return 是否包含{@code static}修饰符
     */
    public static boolean isStatic(Class<?> clazz) {
        return clazz != null && Modifier.isStatic(clazz.getModifiers());
    }

    /**
     * 是是否包含{@code static}修饰符
     * @param member 类成员，{@code Constructor}、{@code Method}、{@code Field}
     * @return 是否包含{@code static}修饰符
     */
    public static boolean isStatic(Member member) {
        return member != null && Modifier.isStatic(member.getModifiers());
    }

    /**
     * 是否抽象类，包含{@code abstract}修饰符
     * @param clazz 类
     * @return 是否包含{@code abstract}修饰符
     */
    public static boolean isAbstract(Class<?> clazz) {
        return clazz != null && Modifier.isAbstract(clazz.getModifiers());
    }

    /**
     * 是否抽象方法，包含{@code abstract}修饰符
     * @param method 方法
     * @return 是否包含{@code abstract}修饰符
     */
    public static boolean isAbstract(Method method) {
        return method != null && Modifier.isAbstract(method.getModifiers());
    }

    /**
     * 是否包含{@code final}修饰符
     * @param clazz 类
     * @return 是否包含{@code final}修饰符
     */
    public static boolean isFinal(Class<?> clazz) {
        return clazz != null && Modifier.isFinal(clazz.getModifiers());
    }

    /**
     * 是否包含{@code final}修饰符
     * @param member 类成员，{@code Constructor}、{@code Method}、{@code Field}
     * @return 是否包含{@code final}修饰符
     */
    public static boolean isFinal(Member member) {
        return member != null && Modifier.isFinal(member.getModifiers());
    }

    /**
     * 是否包含{@code volatile}修饰符
     * @param field 字段
     * @return 是否包含{@code volatile}修饰符
     */
    public static boolean isVolatile(Field field) {
        return field != null && Modifier.isFinal(field.getModifiers());
    }

    /**
     * 是否包含{@code transient}修饰符
     * @param field 字段
     * @return 是否包含{@code transient}修饰符
     */
    public static boolean isTransient(Field field) {
        return field != null && Modifier.isTransient(field.getModifiers());
    }

    /**
     * 是否是合成类（由java编译器生成的）<br>
     * 合成类（synthetic class）是指由Java编译器自动生成的、在源代码中并不存在的类。<br>
     * 它们通常用于实现内部类、匿名类、Lambda表达式和嵌套类等特性。
     * @param clazz 类
     * @return 是否是合成
     */
    public static boolean isSynthetic(Class<?> clazz) {
        return clazz != null && clazz.isSynthetic();
    }

    /**
     * 是否是合成成员 （由java编译器生成的）<br>
     * 合成类（synthetic class）是指由Java编译器自动生成的、在源代码中并不存在的类。<br>
     * 它们通常用于实现内部类、匿名类、Lambda表达式和嵌套类等特性。
     * @param member 类成员，{@code Constructor}、{@code Method}、{@code Field}
     * @return 是否是合成成员
     */
    public static boolean isSynthetic(Member member) {
        return member != null && member.isSynthetic();
    }
}
