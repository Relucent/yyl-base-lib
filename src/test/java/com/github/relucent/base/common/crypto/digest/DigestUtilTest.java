package com.github.relucent.base.common.crypto.digest;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.junit.Assert;
import org.junit.Test;

import com.github.relucent.base.common.codec.Hex;

/**
 * {@link DigestUtil} 单元测试
 */
public class DigestUtilTest {

    @Test
    public void testMd5() {
        // MD5("123") 标准值
        Assert.assertEquals("202cb962ac59075b964b07152d234b70", DigestUtil.md5Hex("123"));
        // 空字符串
        Assert.assertEquals("d41d8cd98f00b204e9800998ecf8427e", DigestUtil.md5Hex(""));
    }

    @Test
    public void testMd5Bytes() {
        byte[] result = DigestUtil.md5("123");
        Assert.assertEquals(16, result.length);
        Assert.assertArrayEquals(result, DigestUtil.digest(DigestAlgorithm.MD5, "123"));
    }

    @Test
    public void testSha256() {
        // SHA-256("abc") 标准测试向量
        Assert.assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
            DigestUtil.sha256Hex("abc"));
        // 长度 32 字节
        Assert.assertEquals(32, DigestUtil.sha256("abc").length);
    }

    @Test
    public void testDigestByAlgorithm() {
        byte[] data = "123".getBytes(StandardCharsets.UTF_8);
        Assert.assertArrayEquals(DigestUtil.md5(data), DigestUtil.digest(DigestAlgorithm.MD5, data));
        Assert.assertArrayEquals(DigestUtil.sha256(data), DigestUtil.digest(DigestAlgorithm.SHA_256, data));
    }

    @Test
    public void testDigestHexConsistency() {
        // digestHex 与 digest 结果一致
        byte[] data = "hello".getBytes(StandardCharsets.UTF_8);
        Assert.assertEquals(Hex.encodeHexString(DigestUtil.md5(data)), DigestUtil.md5Hex(data));
    }

    @Test
    public void testSm3() {
        // SM3 依赖 BC,摘要长度 32 字节
        byte[] result = DigestUtil.sm3("abc");
        Assert.assertEquals(32, result.length);
        // hex 长度 64
        Assert.assertEquals(64, DigestUtil.sm3Hex("abc").length());
    }

    @Test
    public void testGetDigest() {
        MessageDigest md5 = DigestUtil.getDigest(DigestAlgorithm.MD5);
        Assert.assertNotNull(md5);
        Assert.assertEquals("MD5", md5.getAlgorithm());
        MessageDigest sha256 = DigestUtil.getDigest(DigestAlgorithm.SHA_256);
        Assert.assertNotNull(sha256);
    }

    @Test
    public void testDigestInputStream() throws Exception {
        MessageDigest md5 = DigestUtil.getDigest(DigestAlgorithm.MD5);
        byte[] data = "123".getBytes(StandardCharsets.UTF_8);
        byte[] result = DigestUtil.digest(md5, new ByteArrayInputStream(data));
        Assert.assertArrayEquals(DigestUtil.md5("123"), result);
    }

    @Test
    public void testDigestByteBuffer() {
        MessageDigest md5 = DigestUtil.getDigest(DigestAlgorithm.MD5);
        byte[] data = "123".getBytes(StandardCharsets.UTF_8);
        java.nio.ByteBuffer buffer = java.nio.ByteBuffer.wrap(data);
        // 注意:getDigest 返回的 MessageDigest 是新实例,可重复使用
        byte[] result = DigestUtil.digest(md5, buffer);
        Assert.assertArrayEquals(DigestUtil.md5("123"), result);
    }
}
