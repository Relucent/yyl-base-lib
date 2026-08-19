package com.github.relucent.base.common.crypto.symmetric;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.crypto.spec.SecretKeySpec;

import org.junit.Assert;
import org.junit.Test;

import com.github.relucent.base.common.crypto.symmetric.AesGcm.AesGcmPayload;

/**
 * {@link AesGcm} 单元测试
 */
public class AesGcmTest {

    private static final byte[] KEY_128 = new byte[16];

    static {
        for (int i = 0; i < 16; i++) {
            KEY_128[i] = (byte) (i + 1);
        }
    }

    private AesGcm createAesGcm() {
        return new AesGcm(new SecretKeySpec(KEY_128, "AES"));
    }

    @Test
    public void testEncryptDecryptRoundTrip() throws Exception {
        AesGcm aesGcm = createAesGcm();
        byte[] plain = "Hello, AES-GCM!".getBytes(StandardCharsets.UTF_8);
        AesGcmPayload payload = aesGcm.encrypt(plain);
        byte[] decrypted = aesGcm.decrypt(payload);
        Assert.assertArrayEquals(plain, decrypted);
    }

    @Test
    public void testEncryptProducesDifferentIv() throws Exception {
        AesGcm aesGcm = createAesGcm();
        byte[] plain = "same input".getBytes(StandardCharsets.UTF_8);
        AesGcmPayload p1 = aesGcm.encrypt(plain);
        AesGcmPayload p2 = aesGcm.encrypt(plain);
        // 每次加密生成随机 IV，两次结果应不同
        Assert.assertFalse(Arrays.equals(p1.getIv(), p2.getIv()));
        Assert.assertFalse(Arrays.equals(p1.getCipherText(), p2.getCipherText()));
    }

    @Test
    public void testDifferentKeysCannotDecrypt() throws Exception {
        AesGcm aesGcm1 = createAesGcm();
        byte[] plain = "tamper test".getBytes(StandardCharsets.UTF_8);
        AesGcmPayload payload = aesGcm1.encrypt(plain);

        // 用不同密钥解密应失败（GCM 认证校验）
        byte[] wrongKey = new byte[16];
        wrongKey[0] = 99;
        AesGcm aesGcm2 = new AesGcm(new SecretKeySpec(wrongKey, "AES"));
        try {
            aesGcm2.decrypt(payload);
            Assert.fail("Should throw exception with wrong key");
        } catch (Exception e) {
            // expected: AEADBadTagException or similar
        }
    }

    @Test
    public void testPayloadToBytesAndParse() throws Exception {
        AesGcm aesGcm = createAesGcm();
        byte[] plain = "payload test".getBytes(StandardCharsets.UTF_8);
        AesGcmPayload payload = aesGcm.encrypt(plain);

        byte[] combined = payload.toBytes();
        AesGcmPayload parsed = AesGcmPayload.parse(combined);

        Assert.assertArrayEquals(payload.getIv(), parsed.getIv());
        Assert.assertArrayEquals(payload.getCipherText(), parsed.getCipherText());

        byte[] decrypted = aesGcm.decrypt(parsed);
        Assert.assertArrayEquals(plain, decrypted);
    }

    @Test
    public void testPayloadParseWithCustomIvLength() throws Exception {
        AesGcm aesGcm = new AesGcm(new SecretKeySpec(KEY_128, "AES"), 16, 128);
        byte[] plain = "custom iv".getBytes(StandardCharsets.UTF_8);
        AesGcmPayload payload = aesGcm.encrypt(plain);

        Assert.assertEquals(16, payload.getIv().length);

        byte[] combined = payload.toBytes();
        AesGcmPayload parsed = AesGcmPayload.parse(combined, 16);
        byte[] decrypted = aesGcm.decrypt(parsed);
        Assert.assertArrayEquals(plain, decrypted);
    }

    @Test
    public void testStreamEncryptDecrypt() throws Exception {
        AesGcm aesGcm = createAesGcm();
        byte[] plain = "stream encryption test data".getBytes(StandardCharsets.UTF_8);

        ByteArrayOutputStream encOut = new ByteArrayOutputStream();
        aesGcm.encrypt(new ByteArrayInputStream(plain), encOut);
        byte[] encrypted = encOut.toByteArray();

        // 密文 = IV(12) + ciphertext + tag
        Assert.assertTrue(encrypted.length > 12);

        ByteArrayOutputStream decOut = new ByteArrayOutputStream();
        aesGcm.decrypt(new ByteArrayInputStream(encrypted), decOut);
        byte[] decrypted = decOut.toByteArray();

        Assert.assertArrayEquals(plain, decrypted);
    }

    @Test
    public void testPayloadParseTooShortThrows() {
        try {
            AesGcmPayload.parse(new byte[5]);
            Assert.fail("Should throw for too-short array");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testDecryptTamperedCipherThrows() throws Exception {
        AesGcm aesGcm = createAesGcm();
        byte[] plain = "integrity check".getBytes(StandardCharsets.UTF_8);
        AesGcmPayload payload = aesGcm.encrypt(plain);

        // 篡改密文
        byte[] tamperedCipher = payload.getCipherText().clone();
        tamperedCipher[0] ^= 0x01;
        AesGcmPayload tampered = new AesGcmPayload(payload.getIv(), tamperedCipher);

        try {
            aesGcm.decrypt(tampered);
            Assert.fail("Should throw for tampered ciphertext");
        } catch (Exception e) {
            // expected: GCM authentication tag mismatch
        }
    }

    @Test
    public void testConstants() {
        Assert.assertEquals("AES/GCM/NoPadding", AesGcm.TRANSFORMATION);
        Assert.assertEquals(12, AesGcm.DEFAULT_GCM_IV_LENGTH);
        Assert.assertEquals(128, AesGcm.DEFAULT_GCM_TAG_LENGTH);
    }
}
