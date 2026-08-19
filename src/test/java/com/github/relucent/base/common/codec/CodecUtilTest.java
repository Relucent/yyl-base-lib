package com.github.relucent.base.common.codec;

import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link CodecUtil} 单元测试
 */
public class CodecUtilTest {

    @Test
    public void testEncodeHexAndDecodeHex() {
        byte[] data = "hello".getBytes(StandardCharsets.UTF_8);
        String hex = CodecUtil.encodeHexString(data);
        Assert.assertArrayEquals(data, CodecUtil.decodeHex(hex));
    }

    @Test
    public void testEncodeBase64AndDecodeBase64() {
        byte[] data = "hello world".getBytes(StandardCharsets.UTF_8);
        String encoded = CodecUtil.encodeBase64(data);
        Assert.assertEquals("aGVsbG8gd29ybGQ=", encoded);
        Assert.assertArrayEquals(data, CodecUtil.decodeBase64(encoded));
    }

    @Test
    public void testEncodeUrlBase64AndDecodeUrlBase64() {
        byte[] data = { (byte) 0xFB, (byte) 0xFF };
        String encoded = CodecUtil.encodeUrlBase64(data);
        Assert.assertArrayEquals(data, CodecUtil.decodeUrlBase64(encoded));
    }

    @Test
    public void testEncodeMimeBase64AndDecodeMimeBase64() {
        byte[] data = "hello world".getBytes(StandardCharsets.UTF_8);
        String encoded = CodecUtil.encodeMimeBase64(data);
        Assert.assertArrayEquals(data, CodecUtil.decodeMimeBase64(encoded));
    }

    @Test
    public void testEncodeUriAndDecodeUri() {
        String input = "hello world & 你好";
        String encoded = CodecUtil.encodeUri(input);
        Assert.assertEquals(input, CodecUtil.decodeUri(encoded));
    }

    @Test
    public void testEncodeUriRfc3986() {
        // 空格应编码为 %20 而非 +
        String encoded = CodecUtil.encodeUriRfc3986("a b");
        Assert.assertEquals("a%20b", encoded);
        // ~ 不被编码
        Assert.assertEquals("~", CodecUtil.encodeUriRfc3986("~"));
    }

    @Test
    public void testEncodeUriRfc3986Roundtrip() {
        String input = "hello world ~ 你好";
        String encoded = CodecUtil.encodeUriRfc3986(input);
        Assert.assertEquals(input, CodecUtil.decodeUri(encoded));
    }

    @Test
    public void testDecodeByteArrayHex() {
        // Hex 字符串走 Hex 解码
        byte[] data = { 0x01, 0x23, (byte) 0xAB };
        String hex = CodecUtil.encodeHexString(data);
        Assert.assertArrayEquals(data, CodecUtil.decodeByteArray(hex));
    }

    @Test
    public void testDecodeByteArrayBase64() {
        // 非 Hex 字符串走 Base64 解码
        byte[] data = "hello".getBytes(StandardCharsets.UTF_8);
        String base64 = CodecUtil.encodeBase64(data);
        Assert.assertArrayEquals(data, CodecUtil.decodeByteArray(base64));
    }

    @Test
    public void testEncodeUriWithEncoding() throws Exception {
        String input = "你好";
        String encoded = CodecUtil.encodeUri(input, "UTF-8");
        Assert.assertEquals(input, CodecUtil.decodeUri(encoded, "UTF-8"));
    }
}
