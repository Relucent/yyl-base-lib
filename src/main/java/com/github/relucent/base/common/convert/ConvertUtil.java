package com.github.relucent.base.common.convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.relucent.base.common.collection.Listx;
import com.github.relucent.base.common.collection.Mapx;
import com.github.relucent.base.common.convert.impl.ArrayConverter;
import com.github.relucent.base.common.convert.impl.BooleanConverter;
import com.github.relucent.base.common.convert.impl.CalendarConverter;
import com.github.relucent.base.common.convert.impl.CharacterConverter;
import com.github.relucent.base.common.convert.impl.DateConverter;
import com.github.relucent.base.common.convert.impl.EnumConverter;
import com.github.relucent.base.common.convert.impl.ListxConverter;
import com.github.relucent.base.common.convert.impl.MapxConverter;
import com.github.relucent.base.common.convert.impl.NumberConverter;
import com.github.relucent.base.common.convert.impl.StringConverter;

/**
 * 类型转换工具类(Type Conversion) Standard Wrapped
 * @author YYL
 */
public class ConvertUtil {

    // ==============================Fields===========================================
    /** 转换器映射表 */
    private static final Map<Class<?>, Converter<?>> CONVERTERS = new ConcurrentHashMap<Class<?>, Converter<?>>();
    static {

        // 原始类型
        CONVERTERS.put(Boolean.TYPE, BooleanConverter.INSTANCE);
        CONVERTERS.put(Character.TYPE, CharacterConverter.INSTANCE);
        CONVERTERS.put(Byte.TYPE, NumberConverter.INSTANCE);
        CONVERTERS.put(Double.TYPE, NumberConverter.INSTANCE);
        CONVERTERS.put(Float.TYPE, NumberConverter.INSTANCE);
        CONVERTERS.put(Integer.TYPE, NumberConverter.INSTANCE);
        CONVERTERS.put(Long.TYPE, NumberConverter.INSTANCE);
        CONVERTERS.put(Short.TYPE, NumberConverter.INSTANCE);
        // 布尔/字符/数值
        CONVERTERS.put(Boolean.class, BooleanConverter.INSTANCE);
        CONVERTERS.put(Character.class, CharacterConverter.INSTANCE);
        CONVERTERS.put(Byte.class, NumberConverter.INSTANCE);
        CONVERTERS.put(Short.class, NumberConverter.INSTANCE);
        CONVERTERS.put(Double.class, NumberConverter.INSTANCE);
        CONVERTERS.put(Float.class, NumberConverter.INSTANCE);
        CONVERTERS.put(Integer.class, NumberConverter.INSTANCE);
        CONVERTERS.put(Long.class, NumberConverter.INSTANCE);
        CONVERTERS.put(BigInteger.class, NumberConverter.INSTANCE);
        CONVERTERS.put(BigDecimal.class, NumberConverter.INSTANCE);
        // 字符串
        CONVERTERS.put(String.class, StringConverter.INSTANCE);
        // 日期
        CONVERTERS.put(Date.class, DateConverter.INSTANCE);
        CONVERTERS.put(java.sql.Date.class, DateConverter.INSTANCE);
        CONVERTERS.put(java.sql.Time.class, DateConverter.INSTANCE);
        CONVERTERS.put(java.sql.Timestamp.class, DateConverter.INSTANCE);
        CONVERTERS.put(Calendar.class, CalendarConverter.INSTANCE);
        // 集合类型
        CONVERTERS.put(Mapx.class, MapxConverter.INSTANCE);
        CONVERTERS.put(Listx.class, ListxConverter.INSTANCE);
    }

    // ==============================Constructors=====================================
    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected ConvertUtil() {
    }

    // ==============================Methods==========================================
    /**
     * 将对象转换为布尔类型
     * @param value 待转换对象
     * @return 转换类型后的对象
     */
    public static Boolean toBoolean(Object value) {
        return toBoolean(value, false);
    }

