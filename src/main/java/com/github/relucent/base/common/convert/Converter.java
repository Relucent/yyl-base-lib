package com.github.relucent.base.common.convert;

import java.lang.reflect.Type;

import com.github.relucent.base.common.lang.ObjectUtil;

/**
 * 类型转换接口，提供到类型的动态对象转换
 * @param <T> 目标类型泛型
 * @author YYL
 */
@FunctionalInterface
public interface Converter<T> {

    /**
     * 转换对象，将对象转换为指定的类型的对象<br>
     * @param source 要转换的对象
     * @param toType 需要转换的类型
     * @return 类型转换后的对象
     */
    T convert(Object source, Type toType);

    /**
     * 转换对象，将对象转换为指定的类型的对象，如果转换失败则返回默认值<br>
     * @param source 要转换的对象
     * @param toType 需要转换的类型
     * @param defaultValue 默认值，如果转换失败时返回默认值
     * @return 转换为目标类型的结果对象
     */
    default T convert(Object source, Type toType, T defaultValue) {
        return (T) ObjectUtil.defaultIfNull(convert(source, toType), defaultValue);
    }
}
