package com.github.relucent.base.common.crypto.digest;

import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link Md5} 单元测试
 */
public class Md5Test {

    @Test
    public void testDigestHex() {
        Md5 md5 = Md5.create();
        Assert.assertEquals(DigestUtil.md5Hex("123"), md5.digestHex("123"));
    }

    @Test
    public void testDigestBytes() {
        Md5 md5 = Md5.create();
        byte[] result = md5.digest("123");
        Assert.assertEquals(16, result.length);
    }

    @Test
    public void testDigestWithSalt() {
        byte[] salt = "salt".getBytes(StandardCharsets.UTF_8);
        Md5 md5 = Md5.create(salt);
        // 加盐在头部: md5(salt + input)
        String expected = DigestUtil.md5Hex("salt" + "123");
        Assert.assertEquals(expected, md5.digestHex("123"));
    }

    @Test
    public void testDigestCount() {
        Md5 md5 = Md5.create(null, 0, 2);
        // 二次摘要
        Digester d2 = new Digester(DigestAlgorithm.MD5);
        d2.setDigestCount(2);
        Assert.assertEquals(d2.digestHex("123"), md5.digestHex("123"));
    }

    @Test
    public void testGetAlgorithm() {
        Md5 md5 = Md5.create();
        Assert.assertNotNull(md5.getMessageDigest());
        Assert.assertEquals("MD5", md5.getMessageDigest().getAlgorithm());
    }
}
