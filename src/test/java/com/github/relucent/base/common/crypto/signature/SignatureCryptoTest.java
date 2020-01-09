package com.github.relucent.base.common.crypto.signature;

import org.junit.Assert;
import org.junit.Test;

public class SignatureCryptoTest {
    @Test
    public void testDigest() {
        SignatureCrypto signatureCrypto = new SignatureCrypto(SignatureAlgorithm.SHA384withECDSA);
        byte[] data1 = "hello".getBytes();
        byte[] data2 = "welcome".getBytes();
        byte[] sign1 = signatureCrypto.sign(data1);
        byte[] sign2 = signatureCrypto.sign(data2);
        Assert.assertTrue(signatureCrypto.verify(data1, sign1));
        Assert.assertTrue(signatureCrypto.verify(data2, sign2));
        Assert.assertFalse(signatureCrypto.verify(data2, sign1));
        Assert.assertFalse(signatureCrypto.verify(data1, sign2));
    }
}
