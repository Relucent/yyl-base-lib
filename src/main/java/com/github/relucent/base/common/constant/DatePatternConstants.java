package com.github.relucent.base.common.constant;

/**
 * 日期格式常量
 */
public class DatePatternConstants {

    /** 标准日期时间格式（精确到秒）：yyyy-MM-dd HH:mm:ss */
    public static final String NORM_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /** 标准日期格式：yyyy-MM-dd */
    public static final String NORM_DATE_PATTERN = "yyyy-MM-dd";

    /** 标准时间格式：HH:mm:ss */
    public static final String NORM_TIME_PATTERN = "HH:mm:ss";

    /** 标准日期时间格式（精确到毫秒）：yyyy-MM-dd HH:mm:ss.SSS */
    public static final String NORM_DATETIME_MS_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    /** 标准日期时间格式（精确到分钟）：yyyy-MM-dd HH:mm */
    public static final String NORM_DATETIME_MINUTE_PATTERN = "yyyy-MM-dd HH:mm";

    /** 中文日期格式：yyyy年MM月dd日 */
    public static final String CHINESE_DATE_PATTERN = "yyyy年MM月dd日";

    /** 中文时间格式 ：HH时mm分ss秒 */
    public static final String CHINESE_TIME_PATTERN = "HH时mm分ss秒";

    /** 纯数字日期时间格式（精确到毫秒）：yyyyMMddHHmmssSSS */
    public static final String PURE_DATETIME_MS_PATTERN = "yyyyMMddHHmmssSSS";

    /** 纯数字日期时间格式：yyyyMMddHHmmss */
    public static final String PURE_DATETIME_PATTERN = "yyyyMMddHHmmss";

    /** 纯数字日期格式：yyyyMMdd */
    public static final String PURE_DATE_PATTERN = "yyyyMMdd";

    /** 纯数字时间格式：HHmmss */
    public static final String PURE_TIME_PATTERN = "HHmmss";

    /** ISO_8601 带时区的日期格式：yyyy-MM-dd'T'HH:mm:ss */
    public static final String ISO_8601_DATETIME_TIME_ZONE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZZ";

    /** ISO_8601 日期格式：yyyy-MM-dd'T'HH:mm:ss */
    public static final String ISO_8601_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    /** SMTP(电子邮件传输的协议/HTTP头)日期时间格式：EEE, dd MMM yyyy HH:mm:ss z */
    public static final String SMTP_DATETIME_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z";

    /** JDK中日期时间格式：EEE MMM dd HH:mm:ss zzz yyyy */
    public static final String JDK_DATETIME_PATTERN = "EEE MMM dd HH:mm:ss zzz yyyy";
}
