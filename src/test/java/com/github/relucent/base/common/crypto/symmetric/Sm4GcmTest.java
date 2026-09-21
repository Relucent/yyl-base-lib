package com.github.relucent.base.common.crypto.symmetric;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.crypto.SecretKey;

import org.junit.Assert;
import org.junit.Test;

import com.github.relucent.base.common.crypto.ProviderFactory;
import com.github.relucent.base.common.crypto.symmetric.Sm4Gcm.Sm4GcmPayload;

/**
 * {@link Sm4Gcm} 单元测试<br>
 * SM4 依赖 BouncyCastle，如果 BC 不可用则跳过。
 */
public class Sm4GcmTest {

    private static final byte[] KEY_128 = new byte[16];

    static {
        for (int i = 0; i < 16; i++) {
            KEY_128[i] = (byte) (i + 1);
        }
    }

    private static boolean bcAvailable() {
        return ProviderFactory.isUseBouncyCastle();
    }

    private Sm4Gcm createSm4Gcm() {
        SecretKey key = SecretKeyUtil.getSm4SecretKey(KEY_128);
        return new Sm4Gcm(key);
    }

    @Test
    public void testEncryptDecryptRoundTrip() throws Exception {
        if (!bcAvailable()) {
            return;
        }
        Sm4Gcm sm4Gcm = createSm4Gcm();
        byte[] plain = "Hello, SM4-GCM!".getBytes(StandardCharsets.UTF_8);
        Sm4GcmPayload payload = sm4Gcm.encrypt(plain);
        byte[] decrypted = sm4Gcm.decrypt(payload);
        Assert.assertArrayEquals(plain, decrypted);
    }

    @Test
    public void testEncryptProducesDifferentIv() throws Exception {
        if (!bcAvailable()) {
            return;
        }
        Sm4Gcm sm4Gcm = createSm4Gcm();
        byte[] plain = "same input".getBytes(StandardCharsets.UTF_8);
        Sm4GcmPayload p1 = sm4Gcm.encrypt(plain);
        Sm4GcmPayload p2 = sm4Gcm.encrypt(plain);
        // 每次加密生成随机 IV，两次结果应不同
        Assert.assertFalse(Arrays.equals(p1.getIv(), p2.getIv()));
        Assert.assertFalse(Arrays.equals(p1.getCipherText(), p2.getCipherText()));
    }

    @Test
    public void testDifferentKeysCannotDecrypt() throws Exception {
        if (!bcAvailable()) {
            return;
        }
        Sm4Gcm sm4Gcm1 = createSm4Gcm();
        byte[] plain = "tamper test".getBytes(StandardCharsets.UTF_8);
        Sm4GcmPayload payload = sm4Gcm1.encrypt(plain);

        // 用不同密钥解密应失败（GCM 认证校验）
        byte[] wrongKey = new byte[16];
        wrongKey[0] = 99;
        Sm4Gcm sm4Gcm2 = new Sm4Gcm(SecretKeyUtil.getSm4SecretKey(wrongKey));
        try {
            sm4Gcm2.decrypt(payload);
            Assert.fail("Should throw exception with wrong key");
        } catch (Exception e) {
            // expected: AEADBadTagException or similar
        }
    }

    @Test
    public void testPayloadToBytesAndParse() throws Exception {
        if (!bcAvailable()) {
            return;
        }
        Sm4Gcm sm4Gcm = createSm4Gcm();
        byte[] plain = "payload test".getBytes(StandardCharsets.UTF_8);
        Sm4GcmPayload payload = sm4Gcm.encrypt(plain);

        byte[] combined = payload.toBytes();
        Sm4GcmPayload parsed = Sm4GcmPayload.parse(combined);

        Assert.assertArrayEquals(payload.getIv(), parsed.getIv());
        Assert.assertArrayEquals(payload.getCipherText(), parsed.getCipherText());

        byte[] decrypted = sm4Gcm.decrypt(parsed);
        Assert.assertArrayEquals(plain, decrypted);
    }

    @Test
    public void testPayloadParseWithCustomIvLength() throws Exception {
        if (!bcAvailable()) {
            return;
        }
        Sm4Gcm sm4Gcm = new Sm4Gcm(SecretKeyUtil.getSm4SecretKey(KEY_128), 16, 128);
        byte[] plain = "custom iv".getBytes(StandardCharsets.UTF_8);
        Sm4GcmPayload payload = sm4Gcm.encrypt(plain);

        Assert.assertEquals(16, payload.getIv().length);

        byte[] combined = payload.toBytes();
        Sm4GcmPayload parsed = Sm4GcmPayload.parse(combined, 16);
        byte[] decrypted = sm4Gcm.decrypt(parsed);
        Assert.assertArrayEquals(plain, decrypted);
    }

    @Test
    public void testStreamEncryptDecrypt() throws Exception {
        if (!bcAvailable()) {
            return;
        }
        Sm4Gcm sm4Gcm = createSm4Gcm();
        byte[] plain = "stream encryption test data".getBytes(StandardCharsets.UTF_8);

        ByteArrayOutputStream encOut = new ByteArrayOutputStream();
        sm4Gcm.encrypt(new ByteArrayInputStream(plain), encOut);
        byte[] encrypted = encOut.toByteArray();

        // 密文 = IV(12) + ciphertext + tag
        Assert.assertTrue(encrypted.length > 12);

        ByteArrayOutputStream decOut = new ByteArrayOutputStream();
        sm4Gcm.decrypt(new ByteArrayInputStream(encrypted), decOut);
        byte[] decrypted = decOut.toByteArray();

        Assert.assertArrayEquals(plain, decrypted);
    }

    @Test
    public void testPayloadParseTooShortThrows() {
        try {
            Sm4GcmPayload.parse(new byte[5]);
            Assert.fail("Should throw for too-short array");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testDecryptTamperedCipherThrows() throws Exception {
        if (!bcAvailable()) {
            return;
        }
        Sm4Gcm sm4Gcm = createSm4Gcm();
        byte[] plain = "integrity check".getBytes(StandardCharsets.UTF_8);
        Sm4GcmPayload payload = sm4Gcm.encrypt(plain);

        // 篡改密文
        byte[] tamperedCipher = payload.getCipherText().clone();
        tamperedCipher[0] ^= 0x01;
        Sm4GcmPayload tampered = new Sm4GcmPayload(payload.getIv(), tamperedCipher);

        try {
            sm4Gcm.decrypt(tampered);
            Assert.fail("Should throw for tampered ciphertext");
        } catch (Exception e) {
            // expected: GCM authentication tag mismatch
        }
    }

    @Test
    public void testConstants() {
        Assert.assertEquals("SM4/GCM/NoPadding", Sm4Gcm.TRANSFORMATION);
        Assert.assertEquals(12, Sm4Gcm.DEFAULT_GCM_IV_LENGTH);
        Assert.assertEquals(128, Sm4Gcm.DEFAULT_GCM_TAG_LENGTH);
    }
}
