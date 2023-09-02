package com.github.relucent.base.common.convert;

import com.github.relucent.base.common.lang.ObjectUtil;

/**
 * 类型转换接口
 * @param <T> 转换到的目标类型
 * @author YYL
 */
@FunctionalInterface
public interface Converter<T> {

    /**
     * 类型转换，将对象转换为指定类型<br>
     * @param source 初始对象
     * @param toType 需要转换的类型
     * @return 类型转换后的对象
     */
    T convert(Object source, Class<? extends T> toType);

    /**
     * 类型转换，将对象转换为指定的类型，如果转换失败时返回默认值<br>
     * @param source 初始对象
     * @param toType 需要转换的类型
     * @param defaultValue 默认值，如果转换失败时返回默认值
     * @return 类型转换后的对象
     */
    default T convert(Object source, Class<? extends T> toType, final T defaultValue) {
        return (T) ObjectUtil.defaultIfNull(convert(source, toType), defaultValue);
    }
}
