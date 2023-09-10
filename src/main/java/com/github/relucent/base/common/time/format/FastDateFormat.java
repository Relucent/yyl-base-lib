package com.github.relucent.base.common.time.format;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

/**
 * {@code FastDateFormat}是{@link java.text.SimpleDateFormat} 的一个快速且线程安全的版本实现。<br>
 * {@code FastDateFormat}是线程安全的，因此可以作为静态成员实例，并在多线程环境中安全使用。<br>
 * 可以通过以下静态方法获得此对象: <br>
 * {@link #getInstance(String, TimeZone, Locale)}<br>
 * {@link #getDateInstance(int, TimeZone, Locale)}<br>
 * {@link #getTimeInstance(int, TimeZone, Locale)}<br>
 * {@link #getDateTimeInstance(int, int, TimeZone, Locale)}<br>
 * 参考：org.apache.commons.lang3.time.FastDateFormat<br>
 */
public class FastDateFormat extends Format implements DateParser, DatePrinter {

    // =================================Static=================================================
    /** 序列化支持 */
    private static final long serialVersionUID = 1L;

    /**
     * 完全长度的日期或时间样式。例如：“1970年1月1日 星期四”或“Thursday, January 1, 1970”
     */
    public static final int FULL = DateFormat.FULL;
    /**
     * 较长的日期时间样式。例如：“1970年1月1日”或“January 1, 1970”
     */
    public static final int LONG = DateFormat.LONG;
    /**
     * 中等长度的时间样式。例如：“1970-1-1”或“Jan 1, 1970”
     */
    public static final int MEDIUM = DateFormat.MEDIUM;
    /**
     * 较短的日期时间样式。例如：“70-1-1”或“1/1/70”
     */
    public static final int SHORT = DateFormat.SHORT;

    /** FastDateFormat缓存 */
    private static final FormatCache<FastDateFormat> CACHE = new FormatCache<FastDateFormat>() {
        @Override
        protected FastDateFormat createInstance(final String pattern, final TimeZone timeZone, final Locale locale) {
            return new FastDateFormat(pattern, timeZone, locale);
        }
    };

    // =================================Instances==============================================
    /**
     * 获得默认的格式化程序实例。
     * @return 默认的日期格式化器
     */
    public static FastDateFormat getInstance() {
        return CACHE.getInstance();
    }

    /**
     * 获得一个使用指定日期格式、默认时区和默认地区的日期格式化器
     * @param pattern 日期格式（与{@link java.text.SimpleDateFormat}兼容）
     * @return 指定格式的日期格式化器
     * @throws IllegalArgumentException 如果日期格式{@code pattern}无效
     */
    public static FastDateFormat getInstance(final String pattern) {
        return CACHE.getInstance(pattern, null, null);
    }

    /**
     * 获得一个使用指定日期格式和地区的日期格式化器
     * @param pattern 日期格式（与{@link java.text.SimpleDateFormat}兼容）
     * @param timeZone {@link TimeZone} 时区
     * @return 指定格式的日期格式化器
     * @throws IllegalArgumentException 如果日期格式{@code pattern}无效
     */
    public static FastDateFormat getInstance(final String pattern, final TimeZone timeZone) {
        return CACHE.getInstance(pattern, timeZone, null);
    }

    /**
     * 获得一个指定日期格式和地区的日期格式化器
     * @param pattern 日期格式（与{@link java.text.SimpleDateFormat}兼容）
     * @param locale 地区（可选），覆盖系统区域设置
     * @return 指定格式的日期格式化器
     * @throws IllegalArgumentException 如果日期格式{@code pattern}无效
     */
    public static FastDateFormat getInstance(final String pattern, final Locale locale) {
        return CACHE.getInstance(pattern, null, locale);
    }

    /**
     * 获得一个指定日期格式、时区和地区的日期格式化器
     * @param pattern 日期格式（与{@link java.text.SimpleDateFormat}兼容）
     * @param timeZone {@link TimeZone} 时区
     * @param locale 地区（可选），覆盖系统区域设置
     * @return 指定格式的日期格式化器
     * @throws IllegalArgumentException 如果日期格式{@code pattern}无效或者为{@code null}
     */
    public static FastDateFormat getInstance(final String pattern, final TimeZone timeZone, final Locale locale) {
        return CACHE.getInstance(pattern, timeZone, locale);
    }

