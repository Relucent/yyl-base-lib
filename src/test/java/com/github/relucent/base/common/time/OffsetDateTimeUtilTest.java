package com.github.relucent.base.common.time;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.github.relucent.base.common.constant.ZoneIdConstant;

public class OffsetDateTimeUtilTest {

    @After
    public void after() {
        // 默认时区是全局静态状态，测试后还原，避免污染其他测试
        ZoneUtil.resetDefaultZoneId();
    }

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
            // 参考值经过 toEpochMilli 后只有毫秒精度，因此比较时对齐到毫秒，
            // 避免因为纳秒部分（如 .123456789）导致断言随机失败
            long expectedMillis = TemporalAccessorUtil.toEpochMilli(temporal);
            OffsetDateTime actual = OffsetDateTimeUtil.parse(text);
            Assert.assertEquals("formatter=" + formatter + ", text=" + text, //
                    expectedMillis, actual.toInstant().toEpochMilli());
        }
    }
}
