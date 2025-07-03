package com.github.relucent.base.common.time;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 时间戳 {@link Instant} 工具类封装
 */
public class InstantUtil {
    // ==============================Constructors=====================================
    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected InstantUtil() {
    }

    // ==============================Methods==========================================

    /**
     * 格式化时间戳时间，使用的是 ISO-8601 标准格式，以 UTC（0时区） 表示时间
     * @param instant 带时间戳
     * @return 日期时间文本（UTC零时区）
     */
    public static String formatUtc(Instant instant) {
        return instant == null ? null : DateTimeFormatter.ISO_INSTANT.format(instant);
    }

    /**
     * 转换时间对象为时间戳对象
     * @param date 时间对象
     * @return 时间戳对象
     */
    public static Instant toInstant(Date date) {
        return date == null ? null : date.toInstant();
    }

    /**
     * 转换毫秒为时间戳对象
     * @param epochMilli 毫秒
     * @return 时间戳对象
     */
    public static Instant toInstant(Long epochMilli) {
        return epochMilli == null ? null : Instant.ofEpochMilli(epochMilli);
    }
}