    // -----------------------------------------------------------------------
    /**
     * 获得一个指定日期样式、默认时区和默认地区的日期格式化器
     * @param style 日期样式 : FULL、LONG、MEDIUM、SHORT
     * @return 指定日期样式的日期格式化器
     * @throws IllegalArgumentException 如果该地区没有定义日期样式
     */
    public static FastDateFormat getDateInstance(final int style) {
        return CACHE.getDateInstance(style, null, null);
    }

    /**
     * 获得一个指定日期样式和地区的日期格式化器
     * @param style 日期样式 : FULL、LONG、MEDIUM、SHORT
     * @param locale {@link Locale} 地区
     * @return 指定日期样式的日期格式化器
     * @throws IllegalArgumentException 如果该地区没有定义日期样式
     */
    public static FastDateFormat getDateInstance(final int style, final Locale locale) {
        return CACHE.getDateInstance(style, null, locale);
    }

    /**
     * 获得一个指定日期样式和时区的日期格式化器
     * @param style 日期样式 : FULL、LONG、MEDIUM、SHORT
     * @param timeZone {@link TimeZone} 时区
     * @return 指定日期样式的日期格式化器
     * @throws IllegalArgumentException 如果该地区没有定义日期样式
     */
    public static FastDateFormat getDateInstance(final int style, final TimeZone timeZone) {
        return CACHE.getDateInstance(style, timeZone, null);
    }

    /**
     * 获得一个指定日期样式、时区和地区的日期格式化器
     * @param style 日期样式 : FULL、LONG、MEDIUM、SHORT
     * @param timeZone {@link TimeZone} 时区
     * @param locale {@link Locale} 地区
     * @return 指定日期样式的日期格式化器
     * @throws IllegalArgumentException 如果该地区没有定义日期样式
     */
    public static FastDateFormat getDateInstance(final int style, final TimeZone timeZone, final Locale locale) {
        return CACHE.getDateInstance(style, timeZone, locale);
    }

    // -----------------------------------------------------------------------
    /**
     * 获得一个指定日期样式、默认时区和默认地区的时间格式化器
     * @param style 时间样式 : FULL、LONG、MEDIUM、SHORT
     * @return 指定时间样式的时间格式化器
     * @throws IllegalArgumentException 如果该地区没有定义时间样式
     */
    public static FastDateFormat getTimeInstance(final int style) {
        return CACHE.getTimeInstance(style, null, null);
    }

    /**
     * 获得一个指定时间样式和地区的时间格式化器
     * @param style 时间样式 : FULL、LONG、MEDIUM、SHORT
     * @param locale {@link Locale} 地区
     * @return 指定时间样式的时间格式化器
     * @throws IllegalArgumentException 如果该地区没有定义时间样式
     */
    public static FastDateFormat getTimeInstance(final int style, final Locale locale) {
        return CACHE.getTimeInstance(style, null, locale);
    }

    /**
     * 获得一个指定时间样式和时区的时间格式化器
     * @param style 时间样式 : FULL、LONG、MEDIUM、SHORT
     * @param timeZone {@link TimeZone} 时区
     * @return 指定时间样式的时间格式化器
     * @throws IllegalArgumentException 如果该地区没有定义时间样式
     */
    public static FastDateFormat getTimeInstance(final int style, final TimeZone timeZone) {
        return CACHE.getTimeInstance(style, timeZone, null);
    }

    /**
     * 获得一个指定时间样式、时区和地区的时间格式化器
     * @param style 时间样式 : FULL、LONG、MEDIUM、SHORT
     * @param timeZone {@link TimeZone} 时区
     * @param locale {@link Locale} 地区
     * @return 指定时间样式的时间格式化器
     * @throws IllegalArgumentException 如果该地区没有定义时间样式
     */
    public static FastDateFormat getTimeInstance(final int style, final TimeZone timeZone, final Locale locale) {
        return CACHE.getTimeInstance(style, timeZone, locale);
    }

    // -----------------------------------------------------------------------
    /**
     * 获得一个指定日期样式、时间样式、默认时区和默认地区的日期时间格式化器
     * @param dateStyle 日期样式 : FULL、LONG、MEDIUM、SHORT
     * @param timeStyle 时间样式 : FULL、LONG、MEDIUM、SHORT
     * @return 指定时间样式的日期时间格式化器
     * @throws IllegalArgumentException 如果该地区没有定义时间样式或者日期样式
     */
    public static FastDateFormat getDateTimeInstance(final int dateStyle, final int timeStyle) {
        return CACHE.getDateTimeInstance(dateStyle, timeStyle, null, null);
    }

