package com.github.relucent.base.common.reflect;

import java.lang.reflect.Constructor;

import com.github.relucent.base.common.exception.GeneralException;
import com.github.relucent.base.common.lang.ArrayUtil;
import com.github.relucent.base.common.lang.AssertUtil;
import com.github.relucent.base.common.lang.ClassUtil;

/**
 * 构造函数{@code java.lang.reflect.Constructor}相关反射工具类<br>
 * @author YYL
 */
public class ConstructorUtil {

    // =================================Fields================================================

    // =================================Constructors===========================================
    /**
     * 工具类私有构造
     */
    protected ConstructorUtil() {
    }

    // =================================Methods================================================
    /**
     * 获得类的构造函数列表
     * @param clazz 要为其查找构造函数的类
     * @return 构造函数列表
     * @see Class#getConstructors()
     */
    public static Constructor<?>[] getPublicConstructors(Class<?> clazz) {
        return clazz.getConstructors();
    }

    /**
     * 获得类全部的构造函数，包括类本身和继承来的所有{@code public} {@code protected} default(package) {@code private}方法。
     * @param clazz 要为其查找构造函数的类
     * @return 构造函数列表
     * @see Class#getDeclaredConstructors()
     */
    public static Constructor<?>[] getDeclaredConstructors(Class<?> clazz) {
        return clazz.getDeclaredConstructors();
    }