    /**
     * 将对象转换为布尔类型
     * @param value 待转换对象
     * @param defaultValue 默认值
     * @return 转换类型后的对象
     */
    public static Boolean toBoolean(Object value, Boolean defaultValue) {
        return convert(value, Boolean.class, defaultValue);
    }

    /**
     * 将对象转换为整形类型
     * @param value 待转换对象
     * @return 转换类型后的对象
     */
    public static Integer toInteger(Object value) {
        return toInteger(value, null);
    }

    /**
     * 将对象转换为整形类型
     * @param value 待转换对象
     * @param defaultValue 默认值
     * @return 转换类型后的对象
     */
    public static Integer toInteger(Object value, Integer defaultValue) {
        return convert(value, Integer.class, defaultValue);
    }

    /**
     * 将对象转换为长整形类型
     * @param value 待转换对象
     * @return 转换类型后的对象
     */
    public static Long toLong(Object value) {
        return toLong(value, null);
    }

    /**
     * 将对象转换为长整形类型
     * @param value 待转换对象
     * @param defaultValue 默认值
     * @return 转换类型后的对象
     */
    public static Long toLong(Object value, Long defaultValue) {
        return convert(value, Long.class, defaultValue);
    }

    /**
     * 将对象转换为浮点类型
     * @param value 待转换对象
     * @return 转换类型后的对象
     */
    public static Float toFloat(Object value) {
        return toFloat(value, null);
    }

    /**
     * 将对象转换为浮点类型
     * @param value 待转换对象
     * @param defaultValue 默认值
     * @return 转换类型后的对象
     */
    public static Float toFloat(Object value, Float defaultValue) {
        return convert(value, Float.class, defaultValue);
    }

    /**
     * 将对象转换为双字节类型
     * @param value 待转换对象
     * @return 转换类型后的对象
     */
    public static Double toDouble(Object value) {
        return toDouble(value, null);
    }

    /**
     * 将对象转换为双字节类型
     * @param value 待转换对象
     * @param defaultValue 默认值
     * @return 转换类型后的对象
     */
    public static Double toDouble(Object value, Double defaultValue) {
        return convert(value, Double.class, defaultValue);
    }

    /**
     * 将对象转换为字符串类型
     * @param value 待转换对象
     * @return 转换类型后的对象
     */
    public static String toString(Object value) {
        return toString(value, null);
    }

    /**
     * 将对象转换为字符串类型
     * @param value 待转换对象
     * @param defaultValue 默认值
     * @return 转换类型后的对象
     */
    public static String toString(Object value, String defaultValue) {
        return convert(value, String.class, defaultValue);
    }

    /**
     * 将对象转换为日期类型
     * @param value 待转换对象
     * @return 转换类型后的对象
     */
    public static Date toDate(Object value) {
        return toDate(value, null);
    }

    /**
     * 将对象转换为日期类型
     * @param value 待转换对象
     * @param defaultValue 默认值
     * @return 转换类型后的对象
     */
    public static Date toDate(Object value, Date defaultValue) {
        return convert(value, Date.class, defaultValue);
    }

    /**
     * 将对象转换为枚举类型
     * @param <T> 枚举类型泛型
     * @param enumType 枚举类型
     * @param value 待转换对象
     * @return 转换类型后的对象
     */
    public static <T extends Enum<T>> T toEnum(Object value, Class<T> enumType) {
        return toEnum(value, enumType, null);
    }

    /**
     * 将对象转换为枚举类型
     * @param <T> 枚举类型泛型
     * @param enumType 枚举类型
     * @param value 待转换对象
     * @param defaultValue 默认值
     * @return 转换类型后的对象
     */
    public static <T extends Enum<T>> T toEnum(Object value, Class<T> enumType, T defaultValue) {
        return convert(value, enumType, defaultValue);
    }

    /**
     * 将对象转换为大整型类型
     * @param value 待转换对象
     * @return 转换类型后的对象
     */
    public static BigInteger toBigInteger(Object value) {
        return toBigInteger(value, null);
    }