    /**
     * 获得一个指定日期样式、时间样式和地区的日期时间格式化器
     * @param dateStyle 日期样式 : FULL、LONG、MEDIUM、SHORT
     * @param timeStyle 时间样式 : FULL、LONG、MEDIUM、SHORT
     * @param locale {@link Locale} 地区
     * @return 指定时间样式的日期时间格式化器
     * @throws IllegalArgumentException 如果该地区没有定义时间样式或者日期样式
     */
    public static FastDateFormat getDateTimeInstance(final int dateStyle, final int timeStyle, final Locale locale) {
        return CACHE.getDateTimeInstance(dateStyle, timeStyle, null, locale);
    }

    /**
     * 获得一个指定日期样式、时间样式和时区的日期时间格式化器
     * @param dateStyle 日期样式 : FULL、LONG、MEDIUM、SHORT
     * @param timeStyle 时间样式 : FULL、LONG、MEDIUM、SHORT
     * @param timeZone {@link TimeZone} 时区
     * @return 指定时间样式的日期时间格式化器
     * @throws IllegalArgumentException 如果该地区没有定义时间样式或者日期样式
     */
    public static FastDateFormat getDateTimeInstance(final int dateStyle, final int timeStyle, final TimeZone timeZone) {
        return getDateTimeInstance(dateStyle, timeStyle, timeZone, null);
    }

    /**
     * 获得一个指定日期样式、时间样式、时区和地区的日期时间格式化器
     * @param dateStyle 日期样式 : FULL、LONG、MEDIUM、SHORT
     * @param timeStyle 时间样式 : FULL、LONG、MEDIUM、SHORT
     * @param timeZone {@link TimeZone} 时区
     * @param locale {@link Locale} 地区
     * @return 指定时间样式的日期时间格式化器
     * @throws IllegalArgumentException 如果该地区没有定义时间样式或者日期样式
     */
    public static FastDateFormat getDateTimeInstance(final int dateStyle, final int timeStyle, final TimeZone timeZone, final Locale locale) {
        return CACHE.getDateTimeInstance(dateStyle, timeStyle, timeZone, locale);
    }

    // =================================Fields=================================================
    /** 打印器（用于将日期对象输出成符合格式的字符串） */
    private final FastDatePrinter printer;
    /** 解析器 （用于将日期字符串解析成日期对象） */
    private final FastDateParser parser;

    // =================================Constructors===========================================
    /**
     * 构造函数
     * @param pattern 与{@link java.text.SimpleDateFormat}兼容的日期格式
     * @param timeZone {@link TimeZone} 时区
     * @param locale {@link Locale} 地区
     * @throws NullPointerException 如果 pattern、timeZone、locale 为{@code null}
     */
    protected FastDateFormat(final String pattern, final TimeZone timeZone, final Locale locale) {
        this(pattern, timeZone, locale, null);
    }

    /**
     * 构造函数
     * @param pattern 与{@link java.text.SimpleDateFormat}兼容的日期格式
     * @param timeZone {@link TimeZone} 时区
     * @param locale {@link Locale} 地区
     * @param centuryStart 世纪开始时间， 100年期间的开始用作2位数年份解析的“默认世纪”。如果centuryStart为null，则默认为now-80年
     * @throws NullPointerException 如果 pattern、timeZone、locale 为{@code null}
     */
    protected FastDateFormat(final String pattern, final TimeZone timeZone, final Locale locale, final Date centuryStart) {
        printer = new FastDatePrinter(pattern, timeZone, locale);
        parser = new FastDateParser(pattern, timeZone, locale, centuryStart);
    }

    // =================================FormatMethods==========================================
    /**
     * 格式化时间的对象<br>
     * 包括：{@code Date}、{@code Calendar}和{@code Long}（毫秒）对象<br>
     * {@link Format#format(Object, StringBuffer, FieldPosition)}<br>
     * @param obj 要格式化的对象
     * @param toAppendTo 要追加内容的字符缓冲器
     * @param pos 位置（该参数没有被使用，可以忽略）
     * @return 追加内容的字符缓冲器
     */
    @Override
    public StringBuffer format(final Object obj, final StringBuffer toAppendTo, final FieldPosition pos) {
        return toAppendTo.append(printer.format(obj));
    }

