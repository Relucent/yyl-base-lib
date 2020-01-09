package com.github.relucent.base.common.crypto.symmetric;

import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;

public class DesTest {
    @Test
    public void testEncryptAndDecrypt() {
        byte[] key = {0x0, 0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7, 0x8};
        Des des = Des.create(key);
        String sample = "hello world";
        // HEX
        {
            String ciphertext = des.encryptHex(sample);
            String plaintext = des.decryptHexString(ciphertext);
            Assert.assertEquals(plaintext, sample);
        }
        // BASE64
        {
            String ciphertext = des.encryptBase64(sample);
            String plaintext = des.decryptBase64String(ciphertext);
            Assert.assertEquals(plaintext, sample);
        }
        // BYTES
        {
            byte[] sampleBytes = sample.getBytes(StandardCharsets.UTF_8);
            byte[] ciphertext = des.encrypt(sampleBytes);
            byte[] plaintext = des.decrypt(ciphertext);
            Assert.assertArrayEquals(plaintext, sampleBytes);
        }
    }
}
