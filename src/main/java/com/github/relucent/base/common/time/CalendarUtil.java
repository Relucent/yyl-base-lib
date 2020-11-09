package com.github.relucent.base.common.time;

import java.util.Calendar;
import java.util.Date;

/**
 * 日历工具类
 */
public class CalendarUtil {

    /**
     * 转换 {@link Date} 为 {@link Calendar} 类型
     * @param date {@link Date}
     * @return {link {@link Calendar}}
     */
    public static Calendar toCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * 转换 {@link Date} 为 {@link Calendar} 类型
     * @param millis 时间的毫秒值
     * @return {link {@link Calendar}}
     */
    public static Calendar toCalendar(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar;
    }

    /**
     * 得到某一时间指定周期的起始时间
     * @param calendar 指定的时间
     * @param unit 指定的单位类型
     * @return 指定时间指定周期的起始时间
     */
    public static Calendar getBegin(Calendar calendar, DateUnit unit) {
        Calendar begin = Calendar.getInstance();
        switch (unit) {
        case YEAR: {// 年
            int year = calendar.get(Calendar.YEAR);
            begin.set(year, Calendar.JANUARY, 1, 0, 0, 0);// _年1月0日0时0分0秒
            break;
        }
        case HALFYEAR: {// 半年
            int halfYear = getFieldValue(calendar, DateUnit.HALFYEAR);
            int year = calendar.get(Calendar.YEAR);
            int month = halfYear == 0 ? Calendar.JANUARY : Calendar.JULY;// [01月|07月]
            begin.set(year, month, 1, 0, 0, 0);// 年月日时分秒
            break;
        }
        case QUARTER: {// 季度
            int year = calendar.get(Calendar.YEAR);
            int quarter = getFieldValue(calendar, DateUnit.QUARTER); // 季度
            int month = quarter * 3;// 季度的开始月 JANUARY_01|APRIL_04|JULY_07|OCTOBER_10
            begin.set(year, month, 1, 0, 0, 0);// 年月日时分秒
            break;
        }
        case MONTH: {// 月份
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            begin.set(year, month, 1, 0, 0, 0);// 年月日时分秒
            break;
        }
        case DATE: {// 日期
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int date = calendar.get(Calendar.DATE);
            begin.set(year, month, date, 0, 0, 0);// 年月日时分秒
            break;
        }
        case HOUR_OF_DAY: {// 小时(0-24)
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int date = calendar.get(Calendar.DATE);
            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            begin.set(year, month, date, hourOfDay, 0, 0);// 年月日时分秒
            break;
        }
        case MINUTE: {// 小时(0-24)
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int date = calendar.get(Calendar.DATE);
            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            begin.set(year, month, date, hourOfDay, minute, 0);// 年月日时分秒
            break;
        }
        case SECOND: {// 小时(0-24)
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int date = calendar.get(Calendar.DATE);
            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);
            begin.set(year, month, date, hourOfDay, minute, second);// 年月日时分秒
            break;
        }
        default:
            // Ignore
        }
        begin.set(Calendar.MILLISECOND, 0);// 毫秒
        return begin;
    }

    /**
     * 得到某一时间指定周期的結束时间
     * @param calendar 指定的时间
     * @param unit 指定的单位类型
     * @return 指定时间指定周期的結束时间
     */
    public static Calendar getEnd(Calendar calendar, DateUnit unit) {
        Calendar end = Calendar.getInstance();
        switch (unit) {
        case YEAR: { // 年
            int year = calendar.get(Calendar.YEAR);
            end.set(year, Calendar.DECEMBER, 31, 23, 59, 59);// _年12月31日23时59分59秒
            break;
        }
        case HALFYEAR: {// 半年
            int halfYear = getFieldValue(calendar, DateUnit.HALFYEAR);
            int year = calendar.get(Calendar.YEAR);
            int month = halfYear == 0 ? Calendar.JUNE : Calendar.DECEMBER;// [06月|12月]
            end.set(Calendar.YEAR, year);// 年
            end.set(Calendar.MONTH, month);// 月
            end.set(Calendar.DATE, 1);
            int dayOfEndMonth = end.getActualMaximum(Calendar.DAY_OF_MONTH);// 该月天数
            end.set(Calendar.DATE, dayOfEndMonth);// 日
            end.set(Calendar.HOUR_OF_DAY, 23); // 时
            end.set(Calendar.MINUTE, 59);// 分
            end.set(Calendar.SECOND, 59);// 秒
            break;
        }
        case QUARTER: {// 季度
            int quarter = getFieldValue(calendar, DateUnit.QUARTER);// 季度
            int endMonth = quarter * 3 + 2;// 季度的结束月 MARCH_03|JUNE_06|SEPTEMBER_09|DECEMBER_12
            int year = calendar.get(Calendar.YEAR);
            end.set(Calendar.YEAR, year);
            end.set(Calendar.MONTH, endMonth);
            end.set(Calendar.DATE, 1);
            int dayOfEndMonth = end.getActualMaximum(Calendar.DAY_OF_MONTH);// 该月天数
            end.set(Calendar.DATE, dayOfEndMonth);// 日
            end.set(Calendar.HOUR_OF_DAY, 23); // 时
            end.set(Calendar.MINUTE, 59);// 分
            end.set(Calendar.SECOND, 59);// 秒
            break;
        }
        case MONTH: {// 月份
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            end.set(Calendar.YEAR, year);
            end.set(Calendar.MONTH, month);
            end.set(Calendar.DATE, 1);
            int dayOfEndMonth = end.getActualMaximum(Calendar.DAY_OF_MONTH);// 该月天数
            end.set(Calendar.DATE, dayOfEndMonth);// 日
            end.set(Calendar.HOUR_OF_DAY, 23); // 时
            end.set(Calendar.MINUTE, 59);// 分
            end.set(Calendar.SECOND, 59);// 秒
            break;
        }
        case DATE: {// 日期
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int date = calendar.get(Calendar.DATE);
            end.set(year, month, date, 23, 59, 59);// 年月日时分秒
            break;
        }
        case HOUR_OF_DAY: {// 小时(0-24)
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int date = calendar.get(Calendar.DATE);
            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            end.set(year, month, date, hourOfDay, 59, 59);// 年月日时分秒
            break;
        }
        case MINUTE: {// 分钟(0-24)
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int date = calendar.get(Calendar.DATE);
            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            end.set(year, month, date, hourOfDay, minute, 59);// 年月日时分秒
            break;
        }
        case SECOND: {// 秒钟(0-24)
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int date = calendar.get(Calendar.DATE);
            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);
            end.set(year, month, date, hourOfDay, minute, second);// 年月日时分秒
            break;
        }
        default:
            // Ignore
        }
        end.set(Calendar.MILLISECOND, 999);// 毫秒
        return end;
    }

    /**
     * 返回给定日历给定周期类型字段的值。
     * @param calendar 时间
     * @param unit 指定的单位类型
     * @return 所在季度(0-1)
     */
    public static int getFieldValue(Calendar calendar, DateUnit unit) {
        switch (unit) {
        case YEAR: // 年
            return calendar.get(Calendar.YEAR);
        case HALFYEAR:// 半年
            return calendar.get(Calendar.MONTH) < Calendar.JULY ? 0 : 1;// 小于[7月]
        case QUARTER:// 季度
            return calendar.get(Calendar.MONTH) / 3;
        case MONTH:// 月份
            return calendar.get(Calendar.MONTH);
        case DATE:// 日期
            return calendar.get(Calendar.DAY_OF_MONTH);
        default:
            return -1;
        }
    }
}
