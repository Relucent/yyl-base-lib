package com.github.relucent.base.util.convert.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.github.relucent.base.util.convert.Converter;


/**
 * 数值类型转换器
 * @author YYL
 */
public class NumberConverter implements Converter<Number> {

    public static final NumberConverter INSTANCE = new NumberConverter();

    @Override
    public Number convert(Object source, Class<? extends Number> toType, Number vDefault) {
        try {
            if (toType.isPrimitive() && vDefault == null) {
                if (Double.TYPE.equals(toType)) {
                    vDefault = 0.0D;
                } else if (Float.TYPE.equals(toType)) {
                    vDefault = 0.0F;
                } else if (Long.TYPE.equals(toType)) {
                    vDefault = 0L;
                } else if (Integer.TYPE.equals(toType)) {
                    vDefault = 0x0;
                } else if (Short.TYPE.equals(toType)) {
                    vDefault = (short) 0;
                } else if (Byte.TYPE.equals(toType)) {
                    vDefault = (byte) 0;
                }
            }
            if (source == null) {
                return vDefault;
            }
            if (toType.isInstance(source)) {
                return toType.cast(source);
            }
            BigDecimal decimal = null;
            if (source instanceof Boolean) {
                decimal = new BigDecimal(((Boolean) source).booleanValue() ? "1" : "0");
            } else if (source instanceof Character) {
                decimal = new BigDecimal(((Character) source).charValue());
            } else if (source instanceof Number) {
                decimal = new BigDecimal(((Number) source).toString());
            } else {
                decimal = new BigDecimal(source.toString());
            }

            if (BigDecimal.class.isAssignableFrom(toType)) {
                return decimal;
            } else if (Double.class.isAssignableFrom(toType)) {
                return Double.valueOf(decimal.doubleValue());
            } else if (Float.class.isAssignableFrom(toType)) {
                return Float.valueOf(decimal.floatValue());
            } else if (BigInteger.class.isAssignableFrom(toType)) {
                return decimal.toBigInteger();
            } else if (Long.class.isAssignableFrom(toType)) {
                return Long.valueOf(decimal.longValue());
            } else if (Integer.class.isAssignableFrom(toType)) {
                return Integer.valueOf(decimal.intValue());
            } else if (Short.class.isAssignableFrom(toType)) {
                return Short.valueOf(decimal.shortValue());
            } else if (Byte.class.isAssignableFrom(toType)) {
                return Byte.valueOf(decimal.byteValue());
            } else if (AtomicInteger.class.isAssignableFrom(toType)) {
                return new AtomicInteger(decimal.intValue());
            } else if (AtomicLong.class.isAssignableFrom(toType)) {
                return new AtomicLong(decimal.longValue());
            } else if (Number.class.equals(toType)) {
                return decimal;
            } else if (Double.TYPE.equals(toType)) {
                return decimal.doubleValue();
            } else if (Float.TYPE.equals(toType)) {
                return decimal.floatValue();
            } else if (Long.TYPE.equals(toType)) {
                return decimal.longValue();
            } else if (Integer.TYPE.equals(toType)) {
                return decimal.intValue();
            } else if (Short.TYPE.equals(toType)) {
                return decimal.shortValue();
            } else if (Byte.TYPE.equals(toType)) {
                return decimal.byteValue();
            }
        } catch (Exception e) {
            // Ignore//
        }
        return vDefault;
    }

    @Override
    public boolean support(Class<? extends Number> type) {
        return Number.class.equals(type) || //
                BigDecimal.class.isAssignableFrom(type) || //
                Double.class.isAssignableFrom(type) || //
                Float.class.isAssignableFrom(type) || //
                BigInteger.class.isAssignableFrom(type) || //
                Long.class.isAssignableFrom(type) || //
                Integer.class.isAssignableFrom(type) || //
                Short.class.isAssignableFrom(type) || //
                Byte.class.isAssignableFrom(type) || //
                AtomicInteger.class.isAssignableFrom(type) || //
                AtomicLong.class.isAssignableFrom(type) || //
                Double.TYPE.equals(type) || //
                Float.TYPE.equals(type) || //
                Long.TYPE.equals(type) || //
                Integer.TYPE.equals(type) || //
                Short.TYPE.equals(type) || //
                Byte.TYPE.equals(type);
    }
}