    /**
     * 查找类指定参数的构造函数，构造参数类型必须完全匹配<br>
     * @param <T> 要构造的类型
     * @param clazz 要为其查找构造函数的类, 不能为{@code null}
     * @param parameterTypes 参数类型数组，{@code null}被视为没有参数
     * @return 匹配的构造函数，如果找不到则返回{@code null}
     * @throws NullPointerException 如果参数{@code clazz} 为 {@code null}
     * @see Class#getConstructor
     */
    public static <T> Constructor<T> getConstructor(final Class<T> clazz, final Class<?>... parameterTypes) {
        AssertUtil.notNull(clazz, "class cannot be null");
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor(parameterTypes);
            MemberUtil.setAccessible(constructor);
            return constructor;
        } catch (final NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * 查找具有兼容参数的可访问构造函数。<br>
     * 这将检查所有构造函数，并找到一个具有兼容参数的构造函数。这要求每个参数都可以从给定的参数类型中赋值。这是一种比普通精确匹配算法更灵活的搜索。<br>
     * 首先，检查是否有一个构造函数与确切的签名匹配；如果没有找到则检查该类的所有构造函数，以查看其签名是否与参数类型的赋值兼容，返回第一个与赋值兼容的构造函数。<br>
     * @param <T> 要检查的类的类型
     * @param clazz 构造函数所属类
     * @param parameterTypes 构造函数参数类型列表
     * @return 有效的构造函数对象。如果没有匹配的构造函数，则返回<code>null</code>.
     */
    @SuppressWarnings("unchecked")
    public static <T> Constructor<T> getMatchingConstructor(final Class<T> clazz, final Class<?>... parameterTypes) {
        AssertUtil.notNull(clazz, "class cannot be null");

        // 检查是否有一个构造函数与确切的签名匹配，多数情况下可以直接找到构造器
        try {
            final Constructor<T> constructor = clazz.getConstructor(parameterTypes);
            MemberUtil.setAccessible(constructor);
            return constructor;

        } catch (final NoSuchMethodException e) {
            // Ignore
        }

        final Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        Constructor<T> result = null;

        // 返回最佳匹配
        for (Constructor<?> constructor : constructors) {
            // 比较参数
            if (MemberUtil.isMatchingExecutable(constructor, parameterTypes)) {
                MemberUtil.setAccessible(constructor);
                if (result == null || MemberUtil.compareConstructorFit(constructor, result, parameterTypes) < 0) {
                    result = (Constructor<T>) constructor;
                }
            }
        }
        return result;
    }

    /**
     * 调用构造函数返回指定类的新实例，从参数类型列表中选择正确的构造函数。<br>
     * 这将定位并调用构造函数，构造函数签名必须通过赋值兼容性与参数类型匹配。<br>
     * @param <T> 要构造的类型
     * @param clazz 要构造的类，不能为{@code null}
     * @param args 参数数组, {@code null} 被视为没有参数
     * @param parameterTypes 参数的类型数组, {@code null} 被视为没有参数
     * @return {@code clazz}的新实例
     * @throws NullPointerException 如果 {@code clazz} 为 {@code null}
     * @throws RuntimeException 如果调用时发生错误
     * @see Constructor#newInstance
     */
    public static <T> T invokeConstructor(final Class<T> clazz, Object[] args, Class<?>[] parameterTypes) {
        args = ArrayUtil.nullToEmpty(args);
        parameterTypes = ArrayUtil.nullToEmpty(parameterTypes);
        final Constructor<T> constructor = getMatchingConstructor(clazz, parameterTypes);
        if (constructor == null) {
            throw new GeneralException("No such accessible constructor on object: " + clazz.getName());
        }
        if (constructor.isVarArgs()) {
            final Class<?>[] methodParameterTypes = constructor.getParameterTypes();
            args = MethodUtil.getVarArgs(args, methodParameterTypes);
        }
        return invokeConstructor(constructor, args);
    }

    /**
     * 调用构造函数返回指定类的新实例，该类从参数类型推断正确的构造函数。<br>
     * @param <T> 要构造的类型
     * @param clazz 要构造的类，不能为{@code null}
     * @param args 实际参数数组，可能为null（这将导致调用默认构造函数）
     * @return 对象实例
     * @throws RuntimeException 如果调用时发生错误
     */
    public static <T> T invokeConstructor(final Class<T> clazz, Object[] args) {
        final Class<?>[] parameterTypes = ClassUtil.toClass(ArrayUtil.nullToEmpty(args));
        return invokeConstructor(clazz, args, parameterTypes);
    }

    /**
     * 调用构造函数返回指定类的新实例，从参数类型推断正确的构造函数。<br>
     * 这将定位并调用构造函数， 构造函数签名必须与参数类型完全匹配。<br>
     * @param <T> 要构造的类型
     * @param clazz 要构造的类，不能为{@code null}
     * @param args 参数数组, {@code null} 被视为没有参数
     * @return 对象实例
     * @throws RuntimeException 如果调用时发生错误
     */
    public static <T> T invokeExactConstructor(final Class<T> clazz, Object... args) {
        args = ArrayUtil.nullToEmpty(args);
        final Class<?> parameterTypes[] = ClassUtil.toClass(args);
        return invokeExactConstructor(clazz, args, parameterTypes);
    }

    /**
     * 调用构造函数返回指定类的新实例，从参数类型列表中选择正确的构造函数。<br>
     * 这将定位并调用构造函数， 构造函数签名必须与参数类型完全匹配。<br>
     * @param <T> 要构造的类型
     * @param clazz 要构造的类，不能为{@code null}
     * @param args 参数数组, {@code null} 被视为没有参数
     * @param parameterTypes 参数的类型数组, {@code null} 被视为没有参数
     * @return 对象实例
     * @throws RuntimeException 如果调用时发生错误
     */
    public static <T> T invokeExactConstructor(final Class<T> clazz, Object[] args, Class<?>[] parameterTypes) {
        args = ArrayUtil.nullToEmpty(args);
        parameterTypes = ArrayUtil.nullToEmpty(parameterTypes);
        final Constructor<T> constructor = getConstructor(clazz, parameterTypes);
        if (constructor == null) {
            throw new GeneralException("No such accessible constructor on object: " + clazz.getName());
        }
        return invokeConstructor(constructor, args);
    }

    /**
     * 调用构造函数返回类的新实例<br>
     * @param constructor 构造函数
     * @param args 参数数组, {@code null} 被视为没有参数
     * @return 类的新实例
     * @see Constructor#newInstance
     */
    public static <T> T invokeConstructor(Constructor<T> constructor, Object... args) {
        try {
            return constructor.newInstance(args);
        } catch (Exception ex) {
            MemberUtil.handleReflectionException(ex);
        }
        throw new GeneralException("Should never get here");
    }
}
