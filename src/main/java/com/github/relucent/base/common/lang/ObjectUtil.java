package com.github.relucent.base.common.lang;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 对象工具类
 * @author YYL
 */
public class ObjectUtil {

    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected ObjectUtil() {
    }

    /**
     * 判断对象是否为NULL
     * @param object 要检查的对象
     * @return 是否为NULL
     */
    public static boolean isNull(final Object object) {
        return object == null;
    }

    /**
     * 判断对象是否非NULL
     * @param value 要检查的对象
     * @return 是否非NULL
     */
    public static boolean isNotNull(final Object value) {
        return value != null;
    }

    /**
     * 如果传入的对象不为{@code null}，则直接返回，否则返回默认对象{@code defaultValue}
     * 
     * <pre>
     * ObjectUtil.orElse(null, "NULL")  = "NULL"
     * ObjectUtil.orElse("", "NULL")    = ""
     * ObjectUtil.orElse("bat", "NULL") = "bat"
     * </pre>
     * 
     * @param <T>          对象类型
     * @param value        要检查的对象
     * @param defaultValue 默认对象
     * @return 传入的对象，如果是{@code null}，则返回默认对象{@code defaultValue}.
     */
    public static <T> T defaultIfNull(final T value, final T defaultValue) {
        return value != null ? value : defaultValue;
    }

    /**
     * 如果传入的对象不为{@code null}，则直接返回，否则调用{@code defaultSupplier}并返回该调用的结果。
     * @param <T>             对象类型
     * @param value           要检查的对象
     * @param defaultSupplier {@code Supplier}，如果不存在值，则返回其结果
     * @return 传入的对象，如果是{@code null}，则返回{@code defaultSupplier}调用的结果。
     */
    public static <T> T defaultIfNullGet(final T value, final Supplier<? extends T> defaultSupplier) {
        return value != null ? value : defaultSupplier.get();
    }

    /**
     * 转换对象元素
     * @param <T>    对象元素类型
     * @param <R>    新对象元素类型
     * @param object 对象元素
     * @param mapper 转换方式
     * @param other  新集对象构造器
     * @return 新对象
     */
    public static <T, R> R map(T object, Function<T, R> mapper, Supplier<R> other) {
        if (other == null) {
            other = () -> null;
        }
        if (object == null || mapper == null) {
            return other.get();
        }
        return Optional.ofNullable(object).map(mapper).orElseGet(other);
    }

    /**
     * 比较两个对象是否相等
     * @param a 对象
     * @param b 对象比较的对象
     * @return 是否相等
     */
    public static boolean equals(Object a, Object b) {
        return Objects.equals(a, b);
    }

    /**
     * 尝试创建对象实例，如果无法创建则返回 null 或默认实现<br>
     * <h3>支持的情况：</h3>
     * <ul>
     * <li>接口/抽象类 → 使用默认实现（Map、List、Set 等）</li>
     * <li>数组 → 创建长度为 0 的数组</li>
     * <li>枚举 → 返回第一个枚举常量</li>
     * <li>普通类 → 尝试无参构造或有参构造</li>
     * </ul>
     * @param type Class 类型
     * @param <T>  对象类型
     * @return 实例对象，无法创建则返回 null
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstanceIfPossible(Class<T> type) {

        if (type == null) {
            return null;
        }

        // --- 1. 原始类型：返回默认值 ---
        if (type.isPrimitive()) {
            return (T) ClassUtil.getDefaultValue(type);
        }

        Class<? extends T> implType = type;

        // --- 2. 接口或抽象类：使用默认实现 ---
        if ((implType.isInterface() || Modifier.isAbstract(implType.getModifiers())) && !implType.isArray()) {
            implType = ClassUtil.getDefaultImplementation(type);
            // 如果找不到默认实现
            if (implType == null) {
                // 如果是函数式接口
                if (NoOpFunctionFactory.isFunctionalInterface(type)) {
                    return NoOpFunctionFactory.getNoOpInstance(type);
                }
                // 无法实例化的其他接口/抽象类
                return null;
            }
        }

        // --- 3. 枚举类型：返回第一个常量 ---
        if (implType.isEnum()) {
            T[] constants = implType.getEnumConstants();
            return (constants != null && constants.length > 0) ? constants[0] : null;
        }

        // --- 4. 数组类型：创建长度为 0 的数组 ---
        if (implType.isArray()) {
            Class<?> componentType = implType.getComponentType();
            return (T) Array.newInstance(componentType, 0);
        }

        // --- 5. 尝试无参构造 ---
        try {
            Constructor<? extends T> ctor = implType.getDeclaredConstructor();
            ctor.setAccessible(true);
            return ctor.newInstance();
        } catch (Exception ignore) {
            // 无无参构造则继续尝试带参构造
        }

        // --- 6. 如果无参构造不可用，尝试带参数构造（使用默认值填充） ---
        for (Constructor<?> constructor : implType.getDeclaredConstructors()) {
            try {
                constructor.setAccessible(true);
                Class<?>[] paramTypes = constructor.getParameterTypes();
                Object[] defaultParams = new Object[paramTypes.length];
                for (int i = 0; i < paramTypes.length; i++) {
                    defaultParams[i] = ClassUtil.getDefaultValue(paramTypes[i]);
                }
                return (T) constructor.newInstance(defaultParams);
            } catch (Exception ignore) {
                // 尝试下一个构造器
            }
        }

        // --- 7. 完全无法实例化 ---
        return null;
    }
}
