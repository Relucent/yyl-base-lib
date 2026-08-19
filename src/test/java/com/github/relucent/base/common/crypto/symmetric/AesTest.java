package com.github.relucent.base.common.crypto.symmetric;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.github.relucent.base.common.crypto.ModeEnum;
import com.github.relucent.base.common.crypto.PaddingEnum;

/**
 * {@link Aes} 单元测试
 */
public class AesTest {

    private static final byte[] KEY_128 = new byte[16];
    private static final byte[] IV_16 = new byte[16];

    static {
        // 填充非零值，避免全零密钥的特殊行为
        for (int i = 0; i < 16; i++) {
            KEY_128[i] = (byte) (i + 1);
            IV_16[i] = (byte) (i + 0x10);
        }
    }

    @Test
    public void testEncryptDecryptRoundTrip() {
        Aes aes = Aes.create(KEY_128);
        byte[] plain = "Hello, AES!".getBytes(StandardCharsets.UTF_8);
        byte[] cipher = aes.encrypt(plain);
        byte[] decrypted = aes.decrypt(cipher);
        Assert.assertArrayEquals(plain, decrypted);
    }

    @Test
    public void testEncryptHexDecryptHex() {
        Aes aes = Aes.create(KEY_128);
        String plain = "hex round trip";
        String hex = aes.encryptHex(plain);
        String decrypted = aes.decryptHexString(hex);
        Assert.assertEquals(plain, decrypted);
    }

    @Test
    public void testEncryptBase64DecryptBase64() {
        Aes aes = Aes.create(KEY_128);
        String plain = "base64 round trip";
        String b64 = aes.encryptBase64(plain);
        String decrypted = aes.decryptBase64String(b64);
        Assert.assertEquals(plain, decrypted);
    }

    @Test
    public void testEncryptStringDecryptString() {
        Aes aes = Aes.create(KEY_128);
        String plain = "Hello World 你好世界";
        byte[] cipher = aes.encrypt(plain);
        String decrypted = aes.decryptString(cipher);
        Assert.assertEquals(plain, decrypted);
    }

    @Test
    public void testSameKeyProducesSameCipher() {
        Aes aes1 = Aes.create(KEY_128);
        Aes aes2 = Aes.create(KEY_128);
        byte[] plain = "same key".getBytes(StandardCharsets.UTF_8);
        Assert.assertArrayEquals(aes1.encrypt(plain), aes2.encrypt(plain));
    }

    @Test
    public void testDifferentKeysProduceDifferentCipher() {
        byte[] key1 = new byte[16];
        byte[] key2 = new byte[16];
        key2[0] = 1;
        Aes aes1 = Aes.create(key1);
        Aes aes2 = Aes.create(key2);
        byte[] plain = "different key".getBytes(StandardCharsets.UTF_8);
        Assert.assertFalse(Arrays.equals(aes1.encrypt(plain), aes2.encrypt(plain)));
    }

    @Test
    public void testCbcModeWithIv() {
        Aes aes = Aes.create(ModeEnum.CBC, PaddingEnum.PKCS5Padding, KEY_128, IV_16);
        byte[] plain = "CBC mode test".getBytes(StandardCharsets.UTF_8);
        byte[] cipher = aes.encrypt(plain);
        byte[] decrypted = aes.decrypt(cipher);
        Assert.assertArrayEquals(plain, decrypted);
    }

    @Test
    public void testCbcSameKeyIvProducesSameCipher() {
        Aes aes1 = Aes.create(ModeEnum.CBC, PaddingEnum.PKCS5Padding, KEY_128, IV_16);
        Aes aes2 = Aes.create(ModeEnum.CBC, PaddingEnum.PKCS5Padding, KEY_128, IV_16);
        byte[] plain = "cbc consistency".getBytes(StandardCharsets.UTF_8);
        Assert.assertArrayEquals(aes1.encrypt(plain), aes2.encrypt(plain));
    }

    @Test
    public void testGetAlgorithm() {
        Aes aes = Aes.create(KEY_128);
        Assert.assertEquals("AES", aes.getAlgorithm());
    }

    @Test
    public void testCbcAlgorithm() {
        Aes aes = Aes.create(ModeEnum.CBC, PaddingEnum.PKCS5Padding, KEY_128, IV_16);
        Assert.assertEquals("AES/CBC/PKCS5Padding", aes.getAlgorithm());
    }

    @Test
    public void testGetSecretKey() {
        Aes aes = Aes.create(KEY_128);
        Assert.assertNotNull(aes.getSecretKey());
        Assert.assertEquals("AES", aes.getSecretKey().getAlgorithm());
    }

    @Test
    public void testRandomKeyRoundTrip() {
        Aes aes = Aes.create();
        byte[] plain = "random key test".getBytes(StandardCharsets.UTF_8);
        byte[] cipher = aes.encrypt(plain);
        byte[] decrypted = aes.decrypt(cipher);
        Assert.assertArrayEquals(plain, decrypted);
    }
}
