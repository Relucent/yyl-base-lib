/**
 * 日期格式化与解析包，提供{@link com.github.relucent.base.common.time.format.FastDateFormat}等
 * 线程安全的日期格式化工具。<br>
 * <p>
 * 主要包括：
 * <ul>
 * <li>{@link com.github.relucent.base.common.time.format.FastDateFormat} —— {@code SimpleDateFormat}
 * 的快速且线程安全的替代实现，实例可安全地作为静态成员在多线程环境共享；
 * 通过 {@code getInstance} / {@code getDateInstance} / {@code getTimeInstance} / {@code getDateTimeInstance}
 * 系列静态方法获取，相同参数的实例会被 {@code FormatCache} 缓存复用</li>
 * <li>{@link com.github.relucent.base.common.time.format.FastDatePrinter} —— 格式化（日期-&gt;字符串）实现</li>
 * <li>{@link com.github.relucent.base.common.time.format.FastDateParser} —— 解析（字符串-&gt;日期）实现，
 * 支持 Joda 风格的时区模式字母（{@code Z}/{@code X} 系列）</li>
 * <li>{@link com.github.relucent.base.common.time.format.DatePrinter} / {@link com.github.relucent.base.common.time.format.DateParser}
 * —— 打印与解析的行为接口</li>
 * <li>{@link com.github.relucent.base.common.time.format.ThreadLocalDateFormat} —— 基于 {@code ThreadLocal}
 * 的 {@code SimpleDateFormat} 包装，用于无法使用 {@code FastDateFormat} 的场景</li>
 * </ul>
 * <p>
 * 注意事项：
 * <ul>
 * <li>时区模式字母的打印宽度与解析宽度是对称的：{@code Z} 宽度1/2/3 与 {@code X} 宽度1/2/3，
 * 例如 {@code ZZ} 打印 {@code +08:00} 同样可以解析 {@code +08:00}</li>
 * <li>{@code FastDateParser#parse(String, ParsePosition)} 是前缀匹配（{@code lookingAt} 语义），
 * 与 {@code java.text.DateFormat#parse(String, ParsePosition)} 一致，不要求消费完整输入</li>
 * </ul>
 * @author YYL
 */
package com.github.relucent.base.common.time.format;
