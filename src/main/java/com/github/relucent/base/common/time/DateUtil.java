package com.github.relucent.base.common.time;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.github.relucent.base.common.lang.StringUtil;

/**
 * 日期工具类
 */
public class DateUtil {

    /** ISO日期格式 */
    public static final String ISO8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    /** 默认日期格式 */
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /** 默认日期格式 */
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    /** 0001-01-01T00:00:00 */
    public static final Long MIN_MILLIS = -62135798400000L;
    /** 9999-12-31T23:59:59 */
    public static final Long MAX_MILLIS = 253402271999000L;

    /** 零时区格式特殊处理，该格式24个字符 */
    private static final String ISO_8601_ZERO_ZONE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    /** 可解析的日期格式列表 */
    private static final String[] PARSE_DATE_PATTERNS = { //
            ISO8601_FORMAT, //
            DATETIME_FORMAT, //
            "yyyy-MM-dd'T'HH:mm:ss.SSSZZ", //
            "yyyy-MM-dd'T'HH:mm:ss.SSS", //
            "EEE MMM dd HH:mm:ss zzz yyyy", //
            "yyyy-MM-dd HH:mm:ss.SSS", //
            "yyyy-MM-dd HH:mm", //
            "yyyy-MM-dd HH", //
            "yyyy-MM-dd", //
            "yyyy-MM", //
            "d MMM yyyy h:m a", // 30 Jun 2017 2:40 PM
            "MMM d, yyyy HH:mm", //
            "MMM d, yyyy", //
            "MM/dd/yyyy", //
            "yyyyMMdd", //
            "yyyyMM", //
            "yyyy"//
    };

    /** DateFormat线程持有(保证线程安全) */
    private static final ThreadLocal<DateFormat> ISO8601_FORMAT_HOLDER = ThreadLocal.withInitial(() -> new SimpleDateFormat(ISO8601_FORMAT));

    /** DateFormat线程持有(保证线程安全) */
    private static final ThreadLocal<DateFormat> DATETIME_FORMAT_HOLDER = ThreadLocal.withInitial(() -> new SimpleDateFormat(DATETIME_FORMAT));

    /** DateFormat线程持有(保证线程安全) */
    private static final ThreadLocal<DateFormat> DATE_FORMAT_HOLDER = ThreadLocal.withInitial(() -> new SimpleDateFormat(DATE_FORMAT));

    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected DateUtil() {
    }

    /**
     * 获得当前时间
     * @return 当前时间
     */
    public static Date now() {
        return new Date();
    }

    /**
     * 获得最小时间(0001-01-01T00:00:00)
     * @return 最小时间
     */
    public static Date min() {
        return new Date(MIN_MILLIS);
    }

    /**
     * 获得最大时间(9999-12-31T23:59:59)
     * @return 最大时间
     */
    public static Date max() {
        return new Date(MAX_MILLIS);
    }