    /**
     * 格式化毫秒{@code long}
     * @param millis 要格式化的毫秒值
     * @return 格式化后的日期时间字符串
     */
    @Override
    public String format(final long millis) {
        return printer.format(millis);
    }

    /**
     * 格式化 {@code Date}对象，使用{@code GregorianCalendar}（格里高利历）
     * @param date 要格式化的日期对象
     * @return 格式化后的日期时间字符串
     */
    @Override
    public String format(final Date date) {
        return printer.format(date);
    }

    /**
     * 格式化 {@code Calendar}对象
     * @param calendar 要格式化的日历对象
     * @return 格式化后的日期时间字符串
     */
    @Override
    public String format(final Calendar calendar) {
        return printer.format(calendar);
    }

    /**
     * 格式化毫秒{@code long}，并将结果追加到字符缓冲器 {@code StringBuffer}中
     * @param <B> 字符缓冲器{@code Appendable}的类型，通常是StringBuilder或StringBuffer
     * @param millis 要格式化的毫秒值t
     * @param buf 要追加内容的字符缓冲器
     * @return 追加内容的字符缓冲器
     */
    @Override
    public <B extends Appendable> B format(final long millis, final B buf) {
        return printer.format(millis, buf);
    }

    /**
     * 格式化日期对象{@code Date}，并将结果追加到字符缓冲器 {@code StringBuffer}中
     * @param <B> 字符缓冲器{@code Appendable}的类型，通常是StringBuilder或StringBuffer
     * @param date 要格式化的日期对象
     * @param buf 要追加内容的字符缓冲器
     * @return 追加内容的字符缓冲器
     */
    @Override
    public <B extends Appendable> B format(final Date date, final B buf) {
        return printer.format(date, buf);
    }

    /**
     * 格式化日历对象{@code Calendar}，并将结果追加到字符缓冲器 {@code StringBuffer}中
     * @param <B> 字符缓冲器{@code Appendable}的类型，通常是StringBuilder或StringBuffer
     * @param calendar 要格式化的日历对象
     * @param buf 要追加内容的字符缓冲器
     * @return 追加内容的字符缓冲器
     */
    @Override
    public <B extends Appendable> B format(final Calendar calendar, final B buf) {
        return printer.format(calendar, buf);
    }

    // =================================ParsingMethods=========================================
    @Override
    public Date parse(final String source) throws ParseException {
        return parser.parse(source);
    }

    @Override
    public Date parse(final String source, final ParsePosition pos) {
        return parser.parse(source, pos);
    }

    @Override
    public boolean parse(final String source, final ParsePosition pos, final Calendar calendar) {
        return parser.parse(source, pos, calendar);
    }

    @Override
    public Object parseObject(final String source, final ParsePosition pos) {
        return parser.parseObject(source, pos);
    }

    // =================================AccessorsMethods=======================================
    /**
     * 获取日期格式（与{@link java.text.SimpleDateFormat}兼容）
     * @return 日期格式
     */
    @Override
    public String getPattern() {
        return printer.getPattern();
    }

    /**
     * 获得格式化器所使用的时区
     * @return {@link TimeZone}时区
     */
    @Override
    public TimeZone getTimeZone() {
        return printer.getTimeZone();
    }

    /**
     * 获得格式化器所使用的地区
     * @return {@link TimeZone}地区
     */
    @Override
    public Locale getLocale() {
        return printer.getLocale();
    }

    /**
     * 获取将生成字符串最大长度的估计值。<br>
     * 实际格式化生成的日期字符串长度总是小于或等于这个数值。
     * @return 格式化后的日期字符串最大长度
     */
    public int getMaxLengthEstimate() {
        return printer.getMaxLengthEstimate();
    }

    // =================================BasicMethods===========================================
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof FastDateFormat)) {
            return false;
        }
        final FastDateFormat other = (FastDateFormat) obj;
        // 无需检查 parser，因为它与printer具有相同的不变量（Pattern、 Locale、TimeZone）
        return printer.equals(other.printer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(printer);
    }

    @Override
    public String toString() {
        return "FastDateFormat[" + printer.getPattern() + "," + printer.getLocale() + "," + printer.getTimeZone().getID() + "]";
    }
}
