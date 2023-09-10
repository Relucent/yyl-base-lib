package com.github.relucent.base.common.convert.impl;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.Era;
import java.time.chrono.IsoEra;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;

import com.github.relucent.base.common.convert.BasicConverter;
import com.github.relucent.base.common.lang.ObjectUtil;
import com.github.relucent.base.common.lang.StringUtil;
import com.github.relucent.base.common.time.DateUtil;
import com.github.relucent.base.common.time.TemporalAccessorUtil;
import com.github.relucent.base.common.time.ZoneUtil;

/**
 * 时间对象解析转换器。<br>
 * @see java.time.Instant
 * @see java.time.LocalDateTime
 * @see java.time.LocalDate
 * @see java.time.LocalTime
 * @see java.time.ZonedDateTime
 * @see java.time.OffsetDateTime
 * @see java.time.OffsetTime
 */
public class TemporalAccessorConverter implements BasicConverter<TemporalAccessor> {

    public static final TemporalAccessorConverter INSTANCE = new TemporalAccessorConverter();

    public TemporalAccessor convertInternal(Object source, Class<? extends TemporalAccessor> toType) {
        if (source == null) {
            return null;
        }

        if (source.getClass() == toType) {
            return (TemporalAccessor) source;
        }

        if (source instanceof Number) {
            return parseFromLong(((Number) source).longValue(), toType);
        }
        if (source instanceof Instant) {
            return parseFromInstant((Instant) source, ZoneUtil.getDefaultZoneId(), toType);
        }

        if (source instanceof TemporalAccessor) {
            return parseFromTemporalAccessor((TemporalAccessor) source, toType);
        }

        if (source instanceof Date) {
            return parseFromInstant(((Date) source).toInstant(), ZoneUtil.getDefaultZoneId(), toType);
        }

        if (source instanceof Calendar) {
            Calendar calendar = (Calendar) source;
            return parseFromInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId(), toType);
        }

