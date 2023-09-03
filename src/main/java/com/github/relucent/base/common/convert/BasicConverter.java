package com.github.relucent.base.common.convert;

import java.lang.reflect.Type;

import com.github.relucent.base.common.lang.StringUtil;
import com.github.relucent.base.common.reflect.TypeUtil;

/**
 * 基础的转换器，提供通用的转换逻辑。<br>
 * 默认逻辑只支持明确的对象类型，不适用于有泛型参数的对象。<br>
 */
public abstract interface BasicConverter<T> extends Converter<T> {

    @Override
    default T convert(final Object source, final Type toType) {

        if (source == null) {
            return null;
        }

        if (TypeUtil.isUnknown(toType)) {
            throw new ConvertException(StringUtil.format("Unsupported convert to unKnown type: {}", toType));
        }

        @SuppressWarnings("unchecked")
        final Class<T> toClass = (Class<T>) TypeUtil.getClass(toType);

        if (toClass == null) {
            throw new ConvertException(StringUtil.format("Target type {} is not a class!", toType));
        }

        if (toClass.isInstance(source)) {
            return toClass.cast(source);
        }

        return convertInternal(source, toClass);
    }

    /**
     * 转换对象，将对象转换为指定的类型的对象，实现基本转换逻辑<br>
     * @param source 要转换的对象
     * @param toType 需要转换的类型
     * @return 转换为目标类型的结果对象
     */
    T convertInternal(Object source, Class<? extends T> toType);
}
