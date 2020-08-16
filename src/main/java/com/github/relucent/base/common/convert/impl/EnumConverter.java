package com.github.relucent.base.common.convert.impl;

import com.github.relucent.base.common.convert.Converter;

/**
 * 枚举类型转换器
 * @author YYL
 */
@SuppressWarnings("rawtypes")
public class EnumConverter implements Converter<Enum> {

    public static final EnumConverter INSTANCE = new EnumConverter();

    @SuppressWarnings("unchecked")
    @Override
    public Enum convert(Object source, Class<? extends Enum> toType, Enum vDefault) {
        if (source == null || toType == null || !toType.isEnum()) {
            return vDefault;
        }
        try {
            if (toType.isInstance(source)) {
                return (Enum) source;
            }
            if (source instanceof Number) {
                int ordinal = ((Number) source).intValue();
                if (ordinal < 0) {
                    return vDefault;
                }
                Enum<?>[] array = toType.getEnumConstants();
                if (ordinal < array.length) {
                    return array[ordinal];
                }
                return vDefault;
            }
            String name = source.toString();
            return Enum.valueOf(toType, name);
        } catch (Exception e) {
            /* Ignore the error */
        }
        return vDefault;
    }

    @Override
    public boolean support(Class<? extends Enum> toType) {
        return Enum.class.isAssignableFrom(toType);
    }
}