        return parseFromText(StringUtil.string(source), toType);
    }

    /**
     * 将时间戳转换时间对象
     * @param source 时间戳
     * @param toType 目标类型
     * @return {@code TemporalAccessor}时间对象
     */
    private TemporalAccessor parseFromLong(final Long source, final Class<?> toType) {
        if (Month.class.equals(toType)) {
            return Month.of(Math.toIntExact(source));
        }
        if (DayOfWeek.class.equals(toType)) {
            return DayOfWeek.of(Math.toIntExact(source));
        }
        if (Era.class.equals(toType)) {
            return IsoEra.of(Math.toIntExact(source));
        }
        Instant instant = Instant.ofEpochMilli(source);

        return parseFromInstant(instant, (ZoneId) null, toType);
    }

    /**
     * 将TemporalAccessor型时间戳转换为java.time中的对象
     * @param instant {@link Instant}对象
     * @param zoneId 时区ID，null表示当前系统默认的时区
     * @param toType 目标类型
     * @return {@code TemporalAccessor}时间对象
     */
    private TemporalAccessor parseFromInstant(Instant instant, ZoneId zoneId, Class<?> toType) {
        if (Instant.class.equals(toType)) {
            return instant;
        }
        zoneId = ObjectUtil.defaultIfNullGet(zoneId, ZoneUtil::getDefaultZoneId);
        if (LocalDateTime.class.equals(toType)) {
            return LocalDateTime.ofInstant(instant, zoneId);
        }
        if (LocalDate.class.equals(toType)) {
            return instant.atZone(zoneId).toLocalDate();
        }
        if (LocalTime.class.equals(toType)) {
            return instant.atZone(zoneId).toLocalTime();
        }
        if (ZonedDateTime.class.equals(toType)) {
            return instant.atZone(zoneId);
        }
        if (OffsetDateTime.class.equals(toType)) {
            return OffsetDateTime.ofInstant(instant, zoneId);
        }
        if (OffsetTime.class.equals(toType)) {
            return OffsetTime.ofInstant(instant, zoneId);
        }
        if (TemporalAccessor.class.equals(toType)) {
            return instant.atZone(zoneId);
        }
        return null;
    }

    /**
     * 将TemporalAccessor型时间戳转换为java.time中的对象
     * @param temporalAccessor 需要转换的{@code TemporalAccessor}对象
     * @param toType 目标类型
     * @return 转换后的{@code TemporalAccessor}对象
     */
    private TemporalAccessor parseFromTemporalAccessor(TemporalAccessor temporalAccessor, Class<?> toType) {
        if (TemporalAccessor.class.equals(toType)) {
            return (TemporalAccessor) temporalAccessor;
        }
        if (DayOfWeek.class.equals(toType)) {
            return DayOfWeek.from(temporalAccessor);
        }
        if (Month.class.equals(toType)) {
            return Month.from(temporalAccessor);
        }
        if (MonthDay.class.equals(toType)) {
            return MonthDay.from(temporalAccessor);
        }
        if (temporalAccessor instanceof LocalDateTime) {
            return parseFromLocalDateTime((LocalDateTime) temporalAccessor, toType);
        }
        if (temporalAccessor instanceof ZonedDateTime) {
            return parseFromZonedDateTime((ZonedDateTime) temporalAccessor, toType);
        }
        return parseFromInstant(TemporalAccessorUtil.toInstant(temporalAccessor), ZoneUtil.getDefaultZoneId(), toType);
    }

    /**
     * 将{@code LocalDateTime}转换为目标类型的{@code TemporalAccessor}对象
     * @param localDateTime 需要转换的{@code LocalDateTime}对象
     * @param toType 目标类型
     * @return 转换后的{@code TemporalAccessor}对象
     */
    private TemporalAccessor parseFromLocalDateTime(LocalDateTime localDateTime, Class<?> toType) {
        if (TemporalAccessor.class.equals(toType) || LocalDateTime.class.equals(toType)) {
            return localDateTime;
        }
        if (Instant.class.equals(toType)) {
            return TemporalAccessorUtil.toInstant(localDateTime);
        }
        if (LocalDate.class.equals(toType)) {
            return localDateTime.toLocalDate();
        }
        if (LocalTime.class.equals(toType)) {
            return localDateTime.toLocalTime();
        }
        if (ZonedDateTime.class.equals(toType)) {
            return localDateTime.atZone(ZoneUtil.getDefaultZoneId());
        }
        if (OffsetDateTime.class.equals(toType)) {
            return localDateTime.atZone(ZoneUtil.getDefaultZoneId()).toOffsetDateTime();
        }
        if (OffsetTime.class.equals(toType)) {
            return localDateTime.atZone(ZoneUtil.getDefaultZoneId()).toOffsetDateTime().toOffsetTime();
        }
        return null;
    }

    /**
     * 将{@code ZonedDateTime}转换为目标类型的{@code TemporalAccessor}对象
     * @param zonedDateTime 需要转换的{@code ZonedDateTime}对象
     * @param toType 目标类型
     * @return 转换后的{@code TemporalAccessor}对象
     */
    private TemporalAccessor parseFromZonedDateTime(ZonedDateTime zonedDateTime, Class<?> toType) {
        if (TemporalAccessor.class.equals(toType) || ZonedDateTime.class.equals(toType)) {
            return zonedDateTime;
        }
        if (Instant.class.equals(toType)) {
            return TemporalAccessorUtil.toInstant(zonedDateTime);
        }
        if (LocalDateTime.class.equals(toType)) {
            return zonedDateTime.toLocalDateTime();
        }
        if (LocalDate.class.equals(toType)) {
            return zonedDateTime.toLocalDate();
        }
        if (LocalTime.class.equals(toType)) {
            return zonedDateTime.toLocalTime();
        }
        if (OffsetDateTime.class.equals(toType)) {
            return zonedDateTime.toOffsetDateTime();
        }
        if (OffsetTime.class.equals(toType)) {
            return zonedDateTime.toOffsetDateTime().toOffsetTime();
        }
        return null;
    }

    /**
     * 将字符串转换为目标类型的{@code TemporalAccessor}对象
     * @param text 需要转换的字符串
     * @param toType 目标类型
     * @return 转换后的{@code TemporalAccessor}对象
     */
    private TemporalAccessor parseFromText(String text, Class<?> toType) {

        if (DayOfWeek.class.equals(toType)) {
            return DayOfWeek.valueOf(text);
        }
        if (Month.class.equals(toType)) {
            return Month.valueOf(text);
        }
        if (Era.class.equals(toType)) {
            return IsoEra.valueOf(text);
        }
        if (MonthDay.class.equals(toType)) {
            return MonthDay.parse(text);
        }

        TemporalAccessor temporalAccessor = TemporalAccessorUtil.parse(text);
        if (temporalAccessor != null) {
            return parseFromTemporalAccessor(temporalAccessor, toType);
        }

        Date date = DateUtil.parseDate(text);
        if (date != null) {
            return parseFromInstant(((Date) date).toInstant(), ZoneUtil.getDefaultZoneId(), toType);
        }
        return null;
    }
}
