package com.github.relucent.base.common.convert.impl;

import com.github.relucent.base.common.convert.BasicConverter;
import com.github.relucent.base.common.lang.BooleanUtil;

/**
 * 布尔类型转换器
 * @author YYL
 * @version 2012-12-11
 * @see BasicConverter
 */
public class BooleanConverter implements BasicConverter<Boolean> {

    public static final BooleanConverter INSTANCE = new BooleanConverter();

    public Boolean convertInternal(Object source, Class<? extends Boolean> toType) {

        // 空直接返回
        if (source == null) {
            return null;
        }

        // 本身就是布尔类型
        if (source instanceof Boolean) {
            return (Boolean) source;
        }

        // 数字类型 0为false，其它为true
        if (source instanceof Number) {
            return ((Number) source).longValue() != 0L;
        }

        // 转换成字符串在进行比较
        String value = String.valueOf(source).toUpperCase();
        return BooleanUtil.toBoolean(value);
    }
}
