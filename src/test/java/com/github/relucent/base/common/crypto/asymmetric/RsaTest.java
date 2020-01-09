package com.github.relucent.base.common.crypto.asymmetric;

import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;

public class RsaTest {
    @Test
    public void testEncryptAndDecrypt() {
        String sample = "hello world";
        Rsa rsa = Rsa.create();
        // HEX
        {
            String ciphertext = rsa.encryptHex(sample, KeyType.PRIVATE);
            String plaintext = rsa.decryptHexString(ciphertext, KeyType.PUBLIC);
            Assert.assertEquals(plaintext, sample);
        }
        {
            String ciphertext = rsa.encryptHex(sample, KeyType.PUBLIC);
            String plaintext = rsa.decryptHexString(ciphertext, KeyType.PRIVATE);
            Assert.assertEquals(plaintext, sample);
        }
        // BASE64
        {
            String ciphertext = rsa.encryptBase64(sample, KeyType.PRIVATE);
            String plaintext = rsa.decryptBase64String(ciphertext, KeyType.PUBLIC);
            Assert.assertEquals(plaintext, sample);
        }
        {
            String ciphertext = rsa.encryptBase64(sample, KeyType.PUBLIC);
            String plaintext = rsa.decryptBase64String(ciphertext, KeyType.PRIVATE);
            Assert.assertEquals(plaintext, sample);
        }
        // BYTES
        {
            byte[] bytes = sample.getBytes(StandardCharsets.UTF_8);
            byte[] ciphertext = rsa.encrypt(bytes, KeyType.PRIVATE);
            byte[] plaintext = rsa.decrypt(ciphertext, KeyType.PUBLIC);
            Assert.assertArrayEquals(plaintext, bytes);
        }
        {
            byte[] bytes = sample.getBytes(StandardCharsets.UTF_8);
            byte[] ciphertext = rsa.encrypt(bytes, KeyType.PUBLIC);
            byte[] plaintext = rsa.decrypt(ciphertext, KeyType.PRIVATE);
            Assert.assertArrayEquals(plaintext, bytes);
        }
    }
}
