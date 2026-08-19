package com.github.relucent.base.common.crypto.symmetric;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.github.relucent.base.common.crypto.ModeEnum;
import com.github.relucent.base.common.crypto.PaddingEnum;

/**
 * {@link Desede} 单元测试
 */
public class DesedeTest {

    private static final byte[] KEY_24 = new byte[24];
    private static final byte[] IV_8 = new byte[8];

    static {
        for (int i = 0; i < 24; i++) {
            KEY_24[i] = (byte) (i + 1);
        }
        for (int i = 0; i < 8; i++) {
            IV_8[i] = (byte) (i + 0x20);
        }
    }

    @Test
    public void testEncryptDecryptRoundTrip() {
        Desede desede = Desede.create(KEY_24);
        byte[] plain = "Hello, 3DES!".getBytes(StandardCharsets.UTF_8);
        byte[] cipher = desede.encrypt(plain);
        byte[] decrypted = desede.decrypt(cipher);
        Assert.assertArrayEquals(plain, decrypted);
    }

    @Test
    public void testEncryptHexDecryptHex() {
        Desede desede = Desede.create(KEY_24);
        String plain = "3des hex test";
        String hex = desede.encryptHex(plain);
        String decrypted = desede.decryptHexString(hex);
        Assert.assertEquals(plain, decrypted);
    }

    @Test
    public void testEncryptBase64DecryptBase64() {
        Desede desede = Desede.create(KEY_24);
        String plain = "3des base64 test";
        String b64 = desede.encryptBase64(plain);
        String decrypted = desede.decryptBase64String(b64);
        Assert.assertEquals(plain, decrypted);
    }

    @Test
    public void testEncryptStringDecryptString() {
        Desede desede = Desede.create(KEY_24);
        String plain = "3des string test 你好";
        byte[] cipher = desede.encrypt(plain);
        String decrypted = desede.decryptString(cipher);
        Assert.assertEquals(plain, decrypted);
    }

    @Test
    public void testSameKeyProducesSameCipher() {
        Desede d1 = Desede.create(KEY_24);
        Desede d2 = Desede.create(KEY_24);
        byte[] plain = "same key 3des".getBytes(StandardCharsets.UTF_8);
        Assert.assertArrayEquals(d1.encrypt(plain), d2.encrypt(plain));
    }

    @Test
    public void testDifferentKeysProduceDifferentCipher() {
        byte[] key1 = new byte[24];
        byte[] key2 = new byte[24];
        key2[0] = 2; // 非 LSB(奇偶校验位)，确保有效密钥不同
        Desede d1 = Desede.create(key1);
        Desede d2 = Desede.create(key2);
        byte[] plain = "different 3des".getBytes(StandardCharsets.UTF_8);
        Assert.assertFalse(Arrays.equals(d1.encrypt(plain), d2.encrypt(plain)));
    }

    @Test
    public void testCbcModeWithIv() {
        Desede desede = Desede.create(ModeEnum.CBC, PaddingEnum.PKCS5Padding, KEY_24, IV_8);
        byte[] plain = "CBC 3DES test".getBytes(StandardCharsets.UTF_8);
        byte[] cipher = desede.encrypt(plain);
        byte[] decrypted = desede.decrypt(cipher);
        Assert.assertArrayEquals(plain, decrypted);
    }

    @Test
    public void testGetAlgorithm() {
        Desede desede = Desede.create(KEY_24);
        Assert.assertEquals("DESede", desede.getAlgorithm());
    }

    @Test
    public void testCbcAlgorithm() {
        Desede desede = Desede.create(ModeEnum.CBC, PaddingEnum.PKCS5Padding, KEY_24, IV_8);
        Assert.assertEquals("DESede/CBC/PKCS5Padding", desede.getAlgorithm());
    }

    @Test
    public void testGetSecretKey() {
        Desede desede = Desede.create(KEY_24);
        Assert.assertNotNull(desede.getSecretKey());
    }

    @Test
    public void testRandomKeyRoundTrip() {
        Desede desede = Desede.create();
        byte[] plain = "random key 3des".getBytes(StandardCharsets.UTF_8);
        byte[] cipher = desede.encrypt(plain);
        byte[] decrypted = desede.decrypt(cipher);
        Assert.assertArrayEquals(plain, decrypted);
    }
}
