package com.github.relucent.base.common.lang;

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
     * @param <T> 对象类型
     * @param value 要检查的对象
     * @param defaultValue 默认对象
     * @return 传入的对象，如果是{@code null}，则返回默认对象{@code defaultValue}.
     */
    public static <T> T defaultIfNull(final T value, final T defaultValue) {
        return value != null ? value : defaultValue;
    }

    /**
     * 如果传入的对象不为{@code null}，则直接返回，否则调用{@code defaultSupplier}并返回该调用的结果。
     * @param <T> 对象类型
     * @param value 要检查的对象
     * @param defaultSupplier {@code Supplier}，如果不存在值，则返回其结果
     * @return 传入的对象，如果是{@code null}，则返回{@code defaultSupplier}调用的结果。
     */
    public static <T> T defaultIfNullGet(final T value, final Supplier<? extends T> defaultSupplier) {
        return value != null ? value : defaultSupplier.get();
    }

    /**
     * 转换对象元素
     * @param <T> 对象元素类型
     * @param <R> 新对象元素类型
     * @param object 对象元素
     * @param mapper 转换方式
     * @param other 新集对象构造器
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
}
