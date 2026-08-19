package com.github.relucent.base.common.crypto.mac;

import java.nio.charset.StandardCharsets;
import java.security.Provider;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.github.relucent.base.common.crypto.ProviderFactory;

/**
 * {@link MacUtil} 单元测试
 */
public class MacUtilTest {

    private static final String DATA = "test data";
    private static final String KEY = "secret key";

    @Test
    public void testHmacSha256() {
        byte[] mac1 = MacUtil.hmac(DATA, KEY, HmacAlgorithm.HmacSHA256);
        byte[] mac2 = MacUtil.hmac(DATA, KEY, HmacAlgorithm.HmacSHA256);
        Assert.assertArrayEquals(mac1, mac2);
        Assert.assertEquals(32, mac1.length); // SHA-256 = 32 bytes
    }

    @Test
    public void testHmacSha512() {
        byte[] mac = MacUtil.hmac(DATA, KEY, HmacAlgorithm.HmacSHA512);
        Assert.assertEquals(64, mac.length); // SHA-512 = 64 bytes
    }

    @Test
    public void testHmacSha1() {
        byte[] mac = MacUtil.hmac(DATA, KEY, HmacAlgorithm.HmacSHA1);
        Assert.assertEquals(20, mac.length); // SHA-1 = 20 bytes
    }

    @Test
    public void testHmacHex() {
        String hex = MacUtil.hmacHex(DATA, KEY, HmacAlgorithm.HmacSHA256);
        Assert.assertNotNull(hex);
        Assert.assertEquals(64, hex.length()); // 32 bytes = 64 hex chars
    }

    @Test
    public void testHmacBase64() {
        String b64 = MacUtil.hmacBase64(DATA, KEY, HmacAlgorithm.HmacSHA256);
        Assert.assertNotNull(b64);
        Assert.assertTrue(b64.length() > 0);
    }

    @Test
    public void testDifferentKeysProduceDifferentMac() {
        byte[] mac1 = MacUtil.hmac(DATA, "key1", HmacAlgorithm.HmacSHA256);
        byte[] mac2 = MacUtil.hmac(DATA, "key2", HmacAlgorithm.HmacSHA256);
        Assert.assertFalse(Arrays.equals(mac1, mac2));
    }

    @Test
    public void testDifferentDataProduceDifferentMac() {
        byte[] mac1 = MacUtil.hmac("data1", KEY, HmacAlgorithm.HmacSHA256);
        byte[] mac2 = MacUtil.hmac("data2", KEY, HmacAlgorithm.HmacSHA256);
        Assert.assertFalse(Arrays.equals(mac1, mac2));
    }

    @Test
    public void testDifferentAlgorithmsProduceDifferentMac() {
        byte[] mac256 = MacUtil.hmac(DATA, KEY, HmacAlgorithm.HmacSHA256);
        byte[] mac512 = MacUtil.hmac(DATA, KEY, HmacAlgorithm.HmacSHA512);
        Assert.assertFalse(Arrays.equals(mac256, mac512));
    }

    @Test
    public void testHmacSM3() {
        // SM3 HMAC 依赖 BouncyCastle
        byte[] mac = MacUtil.hmac(DATA, KEY, HmacAlgorithm.HmacSM3);
        Assert.assertNotNull(mac);
        Assert.assertEquals(32, mac.length); // SM3 = 32 bytes
    }

    @Test
    public void testHmacHexConsistency() {
        // hex 结果应与手算一致（相同输入重复调用）
        String hex1 = MacUtil.hmacHex(DATA, KEY, HmacAlgorithm.HmacSHA256);
        String hex2 = MacUtil.hmacHex(DATA, KEY, HmacAlgorithm.HmacSHA256);
        Assert.assertEquals(hex1, hex2);
    }

    @Test
    public void testHmacByteArrayOverload() {
        byte[] dataBytes = DATA.getBytes(StandardCharsets.UTF_8);
        byte[] keyBytes = KEY.getBytes(StandardCharsets.UTF_8);
        byte[] mac = MacUtil.hmac(dataBytes, keyBytes, HmacAlgorithm.HmacSHA256);
        Assert.assertEquals(32, mac.length);
    }

    @Test
    public void testHmacWithNullProvider() throws Exception {
        // 修复前: Provider 为 null 时 Mac.getInstance(algo, null) 抛 NPE
        // 通过反射将 ProviderFactory 的 Provider 临时置空,验证退化为 JDK 默认 Provider
        java.lang.reflect.Method setProvider = ProviderFactory.class.getDeclaredMethod("setProvider", Provider.class);
        setProvider.setAccessible(true);
        Provider original = ProviderFactory.getProvider();
        try {
            setProvider.invoke(null, new Object[] { null });
            Assert.assertNull(ProviderFactory.getProvider());
            byte[] mac = MacUtil.hmac(DATA, KEY, HmacAlgorithm.HmacSHA256);
            Assert.assertEquals(32, mac.length);
        } finally {
            setProvider.invoke(null, new Object[] { original });
        }
        // 恢复后 Provider 非空
        Assert.assertEquals(original, ProviderFactory.getProvider());
    }
}
