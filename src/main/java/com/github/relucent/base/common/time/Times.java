package com.github.relucent.base.common.time;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 时间统一门面<br>
 * <p>
 * 本类是 time 包各工具类（{@link DateUtil}、{@link CalendarUtil}、{@link LocalDateTimeUtil}、{@link InstantUtil}、
 * {@link TemporalUtil}、{@link DurationUtil} 等）常用方法的统一入口，内部全部为委托实现，不包含独立逻辑。<br>
 * 适合不确定该用哪个工具类的场景统一从此处调用；各工具类的完整能力仍以各工具类为准。
 * <pre>{@code
 * Date now = Times.now();
 * String text = Times.formatDateTime(now);           // 2026-09-11 19:00:00
 * Date date = Times.parseDate("2026-09-11");         // 解析
 * Date begin = Times.getBegin(date, DateUnit.WEEK);  // 本周周一 00:00:00.000
 * long days = Times.between(start, end, ChronoUnit.DAYS);
 * }</pre>
 * @author YYL
 */
public class Times {

    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected Times() {
    }

    // ================================================当前时间================================================

    /**
     * 获得当前时间
     * @return 当前时间
     * @see DateUtil#now()
     */
    public static Date now() {
        return DateUtil.now();
    }

    /**
     * 获得当前时间戳（毫秒）
     * @return 当前时间戳（毫秒）
     */
    public static long currentMillis() {
        return System.currentTimeMillis();
    }

    /**
     * 获得最小时间(0001-01-01T00:00:00)
     * @return 最小时间
     * @see DateUtil#min()
     */
    public static Date min() {
        return DateUtil.min();
    }

    /**
     * 获得最大时间(9999-12-31T23:59:59)
     * @return 最大时间
     * @see DateUtil#max()
     */
    public static Date max() {
        return DateUtil.max();
    }

    // ================================================解析与格式化================================================

    /**
     * 根据字符串解析日期，依次尝试多种常见格式<br>
     * 宽容语义：无法解析时返回 {@code null}
     * @param source 日期字符串
     * @return 日期对象，如果不能正确解析返回 {@code null}
     * @see DateUtil#parseDate(String)
     */
    public static Date parseDate(String source) {
        return DateUtil.parseDate(source);
    }

    /**
     * 根据指定的日期格式解析日期<br>
     * 宽容语义：无法解析时返回 {@code null}
     * @param source  日期字符串
     * @param pattern 日期格式
     * @return 日期对象，如果不能正确解析返回 {@code null}
     * @see DateUtil#parseDate(String, String)
     */
    public static Date parseDate(String source, String pattern) {
        return DateUtil.parseDate(source, pattern);
    }

    /**
     * 格式化日期对象为ISO格式字符串
     * @param date 日期对象
     * @return 日期ISO格式字符串
     * @see DateUtil#format(Date)
     */
    public static String format(Date date) {
        return DateUtil.format(date);
    }

    /**
     * 格式化日期对象为日期时间字符串 {@code yyyy-MM-dd HH:mm:ss}
     * @param date 日期对象
     * @return 日期时间字符串
     * @see DateUtil#formatDateTime(Date)
     */
    public static String formatDateTime(Date date) {
        return DateUtil.formatDateTime(date);
    }

    /**
     * 格式化日期对象为日期字符串 {@code yyyy-MM-dd}
     * @param date 日期对象
     * @return 日期字符串
     * @see DateUtil#formatDate(Date)
     */
    public static String formatDate(Date date) {
        return DateUtil.formatDate(date);
    }

    /**
     * 格式化日期对象
     * @param date    日期对象
     * @param pattern 日期格式
     * @return 日期字符串
     * @see DateUtil#format(Date, String)
     */
    public static String format(Date date, String pattern) {
        return DateUtil.format(date, pattern);
    }

    // ================================================类型转换================================================

    /**
     * 转换 {@link Date} 为 {@link Instant} 类型
     * @param date {@link Date}，为 {@code null} 时返回 {@code null}
     * @return {@link Instant}
     * @see InstantUtil#toInstant(Date)
     */
    public static Instant toInstant(Date date) {
        return InstantUtil.toInstant(date);
    }

    /**
     * 转换 {@link Date} 为 {@link LocalDateTime} 类型（默认时区）
     * @param date {@link Date}
     * @return {@link LocalDateTime}
     * @see LocalDateTimeUtil#toLocalDateTime(Date)
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTimeUtil.toLocalDateTime(date);
    }

    /**
     * 转换 {@link LocalDateTime} 为 {@link Date} 类型（默认时区）
     * @param localDateTime {@link LocalDateTime}
     * @return {@link Date}
     * @see DateUtil#toDate(LocalDateTime)
     */
    public static Date toDate(LocalDateTime localDateTime) {
        return DateUtil.toDate(localDateTime);
    }

