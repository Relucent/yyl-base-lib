package com.github.relucent.base.common.time;

import java.text.ParseException;
import java.util.Calendar;

import org.junit.Test;

import com.github.relucent.base.common.time.CalendarUtil;
import com.github.relucent.base.common.time.DateUnit;

public class CalendarUtilTest {
    @Test
    public void testGetBegin() throws ParseException {
        Calendar calendar = Calendar.getInstance();
        CalendarUtil.getBegin(calendar, DateUnit.YEAR);
        CalendarUtil.getBegin(calendar, DateUnit.HALFYEAR);
        CalendarUtil.getBegin(calendar, DateUnit.QUARTER);
        CalendarUtil.getBegin(calendar, DateUnit.MONTH);
        CalendarUtil.getBegin(calendar, DateUnit.DATE);
        CalendarUtil.getBegin(calendar, DateUnit.HOUR_OF_DAY);
        CalendarUtil.getBegin(calendar, DateUnit.MINUTE);
        CalendarUtil.getBegin(calendar, DateUnit.SECOND);
    }

    @Test
    public void testGetEnd() throws ParseException {
        Calendar calendar = Calendar.getInstance();
        CalendarUtil.getEnd(calendar, DateUnit.YEAR);
        CalendarUtil.getEnd(calendar, DateUnit.HALFYEAR);
        CalendarUtil.getEnd(calendar, DateUnit.QUARTER);
        CalendarUtil.getEnd(calendar, DateUnit.MONTH);
        CalendarUtil.getEnd(calendar, DateUnit.DATE);
        CalendarUtil.getEnd(calendar, DateUnit.HOUR_OF_DAY);
        CalendarUtil.getEnd(calendar, DateUnit.MINUTE);
        CalendarUtil.getEnd(calendar, DateUnit.SECOND);
    }
}
