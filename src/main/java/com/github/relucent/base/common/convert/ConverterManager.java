package com.github.relucent.base.common.convert;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;

import com.github.relucent.base.common.collection.WeakConcurrentMap;
import com.github.relucent.base.common.convert.impl.BooleanConverter;
import com.github.relucent.base.common.convert.impl.CalendarConverter;
import com.github.relucent.base.common.convert.impl.CharacterConverter;
import com.github.relucent.base.common.convert.impl.DateConverter;
import com.github.relucent.base.common.convert.impl.NumberConverter;
import com.github.relucent.base.common.convert.impl.PrimitiveConverter;
import com.github.relucent.base.common.convert.impl.StringConverter;
import com.github.relucent.base.common.convert.impl.TemporalAccessorConverter;

/**
 * 类型转换管理器<br>
 * 包含已注册转换器的映射，用户可以添加新的转换器。 <br>
 */
public class ConverterManager {

    // =================================Instances==============================================
    private static final ConverterManager CONVERTER_MANAGER = new ConverterManager();

    /**
     * 获得默认的类型转换管理器
     * @return 类型转换管理器
     */
    public static ConverterManager getInstance() {
        return CONVERTER_MANAGER;
    }

    // =================================Fields=================================================
    /** 默认类型转换器 */
    private final WeakConcurrentMap<Type, Converter<?>> defaultConverterCache = new WeakConcurrentMap<>();
    /** 自定义类型转换器 */
    private final WeakConcurrentMap<Type, Converter<?>> customConverterCache = new WeakConcurrentMap<>();

    // =================================Constructors===========================================
    /** 类型转换管理器 */
    protected ConverterManager() {
        // 原始类型
        defaultConverterCache.put(Boolean.TYPE, PrimitiveConverter.INSTANCE);
        defaultConverterCache.put(Character.TYPE, PrimitiveConverter.INSTANCE);
        defaultConverterCache.put(Byte.TYPE, PrimitiveConverter.INSTANCE);
        defaultConverterCache.put(Double.TYPE, PrimitiveConverter.INSTANCE);
        defaultConverterCache.put(Float.TYPE, PrimitiveConverter.INSTANCE);
        defaultConverterCache.put(Integer.TYPE, PrimitiveConverter.INSTANCE);
        defaultConverterCache.put(Long.TYPE, PrimitiveConverter.INSTANCE);
        defaultConverterCache.put(Short.TYPE, PrimitiveConverter.INSTANCE);

        // 布尔
        defaultConverterCache.put(Boolean.class, BooleanConverter.INSTANCE);
        // 字符
        defaultConverterCache.put(Character.class, CharacterConverter.INSTANCE);

        // 数值
        defaultConverterCache.put(Byte.class, NumberConverter.INSTANCE);
        defaultConverterCache.put(Short.class, NumberConverter.INSTANCE);
        defaultConverterCache.put(Integer.class, NumberConverter.INSTANCE);
        defaultConverterCache.put(Long.class, NumberConverter.INSTANCE);
        defaultConverterCache.put(Float.class, NumberConverter.INSTANCE);
        defaultConverterCache.put(Double.class, NumberConverter.INSTANCE);
        defaultConverterCache.put(Number.class, NumberConverter.INSTANCE);
        defaultConverterCache.put(BigInteger.class, NumberConverter.INSTANCE);
        defaultConverterCache.put(BigDecimal.class, NumberConverter.INSTANCE);
        defaultConverterCache.put(AtomicInteger.class, NumberConverter.INSTANCE);
        defaultConverterCache.put(AtomicLong.class, NumberConverter.INSTANCE);
        defaultConverterCache.put(LongAdder.class, NumberConverter.INSTANCE);
        defaultConverterCache.put(DoubleAdder.class, NumberConverter.INSTANCE);

        // 字符串
        defaultConverterCache.put(String.class, StringConverter.INSTANCE);

        // 日期
        defaultConverterCache.put(Date.class, DateConverter.INSTANCE);
        defaultConverterCache.put(java.sql.Date.class, DateConverter.INSTANCE);
        defaultConverterCache.put(java.sql.Time.class, DateConverter.INSTANCE);
        defaultConverterCache.put(java.sql.Timestamp.class, DateConverter.INSTANCE);
        defaultConverterCache.put(Calendar.class, CalendarConverter.INSTANCE);

        // 日期时间 JDK8+(since 5.0.0)
        defaultConverterCache.put(TemporalAccessor.class, TemporalAccessorConverter.INSTANCE);
        defaultConverterCache.put(Instant.class, TemporalAccessorConverter.INSTANCE);
        defaultConverterCache.put(LocalDateTime.class, TemporalAccessorConverter.INSTANCE);
        defaultConverterCache.put(LocalDate.class, TemporalAccessorConverter.INSTANCE);
        defaultConverterCache.put(LocalTime.class, TemporalAccessorConverter.INSTANCE);
        defaultConverterCache.put(ZonedDateTime.class, TemporalAccessorConverter.INSTANCE);
        defaultConverterCache.put(OffsetDateTime.class, TemporalAccessorConverter.INSTANCE);
        defaultConverterCache.put(OffsetTime.class, TemporalAccessorConverter.INSTANCE);
        defaultConverterCache.put(DayOfWeek.class, TemporalAccessorConverter.INSTANCE);
        defaultConverterCache.put(Month.class, TemporalAccessorConverter.INSTANCE);
        defaultConverterCache.put(MonthDay.class, TemporalAccessorConverter.INSTANCE);
    }
    // =================================Methods================================================

    /**
     * 为指定类型注册转换器
     * @param <T> 转换的类型的泛型
     * @param type 转换器能转换的类型
     * @param converter 提供类的转换器
     */
    public <T> void register(final Class<T> type, final Converter<T> converter) {
        customConverterCache.put(type, converter);
    }

    /**
     * 检索指定类型的转换器<br>
     * 先从注册的转换器匹配，如果没找到再从默认的转换器列表中匹配<br>
     * @param <T> 转换的类型的泛型
     * @param type 转换器能转换的类型
     * @return 对应类型的转换器，如果没找到则返回 <code>null</code>
     */
    @SuppressWarnings("unchecked")
    public <T> Converter<T> lookup(final Class<T> type) {
        return (Converter<T>) lookup((Type) type);
    }

    /**
     * 检索指定类型的转换器<br>
     * 先从注册的转换器匹配，如果没找到再从默认的转换器列表中匹配<br>
     * @param type 转换器能转换的类型
     * @return 对应类型的转换器，如果没找到则返回 <code>null</code>
     */
    public Converter<?> lookup(final Type type) {
        Converter<?> converter = customConverterCache.get(type);
        if (converter != null) {
            return converter;
        }
        return defaultConverterCache.get(type);
    }

    /**
     * 取消注册指定类型的转换器
     * @param type 转换器能转换的类型
     */
    public void unregister(final Type type) {
        customConverterCache.remove(type);
    }
}