    /**
     * 转换 {@link Date} 为 {@link Calendar} 类型
     * @param date {@link Date}
     * @return {@link Calendar}
     * @see CalendarUtil#toCalendar(Date)
     */
    public static Calendar toCalendar(Date date) {
        return CalendarUtil.toCalendar(date);
    }

    // ================================================周期取值================================================

    /**
     * 得到某一时间指定周期的起始时间<br>
     * 例如 {@code DateUnit.WEEK} 返回本周周一的 00:00:00.000
     * @param date 指定的时间
     * @param unit 指定的单位类型
     * @return 指定时间指定周期的起始时间
     * @see DateUtil#getBegin(Date, DateUnit)
     */
    public static Date getBegin(Date date, DateUnit unit) {
        return DateUtil.getBegin(date, unit);
    }

    /**
     * 得到某一时间指定周期的結束时间<br>
     * 例如 {@code DateUnit.WEEK} 返回本周周日的 23:59:59.999
     * @param date 指定的时间
     * @param unit 指定的单位类型
     * @return 指定时间指定周期的結束时间
     * @see DateUtil#getEnd(Date, DateUnit)
     */
    public static Date getEnd(Date date, DateUnit unit) {
        return DateUtil.getEnd(date, unit);
    }

    /**
     * 返回给定日期给定周期类型字段的值<br>
     * 取值约定参见 {@link CalendarUtil#getFieldValue(java.util.Calendar, DateUnit)}
     * @param date 时间
     * @param unit 时间周期
     * @return 日历字段的值，不支持的周期类型返回 {@code -1}
     * @see DateUtil#getFieldValue(Date, DateUnit)
     */
    public static int getFieldValue(Date date, DateUnit unit) {
        return DateUtil.getFieldValue(date, unit);
    }

    // ================================================差值与偏移================================================

    /**
     * 获取两个日期的差，如果结束时间早于开始时间，获取结果为负。
     * @param startTime 起始时刻（包括在内）
     * @param endTime 结束时间（不包含）
     * @return 时间差 {@link Duration}对象
     * @see TemporalUtil#between(Temporal, Temporal)
     */
    public static Duration between(Temporal startTime, Temporal endTime) {
        return TemporalUtil.between(startTime, endTime);
    }

    /**
     * 获取两个日期的差，如果结束时间早于开始时间，获取结果为负。
     * @param startTime 起始时刻（包括在内）
     * @param endTime 结束时间（不包含）
     * @param unit 计时单位
     * @return 时间差
     * @see TemporalUtil#between(Temporal, Temporal, ChronoUnit)
     */
    public static long between(Temporal startTime, Temporal endTime, ChronoUnit unit) {
        return TemporalUtil.between(startTime, endTime, unit);
    }

    /**
     * 时间偏移
     * @param <T> 时间对象类型，如{@code LocalDate}或{@code LocalDateTime}
     * @param time {@link Temporal} 时间对象
     * @param amountToAdd 偏移量，正数为向后偏移，负数为向前偏移
     * @param unit 偏移单位，见{@link ChronoUnit}，不能为null
     * @return 偏移后的时间对象
     * @see TemporalUtil#offset(Temporal, long, TemporalUnit)
     */
    public static <T extends Temporal> T offset(T time, long amountToAdd, TemporalUnit unit) {
        return TemporalUtil.offset(time, amountToAdd, unit);
    }

    // ================================================单位转换================================================

    /**
     * 将 {@link TimeUnit} 转换为 {@link ChronoUnit}
     * @param unit 被转换的{@link TimeUnit}单位，如果为{@code null}返回{@code null}
     * @return {@link ChronoUnit}
     * @see TemporalUtil#toChronoUnit(TimeUnit)
     */
    public static ChronoUnit toChronoUnit(TimeUnit unit) {
        return TemporalUtil.toChronoUnit(unit);
    }

    /**
     * 转换 {@link ChronoUnit} 到 {@link TimeUnit}
     * @param unit {@link ChronoUnit}，如果为{@code null}返回{@code null}
     * @return {@link TimeUnit}
     * @see TemporalUtil#toTimeUnit(ChronoUnit)
     */
    public static TimeUnit toTimeUnit(ChronoUnit unit) {
        return TemporalUtil.toTimeUnit(unit);
    }

    /**
     * 将数量和时间单位转换为持续时间
     * @param amount 持续时间的大小，以单位为单位，正数或负数
     * @param timeUnit 时间单位
     * @return {@link Duration}
     * @see DurationUtil#toDuration(long, TimeUnit)
     */
    public static Duration toDuration(long amount, TimeUnit timeUnit) {
        return DurationUtil.toDuration(amount, timeUnit);
    }
}
