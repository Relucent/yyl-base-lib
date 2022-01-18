package com.github.relucent.base.common.lang;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 对象工具类
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
     * @param object 要检查的对象
     * @return 是否非NULL
     */
    public static boolean isNotNull(final Object object) {
        return object != null;
    }

    /**
     * 返回传入的对象，或者如果对象为{@code null}，则返回默认对象{@code defaultObject}.
     * 
     * <pre>
     * ObjectUtil.defaultObject(null, "NULL")  = "NULL"
     * ObjectUtil.defaultObject("", "NULL")    = ""
     * ObjectUtil.defaultObject("bat", "NULL") = "bat"
     * </pre>
     * 
     * @param object 要检查的对象
     * @param defaultObject 默认对象
     * @return 传入的对象，如果是{@code null}，则返回默认对象{@code defaultObject}.
     */
    public static Object defaultObject(final Object object, final Object defaultObject) {
        return object == null ? defaultObject : object;
    }

    /**
     * 转换对象元素
     * @param <T> 对象元素
     * @param <R> 新对象元素
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
