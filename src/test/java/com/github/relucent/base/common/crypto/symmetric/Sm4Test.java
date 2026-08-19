package com.github.relucent.base.common.crypto.symmetric;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.github.relucent.base.common.crypto.ModeEnum;
import com.github.relucent.base.common.crypto.PaddingEnum;
import com.github.relucent.base.common.crypto.ProviderFactory;

/**
 * {@link Sm4} 单元测试
 * <p>
 * SM4 依赖 BouncyCastle，如果 BC 不可用则跳过。
 */
public class Sm4Test {

    private static final byte[] KEY_128 = new byte[16];
    private static final byte[] IV_16 = new byte[16];

    static {
        for (int i = 0; i < 16; i++) {
            KEY_128[i] = (byte) (i + 1);
            IV_16[i] = (byte) (i + 0x10);
        }
    }

    private static boolean bcAvailable() {
        return ProviderFactory.isUseBouncyCastle();
    }

    @Test
    public void testEncryptDecryptRoundTrip() {
        if (!bcAvailable()) return;
        Sm4 sm4 = Sm4.create(KEY_128);
        byte[] plain = "Hello, SM4!".getBytes(StandardCharsets.UTF_8);
        byte[] cipher = sm4.encrypt(plain);
        byte[] decrypted = sm4.decrypt(cipher);
        Assert.assertArrayEquals(plain, decrypted);
    }

    @Test
    public void testEncryptHexDecryptHex() {
        if (!bcAvailable()) return;
        Sm4 sm4 = Sm4.create(KEY_128);
        String plain = "sm4 hex test";
        String hex = sm4.encryptHex(plain);
        String decrypted = sm4.decryptHexString(hex);
        Assert.assertEquals(plain, decrypted);
    }

    @Test
    public void testEncryptBase64DecryptBase64() {
        if (!bcAvailable()) return;
        Sm4 sm4 = Sm4.create(KEY_128);
        String plain = "sm4 base64 test";
        String b64 = sm4.encryptBase64(plain);
        String decrypted = sm4.decryptBase64String(b64);
        Assert.assertEquals(plain, decrypted);
    }

    @Test
    public void testSameKeyProducesSameCipher() {
        if (!bcAvailable()) return;
        Sm4 sm4_1 = Sm4.create(KEY_128);
        Sm4 sm4_2 = Sm4.create(KEY_128);
        byte[] plain = "sm4 consistency".getBytes(StandardCharsets.UTF_8);
        Assert.assertArrayEquals(sm4_1.encrypt(plain), sm4_2.encrypt(plain));
    }

    @Test
    public void testDifferentKeysProduceDifferentCipher() {
        if (!bcAvailable()) return;
        byte[] key1 = new byte[16];
        byte[] key2 = new byte[16];
        key2[0] = 1;
        Sm4 sm4_1 = Sm4.create(key1);
        Sm4 sm4_2 = Sm4.create(key2);
        byte[] plain = "different".getBytes(StandardCharsets.UTF_8);
        Assert.assertFalse(Arrays.equals(sm4_1.encrypt(plain), sm4_2.encrypt(plain)));
    }

    @Test
    public void testCbcModeWithIv() {
        if (!bcAvailable()) return;
        Sm4 sm4 = Sm4.create(ModeEnum.CBC, PaddingEnum.PKCS5Padding,
                SecretKeyUtil.generateSecretKey("SM4", KEY_128), new javax.crypto.spec.IvParameterSpec(IV_16));
        byte[] plain = "CBC mode SM4".getBytes(StandardCharsets.UTF_8);
        byte[] cipher = sm4.encrypt(plain);
        byte[] decrypted = sm4.decrypt(cipher);
        Assert.assertArrayEquals(plain, decrypted);
    }

    @Test
    public void testGetAlgorithm() {
        if (!bcAvailable()) return;
        Sm4 sm4 = Sm4.create(KEY_128);
        Assert.assertEquals("SM4", sm4.getAlgorithm());
    }

    @Test
    public void testGetSecretKey() {
        if (!bcAvailable()) return;
        Sm4 sm4 = Sm4.create(KEY_128);
        Assert.assertNotNull(sm4.getSecretKey());
    }

    @Test
    public void testRandomKeyRoundTrip() {
        if (!bcAvailable()) return;
        Sm4 sm4 = Sm4.create();
        byte[] plain = "random key sm4".getBytes(StandardCharsets.UTF_8);
        byte[] cipher = sm4.encrypt(plain);
        byte[] decrypted = sm4.decrypt(cipher);
        Assert.assertArrayEquals(plain, decrypted);
    }
}
