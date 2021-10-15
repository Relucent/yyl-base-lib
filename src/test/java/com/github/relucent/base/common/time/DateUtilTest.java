package com.github.relucent.base.common.time;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DateUtilTest {

    private Map<String, Date> sample = new LinkedHashMap<>();

    @Before
    public void before() throws ParseException {
        Date now = DateUtil.now();
        for (String pattern : Arrays.asList( //
                "yyyy-MM-dd'T'HH:mm:ss.SSSZZ", //
                "yyyy-MM-dd'T'HH:mm:ss.SSS", //
                "yyyy-MM-dd'T'HH:mm:ss", //
                "yyyy-MM-dd HH:mm:ss", //
                "yyyy-MM-dd HH:mm:ss.SSS", //
                "yyyy-MM-dd HH:mm", //
                "yyyy-MM-dd HH", //
                "yyyy-MM-dd", //
                "yyyy-MM", //
                "EEE MMM dd HH:mm:ss zzz yyyy", //
                "MMM d, yyyy HH:mm", //
                "d MMM yyyy h:m a", //
                "MMM d, yyyy", //
                "MM/dd/yyyy", //
                "yyyyMMdd", //
                "yyyyMM", //
                "yyyy")) {
            DateFormat formater = new SimpleDateFormat(pattern);
            String source = formater.format(now);
            Date date = formater.parse(source);
            sample.put(source, date);
        }
    }

    @Test
    public void testParse() {
        for (Map.Entry<String, Date> entry : sample.entrySet()) {
            Assert.assertEquals(DateUtil.parseDate(entry.getKey()), entry.getValue());
        }
    }
}
