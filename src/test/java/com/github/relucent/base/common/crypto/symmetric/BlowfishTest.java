package com.github.relucent.base.common.crypto.symmetric;

import org.junit.Assert;
import org.junit.Test;

public class BlowfishTest {
    @Test
    public void testEncryptAndDecrypt() {
        Blowfish blowfish = Blowfish.create("password");
        String sample = "hello world";
        String ciphertext = blowfish.encryptString(sample);
        String plaintext = blowfish.decryptString(ciphertext);
        Assert.assertEquals(plaintext, sample);
    }
}
