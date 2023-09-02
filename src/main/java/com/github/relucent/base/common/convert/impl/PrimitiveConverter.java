package com.github.relucent.base.common.convert.impl;

import com.github.relucent.base.common.convert.Converter;
import com.github.relucent.base.common.exception.ExceptionHelper;
import com.github.relucent.base.common.lang.ObjectUtil;

/**
 * 原始类型转换器<br>
 * @see java.lang.Boolean#TYPE
 * @see java.lang.Character#TYPE
 * @see java.lang.Byte#TYPE
 * @see java.lang.Short#TYPE
 * @see java.lang.Integer#TYPE
 * @see java.lang.Long#TYPE
 * @see java.lang.Float#TYPE
 * @see java.lang.Double#TYPE
 */
public class PrimitiveConverter implements Converter<Object> {

    public static final PrimitiveConverter INSTANCE = new PrimitiveConverter();

    private static final Boolean DEFAULT_BOOLEAN = Boolean.valueOf(false);
    private static final Character DEFAULT_CHARACTER = Character.valueOf('\000');
    private static final Byte DEFAULT_BYTE = Byte.valueOf((byte) 0);
    private static final Short DEFAULT_SHORT = Short.valueOf((short) 0);
    private static final Integer DEFAULT_INTEGER = Integer.valueOf(0);
    private static final Long DEFAULT_LONG = Long.valueOf(0L);
    private static final Float DEFAULT_FLOAT = Float.valueOf(0.0F);
    private static final Double DEFAULT_DOUBLE = Double.valueOf(0.0D);

    @Override
    public Object convert(Object source, Class<? extends Object> toType) {

        if (toType == null || !toType.isPrimitive()) {
            throw ExceptionHelper.error("convert error: " + toType + " is not of type a primitive");
        }

        if (boolean.class == toType) {
            return ObjectUtil.defaultIfNull(BooleanConverter.INSTANCE.convert(source, Boolean.class), DEFAULT_BOOLEAN);
        }

        if (char.class == toType) {
            return ObjectUtil.defaultIfNull(CharacterConverter.INSTANCE.convert(source, Character.class), DEFAULT_CHARACTER);
        }

        if (byte.class == toType) {
            return ObjectUtil.defaultIfNull(NumberConverter.toByte(source), DEFAULT_BYTE);
        }

        if (short.class == toType) {
            return ObjectUtil.defaultIfNull(NumberConverter.toShort(source), DEFAULT_SHORT);
        }

        if (int.class == toType) {
            return ObjectUtil.defaultIfNull(NumberConverter.toInteger(source), DEFAULT_INTEGER);
        }

        if (long.class == toType) {
            return ObjectUtil.defaultIfNull(NumberConverter.toLong(source), DEFAULT_LONG);
        }

        if (float.class == toType) {
            return ObjectUtil.defaultIfNull(NumberConverter.toFloat(source), DEFAULT_FLOAT);
        }

        if (double.class == toType) {
            return ObjectUtil.defaultIfNull(NumberConverter.toDouble(source), DEFAULT_DOUBLE);
        }

        throw ExceptionHelper.error("convert error: Unsupported to type: " + toType);
    }
}
