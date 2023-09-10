package com.github.relucent.base.common.convert;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import com.github.relucent.base.common.bean.BeanUtil;
import com.github.relucent.base.common.collection.Listx;
import com.github.relucent.base.common.collection.Mapx;
import com.github.relucent.base.common.convert.impl.ArrayConverter;
import com.github.relucent.base.common.convert.impl.BeanConverter;
import com.github.relucent.base.common.convert.impl.ClassConverter;
import com.github.relucent.base.common.convert.impl.CollectionConverter;
import com.github.relucent.base.common.convert.impl.EnumConverter;
import com.github.relucent.base.common.convert.impl.MapConverter;
import com.github.relucent.base.common.reflect.TypeReference;
import com.github.relucent.base.common.reflect.TypeUtil;

/**
 * 类型转换工具类(Type Conversion) Standard Wrapped
 * @author YYL
 */
public class ConvertUtil {

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
    public static <T> T convert(Object source, Class<T> toType, T defaultValue) {
        return convert(source, (Type) toType, defaultValue);
    }

    /**
     * 将对象转换为指定的类型
     * @param <T> 转换类型泛型
     * @param source 要转换的对象
     * @param toType 转换的目标类型
     * @param defaultValue 默认值
     * @return 转换类型后的对象(无法正确转换类型则返回默认值)
     */

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> T convert(final Object source, final Type toType, final T defaultValue) {
        if (toType == null) {
            return defaultValue;
        }

        if (toType instanceof TypeReference) {
            return convert(source, (Type) ((TypeReference) toType).getType(), defaultValue);
        }

        if (toType == Object.class) {
            return (T) source;
        }

        Converter converter = ConverterManager.getInstance().lookup(toType);

        if (converter != null) {
            return (T) converter.convert(source, toType, defaultValue);
        }

        Class<T> rowType = (Class<T>) TypeUtil.getClass(toType);

        if (rowType == null) {
            if (defaultValue != null) {
                rowType = (Class<T>) defaultValue.getClass();
            } else {
                // 此处考虑抛出异常
                return defaultValue;
            }
        }

        // 枚举类型
        if (rowType.isEnum()) {
            return (T) EnumConverter.INSTANCE.convert(source, (Class<? extends Enum>) toType, (Enum) defaultValue);
        }

        // 数组类型
        if (rowType.isArray()) {
            return (T) ArrayConverter.INSTANCE.convert(source, toType, defaultValue);
        }

        // 集合类型（含有泛型参数）
        if (Collection.class.isAssignableFrom(rowType)) {
            return (T) CollectionConverter.INSTANCE.convert(source, toType, (Collection<?>) defaultValue);
        }

        // Map 类型
        if (Map.class.isAssignableFrom(rowType)) {
            return (T) MapConverter.INSTANCE.convert(source, toType, (Map<?, ?>) defaultValue);
        }

        // Bean
        if (BeanUtil.isWritableBean(rowType)) {
            return (T) BeanConverter.INSTANCE.convert(source, toType);
        }

        // Class
        if ("java.lang.Class".equals(rowType.getName())) {
            return (T) ClassConverter.INSTANCE.convert(source, toType);
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
