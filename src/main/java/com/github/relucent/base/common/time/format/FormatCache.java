package com.github.relucent.base.common.time.format;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.github.relucent.base.common.lang.AssertUtil;
import com.github.relucent.base.common.lang.LocaleUtil;

/**
 * {@link Format}缓存类<br>
 */
abstract class FormatCache<F extends Format> {

    /** 表示没有日期或时间 */
    static final int NONE = -1;

    /** 日期格式缓存 */
    private static final ConcurrentMap<ArrayKey, String> DATE_TIME_INSTANCE_CACHE = new ConcurrentHashMap<>(7);

    /** 日期时间格式化器实例缓存 */
    private final ConcurrentMap<ArrayKey, F> instanceCache = new ConcurrentHashMap<>(7);

    /**
     * 在默认时区和地区中使用默认模式获取格式化器实例
     * @return 日期/时间格式化器
     */
    public F getInstance() {
        return getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, TimeZone.getDefault(), Locale.getDefault());
    }

    /** 
     * 使用指定的模式、时区和地区获取格式化器实例
     * @param pattern 日期格式（与{@link java.text.SimpleDateFormat}兼容），不能为{@code null}
     * @param timeZone 时区，null表示使用默认时区
     * @param locale 地区, null 使用默认地区
     * @return 日期/时间格式化器
     * @throws NullPointerException 如果 {@code pattern}为{@code null}
     * @throws IllegalArgumentException 如果 {@code pattern}无效
     */
    public F getInstance(final String pattern, TimeZone timeZone, Locale locale) {
        AssertUtil.notNull(pattern, "pattern");
        if (timeZone == null) {
            timeZone = TimeZone.getDefault();
        }
        locale = LocaleUtil.defaultLocale(locale);
        final ArrayKey key = new ArrayKey(pattern, timeZone, locale);
        F format = instanceCache.get(key);
        if (format == null) {
            format = createInstance(pattern, timeZone, locale);
            final F previousValue = instanceCache.putIfAbsent(key, format);
            // 极端并发情况，另一个线程同时做了同样的工作，应返回ConcurrentMap中的实例
            if (previousValue != null) {
                format = previousValue;
            }
        }
        return format;
    }

    /**
     * 使用指定的模式、时区和地区创建一个格式实例
     * @param pattern 日期格式（与{@link java.text.SimpleDateFormat}兼容），不能为{@code null}
     * @param timeZone 时区，null表示使用默认时区
     * @param locale 地区, null 使用默认地区
     * @return 日期/时间格式化器
     * @throws IllegalArgumentException 如果 {@code pattern}无效或者为{@code null}
     */
    protected abstract F createInstance(String pattern, TimeZone timeZone, Locale locale);

    /**
     * 用指定的样式、时区和地区获取日期/时间格式化器实例。
     * @param dateStyle 日期样式: FULL、LONG、MEDIUM、SHORT，{@code null} 表示格式中没有日期
     * @param timeStyle 时间样式: FULL、LONG、MEDIUM、SHORT，{@code null} 表示格式中没有时间
     * @param timeZone 可选时区，{@code null}表示使用默认区域设置
     * @param locale 可选地区
     * @return 日期/时间格式化器
     * @throws IllegalArgumentException 如果地区没有定义日期/时间模式
     */
    private F getDateTimeInstance(final Integer dateStyle, final Integer timeStyle, final TimeZone timeZone, Locale locale) {
        locale = LocaleUtil.defaultLocale(locale);
        final String pattern = getPatternForStyle(dateStyle, timeStyle, locale);
        return getInstance(pattern, timeZone, locale);
    }

    /**
     * 使用指定的样式、时区和地区获取日期/时间格式化器实例。
     * @param dateStyle 日期样式: FULL、LONG、MEDIUM、SHORT
     * @param timeStyle 时间样式: FULL、LONG、MEDIUM、SHORT
     * @param timeZone 可选时区，{@code null}表示使用默认区域设置
     * @param locale 可选地区
     * @return 本地化的标准日期/时间格式化器器
     * @throws IllegalArgumentException 如果地区没有定义日期/时间模式
     */
    F getDateTimeInstance(final int dateStyle, final int timeStyle, final TimeZone timeZone, final Locale locale) {
        return getDateTimeInstance(Integer.valueOf(dateStyle), Integer.valueOf(timeStyle), timeZone, locale);
    }

    /**
     * 使用指定的样式、时区和地区获取时间格式化器实例。
     * @param dateStyle 日期样式 : FULL、LONG、MEDIUM、SHORT
     * @param timeZone 可选时区，{@code null}表示使用默认区域设置
     * @param locale 可选地区
     * @return 日期/时间格式化器
     * @throws IllegalArgumentException 如果地区没有定义日期/时间模式
     */
    F getDateInstance(final int dateStyle, final TimeZone timeZone, final Locale locale) {
        return getDateTimeInstance(Integer.valueOf(dateStyle), null, timeZone, locale);
    }

    /**
     * 使用指定的样式、时区和地区获取时间格式化器实例。
     * @param timeStyle 时间样式: FULL、LONG、MEDIUM、SHORT
     * @param timeZone 可选时区，{@code null}表示使用默认区域设置
     * @param locale 可选地区
     * @return 日期/时间格式化器
     * @throws IllegalArgumentException 如果地区没有定义日期/时间模式
     */
    F getTimeInstance(final int timeStyle, final TimeZone timeZone, final Locale locale) {
        return getDateTimeInstance(null, Integer.valueOf(timeStyle), timeZone, locale);
    }

    /**
     * 获取指定样式和地区的日期/时间格式。
     * @param dateStyle 日期样式: FULL、LONG、MEDIUM、SHORT，{@code null} 表示格式中没有日期
     * @param timeStyle 时间样式: FULL、LONG、MEDIUM、SHORT，{@code null} 表示格式中没有时间
     * @param locale 格式的地区，不能为{@code null}
     * @return 本地化的标准日期/时间格式
     * @throws IllegalArgumentException 如果地区没有定义日期/时间模式
     */
    static String getPatternForStyle(final Integer dateStyle, final Integer timeStyle, final Locale locale) {
        final Locale safeLocale = LocaleUtil.defaultLocale(locale);
        final ArrayKey key = new ArrayKey(dateStyle, timeStyle, safeLocale);

        String pattern = DATE_TIME_INSTANCE_CACHE.get(key);
        if (pattern == null) {
            try {
                final DateFormat formatter;
                if (dateStyle == null) {
                    formatter = DateFormat.getTimeInstance(timeStyle.intValue(), safeLocale);
                } else if (timeStyle == null) {
                    formatter = DateFormat.getDateInstance(dateStyle.intValue(), safeLocale);
                } else {
                    formatter = DateFormat.getDateTimeInstance(dateStyle.intValue(), timeStyle.intValue(), safeLocale);
                }
                pattern = ((SimpleDateFormat) formatter).toPattern();
                final String previous = DATE_TIME_INSTANCE_CACHE.putIfAbsent(key, pattern);
                if (previous != null) {
                    pattern = previous;
                }
            } catch (final ClassCastException ex) {
                throw new IllegalArgumentException("No date time pattern for locale: " + safeLocale);
            }
        }
        return pattern;
    }

    /**
     * 由数组组成的键
     */
    private static final class ArrayKey {

        private static int computeHashCode(final Object[] keys) {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.hashCode(keys);
            return result;
        }

        private final Object[] keys;
        private final int hashCode;

        /**
         * 构造实例。
         * @param keys 组成键的一组对象，每个键都可以为null。
         */
        ArrayKey(final Object... keys) {
            this.keys = keys;
            this.hashCode = computeHashCode(keys);
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ArrayKey other = (ArrayKey) obj;
            return Arrays.deepEquals(keys, other.keys);
        }
    }
}
