package com.github.relucent.base.common.time.format;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.github.relucent.base.common.lang.StringUtil;

/**
 * 线程隔离的日期格式化工具类
 */
public class ThreadLocalDateFormat {

    /** 线程持有者 */
    private final ThreadLocal<DateFormat> threadLocal;

    /**
     * 构造函数
     * @param pattern 日期格式
     */
    public ThreadLocalDateFormat(String pattern) {
        threadLocal = ThreadLocal.withInitial(() -> new SimpleDateFormat(pattern));
    }

    /**
     * 格式化日期对象
     * @param date 要格式化为日期对象
     * @return 日期字符串
     */
    public final String format(Date date) {
        return date == null ? null : threadLocal.get().format(date);
    }

    /**
     * 解析日期字符串
     * @param date 要解析的日期字符串
     * @return 日期对象
     * @throws ParseException 如果解析失败抛出该异常
     */
    public final Date parse(String source) throws ParseException {
        return StringUtil.isEmpty(source) ? null : threadLocal.get().parse(source);
    }

    /**
     * 解析日期字符串，如果解析失败返回 {@code null}
     * @param date 要解析的日期字符串
     * @return 日期对象
     */
    public final Date parseQuietly(String source) {
        try {
            return parse(source);
        } catch (Exception e) {
            return null;
        }
    }
}
