package com.github.relucent.base.common.time.format;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 日期格式化输出接口<br>
 * 实现了{@link java.text.DateFormat} 缺失的功能。<br>
 * 参考： org.apache.commons.lang3.time.DateParser<br>
 */
public interface DateParser {

    /**
     * 将日期字符串解析为 {@link Date} 对象<br>
     * 等价于 {@link java.text.DateFormat#parse(String)}
     * @param source 要解析的日期字符串
     * @return {@link Date}对象
     * @throws ParseException 解析异常，被转换的字符串格式错误。
     */
    Date parse(String source) throws ParseException;

    /**
     * 将日期字符串解析为 {@link Date} 对象<br>
     * 等价于 {@link java.text.DateFormat#parse(String,ParsePosition)}
     * @param source 要解析的日期字符串
     * @param pos {@code ParsePosition} 解析位置对象，指定开始解析的位置，输出时会更新。
     * @return {@link Date}对象。如果出现错误，则返回null。
     * @throws NullPointerException 如果{@code source}或{@code pos}为空
     */
    Date parse(String source, ParsePosition pos);

    /**
     * 将日期字符串解析为日期<br>
     * @param source 要解析的日期字符串
     * @return 日期对象{@code java.util.Date}
     * @throws ParseException 解析异常，被转换的字符串格式错误。
     * @see java.text.DateFormat#parseObject(String)
     */
    Object parseObject(String source) throws ParseException;

    /**
     * 根据给定的解析位置将字符串解析为日期/时间<br>
     * @param source 要解析的日期字符串
     * @param pos {@code ParsePosition} 解析位置对象，指定开始解析的位置，输出时会更新。
     * @return 日期对象{@code java.util.Date}
     * @see java.text.DateFormat#parseObject(String, ParsePosition)
     */
    Object parseObject(String source, ParsePosition pos);

    /**
     * 根据格式解析日期字符串，并使用解析的结果更新日历对象。<br>
     * 成功后，将更新ParsePosition索引，以指示消耗了多少源文本。<br>
     * 如果解析失败，ParsePosition错误索引将更新为与提供的格式不匹配的源文本的偏移量。<br>
     * @param source 要解析的日期字符串
     * @param pos 解析位置对象，指定开始解析的位置，输出时会更新
     * @param calendar 要设置的日历对象
     * @return 如果解析成功，返回{@code true}并更新{@code pos}；如果解析失败返回{@code false}，并将错误索引更新到{@code pos}
     */
    boolean parse(String source, ParsePosition pos, Calendar calendar);

    /**
     * 获取日期格式（与{@link java.text.SimpleDateFormat}兼容）
     * @return 日期格式
     */
    String getPattern();

    /**
     * 获得时区
     * @return {@link TimeZone}
     */
    TimeZone getTimeZone();

    /**
     * 获得 日期地理位置
     * @return {@link Locale}
     */
    Locale getLocale();
}
