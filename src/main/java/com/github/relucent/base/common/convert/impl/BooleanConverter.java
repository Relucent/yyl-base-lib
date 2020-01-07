package com.github.relucent.base.common.convert.impl;

import com.github.relucent.base.common.convert.Converter;
import com.github.relucent.base.common.lang.ArrayUtil;

/**
 * 布尔类型转换器
 * @author YYL
 * @version 2012-12-11
 * @see Converter
 */
public class BooleanConverter implements Converter<Boolean> {

    public static final BooleanConverter INSTANCE = new BooleanConverter();

    private static final String[] TRUE_VALUES = {"1", "T", "Y", "TRUE", "YES", "ON", "是"};
    private static final String[] FALSE_VALUES = {"0", "F", "N", "FALSE", "NO", "OFF", "否"};

    public Boolean convert(Object source, Class<? extends Boolean> toType, Boolean vDefault) {
        try {
            if (toType.isPrimitive() && vDefault == null) {
                vDefault = Boolean.FALSE;
            }
            if (source == null) {
                return vDefault;
            }
            if (source instanceof Boolean) {
                return (Boolean) source;
            }
            if (source instanceof Number) {
                return ((Number) source).intValue() != 0;
            }

            String value = String.valueOf(source).toUpperCase();
            if (ArrayUtil.contains(TRUE_VALUES, value)) {
                return Boolean.TRUE;
            }
            if (ArrayUtil.contains(FALSE_VALUES, value)) {
                return Boolean.FALSE;
            }

        } catch (Exception e) {
            // Ignore//
        }
        return vDefault;
    }

    @Override
    public boolean support(Class<? extends Boolean> type) {
        return Boolean.class.equals(type) || Boolean.TYPE.equals(type);
    }
}
