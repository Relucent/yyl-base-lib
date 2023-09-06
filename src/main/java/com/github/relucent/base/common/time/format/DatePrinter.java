package com.github.relucent.base.common.time.format;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 日期格式化输出接口<br>
 * 实现了{@link java.text.DateFormat} 缺失的功能。<br>
 * 参考： org.apache.commons.lang3.time.DatePrinter<br>
 */
public interface DatePrinter {

    /**
     * 格式化日期表示的毫秒数
     * @param millis 日期毫秒数
     * @return 格式化后的字符串
     */
    String format(long millis);

    /**
     * 格式化 {@code Date}对象
     * @param date 要格式化的日期 对象 {@link Date}
     * @return 格式化后的字符串
     */
    String format(Date date);

    /**
     * 格式化 {@code Calendar}对象
     * @param calendar 要格式化的日历{@link Calendar}对象
     * @return 格式化后的字符串
     */
    String format(Calendar calendar);

    /**
     * 格式化日期表示的毫秒数{@code long}，并将格式化结果添加到{@code Appendable}.
     * @param <B> 字符串缓冲区类型，通常是StringBuilder或StringBuffer
     * @param millis 要格式化的毫秒值
     * @param buf 要添加内容的字符串缓冲区
     * @return 指定的字符串缓冲区
     */
    <B extends Appendable> B format(long millis, B buf);

    /**
     * * 格式化日期{@code Date}，并将格式化结果添加到{@code Appendable}
     * @param <B> 字符串缓冲区类型，通常是StringBuilder或StringBuffer
     * @param date 要格式化的日期{@link Date}对象
     * @param buf 要添加内容的字符串缓冲区
     * @return 指定的字符串缓冲区
     */
    <B extends Appendable> B format(Date date, B buf);

    /**
     * 格式化日期{@code Calendar}，并将格式化结果添加到{@code Appendable}
     * @param <B> 字符串缓冲区类型，通常是StringBuilder或StringBuffer
     * @param calendar 要格式化的日历{@link Calendar}对象
     * @param buf 要添加内容的字符串缓冲区
     * @return 指定的字符串缓冲区
     */
    <B extends Appendable> B format(Calendar calendar, B buf);

    /**
     * 获得日期格式化或者转换的格式
     * @return {@link java.text.SimpleDateFormat}兼容的格式
     */
    String getPattern();

    /**
     * 获得时区
     * @return {@link TimeZone}
     */
    TimeZone getTimeZone();

    /**
     * 获得日期地理位置
     * @return {@link Locale}
     */
    Locale getLocale();
}
