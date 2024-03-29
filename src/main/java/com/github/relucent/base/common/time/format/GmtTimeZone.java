package com.github.relucent.base.common.time.format;

import java.util.Date;
import java.util.TimeZone;

/**
 * 包含GMT偏移量的自定义时区
 */
class GmtTimeZone extends TimeZone {

    /** 序列化支持 */
    private static final long serialVersionUID = 1L;

    /** GMT_ID */
    public static final String GMT_ID = "GMT";

    private static final int MILLISECONDS_PER_MINUTE = 60 * 1000;
    private static final int MINUTES_PER_HOUR = 60;
    private static final int HOURS_PER_DAY = 24;

    private final int offset;
    private final String zoneId;

    GmtTimeZone(final boolean negate, final int hours, final int minutes) {
        if (hours >= HOURS_PER_DAY) {
            throw new IllegalArgumentException(hours + " hours out of range");
        }
        if (minutes >= MINUTES_PER_HOUR) {
            throw new IllegalArgumentException(minutes + " minutes out of range");
        }
        final int milliseconds = (minutes + (hours * MINUTES_PER_HOUR)) * MILLISECONDS_PER_MINUTE;
        offset = negate ? -milliseconds : milliseconds;
        zoneId = twoDigits(twoDigits(new StringBuilder(9).append("GMT").append(negate ? '-' : '+'), hours).append(':'), minutes).toString();

    }

    private static StringBuilder twoDigits(final StringBuilder sb, final int n) {
        return sb.append((char) ('0' + (n / 10))).append((char) ('0' + (n % 10)));
    }

    @Override
    public int getOffset(final int era, final int year, final int month, final int day, final int dayOfWeek, final int milliseconds) {
        return offset;
    }

    @Override
    public void setRawOffset(final int offsetMillis) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getRawOffset() {
        return offset;
    }

    @Override
    public String getID() {
        return zoneId;
    }

    @Override
    public boolean useDaylightTime() {
        return false;
    }

    @Override
    public boolean inDaylightTime(final Date date) {
        return false;
    }

    @Override
    public String toString() {
        return "[GmtTimeZone id=\"" + zoneId + "\",offset=" + offset + ']';
    }

    @Override
    public int hashCode() {
        return offset;
    }

    @Override
    public boolean equals(final Object other) {
        return (other instanceof GmtTimeZone) && (zoneId == ((GmtTimeZone) other).zoneId);
    }
}
