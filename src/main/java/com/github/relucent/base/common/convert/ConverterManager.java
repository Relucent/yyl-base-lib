package com.github.relucent.base.common.convert;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;

import com.github.relucent.base.common.collection.WeakConcurrentMap;
import com.github.relucent.base.common.convert.impl.BooleanConverter;
import com.github.relucent.base.common.convert.impl.CalendarConverter;
import com.github.relucent.base.common.convert.impl.CharacterConverter;
import com.github.relucent.base.common.convert.impl.DateConverter;
import com.github.relucent.base.common.convert.impl.DurationConverter;
import com.github.relucent.base.common.convert.impl.LocaleConverter;
import com.github.relucent.base.common.convert.impl.NumberConverter;
import com.github.relucent.base.common.convert.impl.PeriodConverter;
import com.github.relucent.base.common.convert.impl.PrimitiveConverter;
import com.github.relucent.base.common.convert.impl.StringConverter;
import com.github.relucent.base.common.convert.impl.TemporalAccessorConverter;
import com.github.relucent.base.common.convert.impl.TimeZoneConverter;
import com.github.relucent.base.common.convert.impl.ZoneIdConverter;

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
    private final Map<Type, Converter<?>> defaultConverters = new ConcurrentHashMap<>();
    /** 自定义类型转换器 */
    private final Map<Type, Converter<?>> customConverters = new WeakConcurrentMap<>();

    // =================================Constructors===========================================
    /** 类型转换管理器 */
    protected ConverterManager() {
        // 原始类型
        defaultConverters.put(Boolean.TYPE, PrimitiveConverter.INSTANCE);
        defaultConverters.put(Character.TYPE, PrimitiveConverter.INSTANCE);
        defaultConverters.put(Byte.TYPE, PrimitiveConverter.INSTANCE);
        defaultConverters.put(Double.TYPE, PrimitiveConverter.INSTANCE);
        defaultConverters.put(Float.TYPE, PrimitiveConverter.INSTANCE);
        defaultConverters.put(Integer.TYPE, PrimitiveConverter.INSTANCE);
        defaultConverters.put(Long.TYPE, PrimitiveConverter.INSTANCE);
        defaultConverters.put(Short.TYPE, PrimitiveConverter.INSTANCE);

        // 布尔
        defaultConverters.put(Boolean.class, BooleanConverter.INSTANCE);
        // 字符
        defaultConverters.put(Character.class, CharacterConverter.INSTANCE);

        // 数值
        defaultConverters.put(Byte.class, NumberConverter.INSTANCE);
        defaultConverters.put(Short.class, NumberConverter.INSTANCE);
        defaultConverters.put(Integer.class, NumberConverter.INSTANCE);
        defaultConverters.put(Long.class, NumberConverter.INSTANCE);
        defaultConverters.put(Float.class, NumberConverter.INSTANCE);
        defaultConverters.put(Double.class, NumberConverter.INSTANCE);
        defaultConverters.put(Number.class, NumberConverter.INSTANCE);
        defaultConverters.put(BigInteger.class, NumberConverter.INSTANCE);
        defaultConverters.put(BigDecimal.class, NumberConverter.INSTANCE);
        defaultConverters.put(AtomicInteger.class, NumberConverter.INSTANCE);
        defaultConverters.put(AtomicLong.class, NumberConverter.INSTANCE);
        defaultConverters.put(LongAdder.class, NumberConverter.INSTANCE);
        defaultConverters.put(DoubleAdder.class, NumberConverter.INSTANCE);

        // 字符串
        defaultConverters.put(String.class, StringConverter.INSTANCE);

        // 日期
        defaultConverters.put(Date.class, DateConverter.INSTANCE);
        defaultConverters.put(java.sql.Date.class, DateConverter.INSTANCE);
        defaultConverters.put(java.sql.Time.class, DateConverter.INSTANCE);
        defaultConverters.put(java.sql.Timestamp.class, DateConverter.INSTANCE);
        //
        defaultConverters.put(Calendar.class, CalendarConverter.INSTANCE);

        // 日期时间 JDK8+ (TemporalAccessor)
        defaultConverters.put(TemporalAccessor.class, TemporalAccessorConverter.INSTANCE);
        defaultConverters.put(Instant.class, TemporalAccessorConverter.INSTANCE);
        defaultConverters.put(LocalDateTime.class, TemporalAccessorConverter.INSTANCE);
        defaultConverters.put(LocalDate.class, TemporalAccessorConverter.INSTANCE);
        defaultConverters.put(LocalTime.class, TemporalAccessorConverter.INSTANCE);
        defaultConverters.put(ZonedDateTime.class, TemporalAccessorConverter.INSTANCE);
        defaultConverters.put(OffsetDateTime.class, TemporalAccessorConverter.INSTANCE);
        defaultConverters.put(OffsetTime.class, TemporalAccessorConverter.INSTANCE);
        defaultConverters.put(DayOfWeek.class, TemporalAccessorConverter.INSTANCE);
        defaultConverters.put(Month.class, TemporalAccessorConverter.INSTANCE);
        defaultConverters.put(MonthDay.class, TemporalAccessorConverter.INSTANCE);
        //
        defaultConverters.put(Period.class, PeriodConverter.INSTANCE);
        defaultConverters.put(Duration.class, DurationConverter.INSTANCE);

        // 其它类型
        defaultConverters.put(ZoneId.class, ZoneIdConverter.INSTANCE);
        defaultConverters.put(TimeZone.class, TimeZoneConverter.INSTANCE);
        defaultConverters.put(Locale.class, LocaleConverter.INSTANCE);
    }
    // =================================Methods================================================

    /**
     * 为指定类型注册转换器
     * @param <T> 转换的类型的泛型
     * @param type 转换器能转换的类型
     * @param converter 提供类的转换器
     */
    public <T> void register(final Class<T> type, final Converter<T> converter) {
        customConverters.put(type, converter);
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
        Converter<?> converter = customConverters.get(type);
        if (converter != null) {
            return converter;
        }
        return defaultConverters.get(type);
    }

    /**
     * 取消注册指定类型的转换器
     * @param type 转换器能转换的类型
     */
    public void unregister(final Type type) {
        customConverters.remove(type);
    }
}
