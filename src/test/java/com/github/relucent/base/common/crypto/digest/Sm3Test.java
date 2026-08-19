package com.github.relucent.base.common.crypto.digest;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link Sm3} 单元测试(依赖 BouncyCastle)
 */
public class Sm3Test {

    @Test
    public void testDigest() {
        Sm3 sm3 = Sm3.create();
        byte[] result = sm3.digest("abc");
        Assert.assertEquals(32, result.length);
    }

    @Test
    public void testDigestHex() {
        Sm3 sm3 = Sm3.create();
        String hex = sm3.digestHex("abc");
        Assert.assertEquals(64, hex.length());
        // 与 DigestUtil.sm3 结果一致
        Assert.assertEquals(DigestUtil.sm3Hex("abc"), hex);
    }

    @Test
    public void testCreateWithSalt() {
        byte[] salt = { 0x01, 0x02 };
        Sm3 sm3 = Sm3.create(salt);
        Assert.assertNotNull(sm3);
        // 加盐后摘要长度仍为 32
        Assert.assertEquals(32, sm3.digest("abc").length);
    }

    @Test
    public void testConsistencyWithDigestUtil() {
        Sm3 sm3 = Sm3.create();
        Assert.assertArrayEquals(DigestUtil.sm3("hello"), sm3.digest("hello"));
    }

    @Test
    public void testGetDigestLength() {
        Sm3 sm3 = Sm3.create();
        Assert.assertEquals(32, sm3.getDigestLength());
    }
}
