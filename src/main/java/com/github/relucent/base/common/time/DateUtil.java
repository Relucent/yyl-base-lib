package com.github.relucent.base.common.time;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

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

    /** 可解析的日期格式列表 */
    private final static String[] PARSE_DATE_PATTERNS = Arrays.asList(//
            ISO8601_FORMAT, //
            DATETIME_FORMAT, //
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
    ).toArray(new String[0]);

    /** DateFormat线程持有(保证线程安全) */
    private static ThreadLocal<DateFormat> ISO8601_FORMAT_HOLDER = new ThreadLocal<DateFormat>() {
        protected DateFormat initialValue() {
            return new SimpleDateFormat(ISO8601_FORMAT);
        };
    };

    /** DateFormat线程持有(保证线程安全) */
    private static ThreadLocal<DateFormat> DATETIME_FORMAT_HOLDER = new ThreadLocal<DateFormat>() {
        protected DateFormat initialValue() {
            return new SimpleDateFormat(DATETIME_FORMAT);
        };
    };
    /** DateFormat线程持有(保证线程安全) */
    private static ThreadLocal<DateFormat> DATE_FORMAT_HOLDER = new ThreadLocal<DateFormat>() {
        protected DateFormat initialValue() {
            return new SimpleDateFormat(DATE_FORMAT);
        };
    };

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
     * 根据字符串解析日期
     * @param source 日期字符串
     * @return 日期对象
     */
    public static Date parseDate(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        try {
            return forceParseDate(source);
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
     * @param format 格式
     * @return 格式化后的日期对象
     */
    public static String format(Date date, String format) {
        return format(date, new SimpleDateFormat(format));
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
     * 解析日期字符串
     * @param source 日期字符串
     * @return 日期对象
     */
    private static Date forceParseDate(final String source) throws ParseException {
        if (source == null) {
            throw new IllegalArgumentException("date string must not be null");
        }
        SimpleDateFormat parser = new SimpleDateFormat();
        parser.setLenient(true);
        ParsePosition pos = new ParsePosition(0);
        for (final String parsePattern : PARSE_DATE_PATTERNS) {
            String pattern = parsePattern;
            // LANG-530 - need to make sure 'ZZ' output doesn't get passed to SimpleDateFormat
            if (pattern.endsWith("ZZ")) {
                pattern = pattern.substring(0, pattern.length() - 1);
            }
            parser.applyPattern(pattern);
            pos.setIndex(0);
            String str = source;
            // LANG-530 - need to make sure 'ZZ' output doesn't hit SimpleDateFormat as it will ParseException
            if (pattern.endsWith("ZZ")) {
                int signIdx = indexOfSignChars(str, 0);
                while (signIdx != -1) {
                    str = reformatTimezone(str, signIdx);
                    signIdx = indexOfSignChars(str, ++signIdx);
                }
            }
            Date date = parser.parse(str, pos);
            if (date != null && pos.getIndex() == str.length()) {
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
