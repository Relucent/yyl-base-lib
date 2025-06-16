package com.github.relucent.base.common.crypto.mac;

import org.junit.Assert;
import org.junit.Test;

public class MacUtilTest {
    @Test
    public void testHmacSHA256() {
        String sample = "hello";
        String key = "1234567890abcdefghijklmnopqrstuv";
        String actual = MacUtil.hmacBase64(sample, key, HmacAlgorithm.HmacSHA256);
        Assert.assertEquals("hGWaQYz4fDF2zmo2eAmv5BSjKhQJFtZOummS0Rr3kA4=", actual);
    }
}
