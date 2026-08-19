package com.github.relucent.base.common.codec;

import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link Base64} 单元测试
 */
public class Base64Test {

    @Test
    public void testEncodeAndDecode() {
        byte[] data = "hello world".getBytes(StandardCharsets.UTF_8);
        String encoded = Base64.encode(data);
        Assert.assertEquals("aGVsbG8gd29ybGQ=", encoded);
        Assert.assertArrayEquals(data, Base64.decode(encoded));
    }

    @Test
    public void testEncodeUrl() {
        // URL 安全编码使用 - 和 _ 替代 + 和 /
        byte[] data = { (byte) 0xFB, (byte) 0xFF };
        String encoded = Base64.encodeUrl(data);
        Assert.assertNotNull(encoded);
        Assert.assertArrayEquals(data, Base64.decodeUrl(encoded));
    }

    @Test
    public void testEncodeMime() {
        byte[] data = "hello world".getBytes(StandardCharsets.UTF_8);
        String encoded = Base64.encodeMime(data);
        Assert.assertNotNull(encoded);
        Assert.assertArrayEquals(data, Base64.decodeMime(encoded));
    }

    @Test
    public void testEncodeNull() {
        Assert.assertEquals("", Base64.encode(null));
        Assert.assertEquals("", Base64.encode(new byte[0]));
        Assert.assertEquals("", Base64.encodeUrl(null));
        Assert.assertEquals("", Base64.encodeMime(null));
    }

    @Test
    public void testDecodeNull() {
        Assert.assertEquals(0, Base64.decode(null).length);
        Assert.assertEquals(0, Base64.decode("").length);
        Assert.assertEquals(0, Base64.decodeUrl(null).length);
        Assert.assertEquals(0, Base64.decodeMime(null).length);
    }

    @Test
    public void testDecodeInvalid() {
        // 非法 Base64 返回空数组(不抛异常)
        Assert.assertEquals(0, Base64.decode("@#$%").length);
        Assert.assertEquals(0, Base64.decodeUrl("@#$%").length);
    }

    @Test
    public void testRoundtripEmpty() {
        Assert.assertEquals("", Base64.encode(new byte[0]));
    }

    @Test
    public void testRoundtripChinese() {
        byte[] data = "你好,世界".getBytes(StandardCharsets.UTF_8);
        String encoded = Base64.encode(data);
        Assert.assertArrayEquals(data, Base64.decode(encoded));
    }
}
