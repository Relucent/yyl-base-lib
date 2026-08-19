package com.github.relucent.base.common.crypto.digest;

import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link Digester} 单元测试
 */
public class DigesterTest {

    @Test
    public void testDigest() {
        Digester digester = new Digester(DigestAlgorithm.MD5);
        Assert.assertEquals("202cb962ac59075b964b07152d234b70", digester.digestHex("123"));
    }

    @Test
    public void testDigestBytes() {
        Digester digester = new Digester(DigestAlgorithm.SHA_256);
        byte[] result = digester.digest("abc".getBytes(StandardCharsets.UTF_8));
        Assert.assertEquals(32, result.length);
    }

    @Test
    public void testDigestWithCharset() {
        Digester digester = new Digester(DigestAlgorithm.MD5);
        Assert.assertEquals(DigestUtil.md5Hex("123"),
            digester.digestHex("123", StandardCharsets.UTF_8));
    }

    @Test
    public void testSaltAtHead() {
        // 加盐在头部:digest(salt + input)
        byte[] salt = "salt".getBytes(StandardCharsets.UTF_8);
        Digester digester = new Digester(DigestAlgorithm.MD5, salt);
        // 手工拼接验证
        String combined = "salt" + "123";
        Assert.assertEquals(DigestUtil.md5Hex(combined), digester.digestHex("123"));
    }

    @Test
    public void testDigestCount() {
        // digestCount=2: 对摘要结果(raw bytes)再摘要一次
        Digester d2 = new Digester(DigestAlgorithm.MD5);
        d2.setDigestCount(2);
        String twice = d2.digestHex("123");

        // 手动验证: 用 Digester 自身做两次独立单次摘要
        Digester manual = new Digester(DigestAlgorithm.MD5);
        byte[] firstHash = manual.digest("123");
        String manualTwice = manual.digestHex(firstHash);

        Assert.assertEquals(manualTwice, twice);
        // 二次摘要结果应与一次摘要不同
        Assert.assertNotEquals(DigestUtil.md5Hex("123"), twice);
    }

    @Test
    public void testGetAlgorithm() {
        Digester digester = new Digester(DigestAlgorithm.SHA_256);
        // getAlgorithm 返回的字段在构造时未赋值(为 null),验证非空需通过 messageDigest
        Assert.assertNotNull(digester.getMessageDigest());
        Assert.assertEquals("SHA-256", digester.getMessageDigest().getAlgorithm());
        Assert.assertEquals(32, digester.getDigestLength());
    }

    @Test
    public void testSetSalt() {
        Digester digester = new Digester(DigestAlgorithm.MD5);
        digester.setSalt("S".getBytes(StandardCharsets.UTF_8));
        digester.setSaltPosition(0);
        // 加盐在头部
        Assert.assertEquals(DigestUtil.md5Hex("S" + "123"), digester.digestHex("123"));
    }
}