    /**
     * 转换 {@link LocalDate} 为 {@link Calendar} 类型
     * @param localDate {@link LocalDate}
     * @return {@link Date}
     */
    public static Date toDate(final LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneUtil.getDefaultZoneId()).toInstant());
    }

    /**
     * 转换 {@link LocalDateTime} 为 {@link Calendar} 类型
     * @param localDateTime {@link LocalDateTime}
     * @return {@link Date}
     */
    public static Date toDate(final LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneUtil.getDefaultZoneId()).toInstant());
    }

    /**
     * 根据字符串解析日期
     * @param source 日期字符串
     * @return 日期对象，如果不能正确解析返回 {@code null}
     */
    public static Date parseDate(String source) {
        if (StringUtil.isBlank(source)) {
            return null;
        }
        try {
            return forceParseDate(source);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据指定的日期格式解析日期
     * @param source 日期字符串
     * @param pattern 日期格式
     * @return 日期对象，如果不能正确解析返回 {@code null}
     */
    public static Date parseDate(String source, String pattern) {
        if (StringUtil.isBlank(source)) {
            return null;
        }
        try {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            return format.parse(source);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 格式化日期对象为ISO格式字符串
     * @param date 日期对象
     * @return 日期ISO格式字符串
     */
    public static String format(Date date) {
        return format(date, ISO8601_FORMAT_HOLDER.get());
    }

    /**
     * 格式化日期对象为日期时间字符串<code>yyyy-MM-dd HH:mm:ss</code>
     * @param date 日期对象
     * @return 日期时间字符串
     */
    public static String formatDateTime(Date date) {
        return format(date, DATETIME_FORMAT_HOLDER.get());
    }

    /**
     * 格式化日期对象为日期字符串 <code>yyyy-MM-dd</code>
     * @param date 日期对象
     * @return 日期字符串
     */
    public static String formatDate(Date date) {
        return format(date, DATE_FORMAT_HOLDER.get());
    }

    /**
     * 格式化日期对象
     * @param date 日期对象
     * @param pattern 日期格式
     * @return 日期字符串
     */
    public static String format(Date date, String pattern) {
        return format(date, new SimpleDateFormat(pattern));
    }

    /**
     * 得到某一时间指定周期的起始时间
     * @param date 指定的时间
     * @param unit 指定的单位类型
     * @return 指定时间指定周期的起始时间
     */
    public static Date getBegin(Date date, DateUnit unit) {
        if (date == null) {
            return null;
        }
        return CalendarUtil.getBegin(CalendarUtil.toCalendar(date), unit).getTime();
    }

    /**
     * 得到某一时间指定周期的結束时间
     * @param date 指定的时间
     * @param unit 指定的单位类型
     * @return 指定时间指定周期的結束时间
     */
    public static Date getEnd(Date date, DateUnit unit) {
        if (date == null) {
            return null;
        }
        return CalendarUtil.getEnd(CalendarUtil.toCalendar(date), unit).getTime();
    }

    /**
     * 返回给定日历给定周期类型字段的值。
     * @param date 时间
     * @param unit 时间周期
     * @return 所在季度(0-1)
     */
    public static int getFieldValue(Date date, DateUnit unit) {
        return CalendarUtil.getFieldValue(CalendarUtil.toCalendar(date), unit);
    }

    /**
     * 获得两个时间之中最大的时间
     * @param a 第一个时间
     * @param b 第二个时间
     * @return 两个时间之中最大的时间
     */
    public static Date max(Date a, Date b) {
        return a.before(b) ? b : a; // a<b?b:a
    }

    /**
     * 获得两个时间之中最大的时间
     * @param a 第一个时间
     * @param b 第二个时间
     * @return 两个时间之中最大的时间
     */
    public static Date min(Date a, Date b) {
        return b.after(a) ? a : b; // b>a?a:b
    }

    /**
     * 格式化日期对象
     * @param date 日期对象
     * @param format 格式化工具类
     * @return 日期对象字符串
     */
    private static String format(Date date, DateFormat format) {
        if (date == null) {
            return null;
        }
        try {
            return format.format(date);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 通过尝试各种不同的解析器来解析表示日期的字符串。<br>
     * 解析将依次尝试每个解析模式，只有解析整个输入字符串，解析才会被视为成功，解析器将对解析的日期保持宽容。<br>
     * @param source 要分析的日期字符
     * @return 日期对象
     * @throws ParseException 日期无法解析
     */
    private static Date forceParseDate(final String source) throws ParseException {
        if (StringUtil.isBlank(source)) {
            throw new IllegalArgumentException("date string must not be blank");
        }
        SimpleDateFormat parser = new SimpleDateFormat();
        parser.setLenient(true);
        ParsePosition pos = new ParsePosition(0);

        // ISO# yyyy-MM-dd'T'HH:mm:ss.SSSZ (JSON.stringify)
        if (source.length() == 24 && source.charAt(23) == 'Z') {
            parser.applyPattern(ISO_8601_ZERO_ZONE_FORMAT);
            pos.setIndex(0);
            Date date = parser.parse(source, pos);
            if (date != null && pos.getIndex() == source.length()) {
                return new Date(date.getTime() + TimeZone.getDefault().getRawOffset());
            }
        }

        for (final String parsePattern : PARSE_DATE_PATTERNS) {
            String pattern = parsePattern;
            // LANG-530 - need to make sure 'ZZ' output doesn't get passed to SimpleDateFormat
            if (parsePattern.endsWith("ZZ")) {
                pattern = pattern.substring(0, pattern.length() - 1);
            }
            parser.applyPattern(pattern);
            pos.setIndex(0);
            String string = source;
            // LANG-530 - need to make sure 'ZZ' output doesn't hit SimpleDateFormat as it will ParseException
            if (parsePattern.endsWith("ZZ")) {
                int signIdx = indexOfSignChars(string, 0);
                while (signIdx >= 0) {
                    string = reformatTimezone(string, signIdx);
                    signIdx = indexOfSignChars(string, ++signIdx);
                }
            }
            Date date = parser.parse(string, pos);
            if (date != null && pos.getIndex() == string.length()) {
                return date;
            }
        }
        throw new ParseException("Unable to parse the date: " + source, -1);
    }

    // 重新格式化时区
    private static String reformatTimezone(String str, int signIdx) {
        String result = str;
        if (signIdx >= 0 //
                && signIdx + 5 < str.length() //
                && Character.isDigit(str.charAt(signIdx + 1)) //
                && Character.isDigit(str.charAt(signIdx + 2)) //
                && str.charAt(signIdx + 3) == ':' //
                && Character.isDigit(str.charAt(signIdx + 4)) //
                && Character.isDigit(str.charAt(signIdx + 5))) {
            result = str.substring(0, signIdx + 3) + str.substring(signIdx + 4);
        }
        return result;
    }

    // 获得符号字符位置
    private static int indexOfSignChars(String str, int startPos) {
        int idx = str.indexOf('+', startPos);
        if (idx != -1) {
            return idx;
        }
        return str.indexOf('-', startPos);
    }
}
