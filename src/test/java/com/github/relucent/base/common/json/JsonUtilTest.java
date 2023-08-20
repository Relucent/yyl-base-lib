package com.github.relucent.base.common.json;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.relucent.base.common.reflect.TypeReference;

@SuppressWarnings("deprecation")
public class JsonUtilTest {

    private Sample[] samples;

    @Before
    public void testBefore() {
        samples = new Sample[3];
        samples[0] = new Sample();
        samples[0].number = 0L;
        samples[0].string = "hello";
        samples[0].date = new Date(0);

        samples[1] = new Sample();
        samples[1].number = Long.MAX_VALUE;
        samples[1].string = "world";
        samples[1].date = new Date((Long.MAX_VALUE / 1000) * 1000); // 时间只保留秒

        samples[2] = new Sample();
        samples[2].number = Long.MIN_VALUE;
        samples[2].string = "json";
        samples[2].date = new Date((System.currentTimeMillis() / 1000) * 1000); // 时间只保留秒
    }

    @Test
    public void testEncodeAndDecode() {
        String json = JsonUtil.encode(samples);
        Sample[] decodeds = JsonUtil.decode(json, Sample[].class);
        Assert.assertEquals(samples.length, decodeds.length);

        for (int i = 0; i < samples.length; i++) {
            Sample sample = samples[i];
            Sample decoded = decodeds[i];
            Assert.assertEquals(sample.number, decoded.number);
            Assert.assertEquals(sample.string, decoded.string);
            Assert.assertEquals(sample.date, decoded.date);
        }
    }

    @Test
    public void testDecodeTypeReference() {
        TypeReference<Sample[]> token = new TypeReference<Sample[]>() {
        };
        String json = JsonUtil.encode(samples);
        Sample[] decodeds = JsonUtil.decode(json, token);
        Assert.assertEquals(samples.length, decodeds.length);

        for (int i = 0; i < samples.length; i++) {
            Sample sample = samples[i];
            Sample decoded = decodeds[i];
            Assert.assertEquals(sample.number, decoded.number);
            Assert.assertEquals(sample.string, decoded.string);
            Assert.assertEquals(sample.date, decoded.date);
        }
    }

    private static class Sample {
        public Long number;
        public String string;
        public Date date;
    }
}
