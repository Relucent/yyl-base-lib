package com.github.relucent.base.common.time;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class LocalDateTimeUtilTest {

    @Test
    public void testParse() {
        for (String pattern : Arrays.asList( //
                "yyyy-MM-dd HH:mm:ss", //
                "yyyy-MM-dd'T'HH:mm:ss.SSS", //
                "yyyy-MM-dd HH:mm:ss.SSS", //
                "yyyy-MM-dd HH:mm", //
                "yyyy-MM-dd HH", //
                "yyyy-MM-dd", //
                "yyyy-MM", //
                "d MMM yyyy h:m a", //
                "MMM d, yyyy HH:mm", //
                "MMM d, yyyy", //
                "MM/dd/yyyy", //
                "yyyyMMdd", //
                "yyyyMM", //
                "yyyy")) {
            LocalDateTime datetime = LocalDateTime.now();
            String source = LocalDateTimeUtil.format(datetime, pattern);
            LocalDateTime parsed = LocalDateTimeUtil.parse(source, pattern);
            String actual = LocalDateTimeUtil.format(parsed, pattern);
            String expected = LocalDateTimeUtil.format(LocalDateTimeUtil.parse(actual, pattern), pattern);
            Assert.assertEquals(expected, actual);
        }
    }

}
