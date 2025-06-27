package com.github.relucent.base.common.time;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import org.junit.Assert;
import org.junit.Test;

import com.github.relucent.base.common.constant.ZoneIdConstant;

public class OffsetDateTimeUtilTest {
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
            OffsetDateTime now = OffsetDateTime.now();
            String text = formatter.format(now);
            TemporalAccessor temporal = formatter.parse(text);
            OffsetDateTime expected = OffsetDateTimeUtil.ofEpochMilli(TemporalAccessorUtil.toEpochMilli(temporal));
            OffsetDateTime actual = OffsetDateTimeUtil.parse(text);
            Assert.assertEquals(expected.toInstant(), actual.toInstant());
        }
    }
}