    /**
     * 将对象转换为大整型类型
     * @param value 待转换对象
     * @param defaultValue 默认值
     * @return 转换类型后的对象
     */
    public static BigInteger toBigInteger(Object value, BigInteger defaultValue) {
        return convert(value, BigInteger.class, defaultValue);
    }

    /**
     * 将对象转换为大数字类型
     * @param value 待转换对象
     * @return 转换类型后的对象
     */
    public static BigDecimal toBigDecimal(Object value) {
        return toBigDecimal(value, null);
    }

    /**
     * 将对象转换为大数字类型
     * @param value 待转换对象
     * @param defaultValue 默认值
     * @return 转换类型后的对象
     */
    public static BigDecimal toBigDecimal(Object value, BigDecimal defaultValue) {
        return convert(value, BigDecimal.class, defaultValue);
    }

    /**
     * 将对象转换为Map类型
     * @param value 待转换对象
     * @return 转换类型后的对象
     */
    public static Mapx toMap(Object value) {
        return toMap(value, null);
    }

    /**
     * 将对象转换为Map类型
     * @param value 待转换对象
     * @param defaultValue 默认值
     * @return 转换类型后的对象
     */
    public static Mapx toMap(Object value, Mapx defaultValue) {
        return convert(value, Mapx.class, defaultValue);
    }

    /**
     * 将对象转换为List类型
     * @param value 待转换对象
     * @return 转换类型后的对象
     */
    public static Listx toList(Object value) {
        return toList(value, null);
    }

    /**
     * 将对象转换为List类型
     * @param value 待转换对象
     * @param defaultValue 默认值
     * @return 转换类型后的对象
     */
    public static Listx toList(Object value, Listx defaultValue) {
        return convert(value, Listx.class, defaultValue);
    }

    /**
     * 将对象转换为指定的类型
     * @param <T> 转换类型泛型
     * @param obj 对象转换
     * @param toType 转换的目标类型
     * @return 转换类型后的对象，无法正确转换类型则返回 {@code null}
     */
    public static <T> T convert(Object obj, Class<T> toType) {
        return convert(obj, toType, null);
    }

    /**
     * 将对象转换为指定的类型
     * @param <T> 转换类型泛型
     * @param source 要转换的对象
     * @param toType 转换的目标类型
     * @param defaultValue 默认值
     * @return 转换类型后的对象(无法正确转换类型则返回默认值)
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <T> T convert(Object source, Class<T> toType, T defaultValue) {
        if (toType == null) {
            return defaultValue;
        }

        if (toType == Object.class) {
            return (T) source;
        }

        Converter converter = CONVERTERS.get(toType);

        if (converter != null) {
            return (T) converter.convert(source, toType, defaultValue);
        }

        if (toType.isEnum()) {
            return (T) EnumConverter.INSTANCE.convert(source, (Class<? extends Enum>) toType, (Enum) defaultValue);
        }

        if (toType.isArray()) {
            return (T) ArrayConverter.INSTANCE.convert(source, toType, defaultValue);
        }

        for (Converter c : CONVERTERS.values()) {
            if (c.support(toType)) {
                return (T) c.convert(source, toType, defaultValue);
            }
        }

        return defaultValue;
    }

    /**
     * 判断类型是否是准类型
     * @param clazz 对象类型
     * @return 如果参数是标准类型返回TRUE，否则返回FLASE
     */
    public static boolean isStandardType(Class<?> clazz) {
        // Primitive
        if (clazz.isPrimitive()) {
            return true;
        }
        // String
        if (String.class.equals(clazz)) {
            return true;
        }
        // Boolean
        if (Boolean.class.equals(clazz)) {
            return true;
        }
        // Character
        if (Character.class.equals(clazz)) {
            return true;
        }
        // AtomicInteger, AtomicLong, BigDecimal, BigInteger, Byte, Double,Float, Integer, Long,
        // Short
        if (Number.class.isAssignableFrom(clazz)) {
            return true;
        }
        // Date
        if (Date.class.isAssignableFrom(clazz)) {
            return true;
        }
        // _Enum
        if (clazz.isEnum()) {
            return true;
        }
        return false;
    }
}
