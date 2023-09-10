package com.github.relucent.base.common.time;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Assert;
import org.junit.Test;

import com.github.relucent.base.common.constant.ZoneIdConstant;

public class TemporalAccessorUtilTest {

    @Test
    public void testParse() {

        ZoneUtil.setDefaultZoneId(ZoneIdConstant.UTC);

        for (DateTimeFormatter formatter : new DateTimeFormatter[] { //
                DateTimeFormatter.BASIC_ISO_DATE, //
                DateTimeFormatter.ISO_DATE, //
                DateTimeFormatter.ISO_TIME, //
                DateTimeFormatter.ISO_DATE_TIME, //
                DateTimeFormatter.ISO_LOCAL_DATE, //
                DateTimeFormatter.ISO_LOCAL_TIME, //
                DateTimeFormatter.ISO_LOCAL_DATE_TIME, //
                DateTimeFormatter.ISO_OFFSET_DATE, //
                DateTimeFormatter.ISO_OFFSET_TIME, //
                DateTimeFormatter.ISO_OFFSET_DATE_TIME, //
                DateTimeFormatter.ISO_ORDINAL_DATE, //
                DateTimeFormatter.ISO_ZONED_DATE_TIME, //
                DateTimeFormatter.RFC_1123_DATE_TIME, //
                DateTimeFormatter.ISO_INSTANT, //
        }) {
            ZonedDateTime now = ZonedDateTime.now();
            String text = formatter.format(now);
            ZonedDateTime expected = TemporalAccessorUtil.toZonedDateTime(formatter.parse(text));
            ZonedDateTime actual = ZonedDateTimeUtil.parse(text);
            Assert.assertEquals(expected.toInstant(), actual.toInstant());
        }
    }
}
