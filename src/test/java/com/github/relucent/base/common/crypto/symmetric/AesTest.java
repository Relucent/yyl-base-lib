package com.github.relucent.base.common.crypto.symmetric;

import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;

public class AesTest {
    @Test
    public void testEncryptAndDecrypt() {
        byte[] key = {0x0, 0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9, 0xA, 0xB, 0xC, 0xD, 0xE, 0xF};
        Aes aes = Aes.create(key);
        String sample = "hello world";
        // HEX
        {
            String ciphertext = aes.encryptHex(sample);
            String plaintext = aes.decryptHexString(ciphertext);
            Assert.assertEquals(plaintext, sample);
        }
        // BASE64
        {
            String ciphertext = aes.encryptBase64(sample);
            String plaintext = aes.decryptBase64String(ciphertext);
            Assert.assertEquals(plaintext, sample);
        }
        // BYTES
        {
            byte[] input = sample.getBytes(StandardCharsets.UTF_8);
            byte[] ciphertext = aes.encrypt(input);
            byte[] plaintext = aes.decrypt(ciphertext);
            Assert.assertArrayEquals(plaintext, input);
        }
    }
}
