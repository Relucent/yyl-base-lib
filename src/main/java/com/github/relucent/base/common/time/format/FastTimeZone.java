package com.github.relucent.base.common.time.format;

import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 生成自定义时区的工具类
 */
class FastTimeZone {

    private static final Pattern GMT_PATTERN = Pattern.compile("^(?:(?i)GMT)?([+-])?(\\d\\d?)?(:?(\\d\\d?))?$");
    private static final TimeZone GREENWICH = new GmtTimeZone(false, 0, 0);

    /** 不实例化 */
    private FastTimeZone() {
    }

    /**
     * 获取GMT时区
     * @return 原始偏移量为零的时区
     */
    public static TimeZone getGmtTimeZone() {
        return GREENWICH;
    }

    /**
     * 获取具有GMT偏移量的时区。<br>
     * GMT偏移量必须是'Z'或'UTC'，或者匹配<em>(GMT)? hh?(:?mm?)?</em>，其中h和m是表示小时和分钟的数字。
     * @param pattern GMT偏移
     * @return 时区{@code TimeZone}，如果模式不匹配，则时区从GMT偏移或为null
     */
    public static TimeZone getGmtTimeZone(final String pattern) {
        if ("Z".equals(pattern) || "UTC".equals(pattern)) {
            return GREENWICH;
        }

        final Matcher m = GMT_PATTERN.matcher(pattern);
        if (m.matches()) {
            final int hours = parseInt(m.group(2));
            final int minutes = parseInt(m.group(4));
            if (hours == 0 && minutes == 0) {
                return GREENWICH;
            }
            return new GmtTimeZone(parseSign(m.group(1)), hours, minutes);
        }
        return null;
    }

    /**
     * 根据ID获取一个时区<br>
     * 首先查找GMT自定义ID，如果没找到再从奥尔森(OLSON)时区ID查找。<br>
     * GMT自定义id可以是'Z'或'UTC'，或者具有可选的GMT前缀，后跟符号、小时数字、可选的冒号（':'）和可选的分钟数字。<br>
     * 即：<em>[GMT] (+|-) Hours [[:] Minutes]</em>
     * @param id GMT自定义ID或者奥尔森(OLSON)时区ID
     * @return 时区{@code TimeZone}
     */
    public static TimeZone getTimeZone(final String id) {
        final TimeZone tz = getGmtTimeZone(id);
        if (tz != null) {
            return tz;
        }
        return TimeZone.getTimeZone(id);
    }

    private static int parseInt(final String group) {
        return group != null ? Integer.parseInt(group) : 0;
    }

    private static boolean parseSign(final String group) {
        return group != null && group.charAt(0) == '-';
    }
}
