package com.github.relucent.base.common.crypto.digest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Assert;
import org.junit.Test;

import com.github.relucent.base.common.constant.IoConstant;

public class Md5Test {
    @Test
    public void testDigest() {
        Md5 md5 = Md5.create();
        String sample1 = "hello";
        String sample2 = "welcome";
        String hash1 = md5.digestHex(sample1);
        String hash2 = md5.digestHex(sample2);
        Assert.assertNotEquals(hash1, hash2);
        Assert.assertEquals(hash1, md5.digestHex(sample1));
        Assert.assertEquals(hash2, md5.digestHex(sample2));
    }

    @Test
    public void testDigestSalt() {
        byte[] salt1 = {0x0, 0x1};
        byte[] salt2 = {0x1, 0x2};
        Md5 salt1md5 = Md5.create(salt1);
        Md5 salt2md5 = Md5.create(salt2);
        String sample = "hello";
        String salt1hash1 = salt1md5.digestHex(sample);
        String salt1hash2 = salt1md5.digestHex(sample);
        String salt2hash1 = salt2md5.digestHex(sample);
        String salt2hash2 = salt2md5.digestHex(sample);
        Assert.assertEquals(salt1hash1, salt1hash2);
        Assert.assertEquals(salt2hash1, salt2hash2);
        Assert.assertNotEquals(salt1hash1, salt2hash1);
    }

    @Test
    public void testDigestInputStream() throws Exception {
        byte[] salt = {0x1, 0x2, 0x3, 0x4, 0x5};
        int saltPosition = IoConstant.DEFAULT_BUFFER_SIZE + (IoConstant.DEFAULT_BUFFER_SIZE / 2);
        int digestCount = 2;
        Md5 md5 = Md5.create(salt, saltPosition, digestCount);
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            output.write(new byte[IoConstant.DEFAULT_BUFFER_SIZE]);
            output.write(new byte[IoConstant.DEFAULT_BUFFER_SIZE]);
            output.write(new byte[IoConstant.DEFAULT_BUFFER_SIZE]);
            byte[] large = output.toByteArray();
            try (ByteArrayInputStream input = new ByteArrayInputStream(large)) {
                byte[] hash1 = md5.digest(large);
                byte[] hash2 = md5.digest(input);
                Assert.assertArrayEquals(hash1, hash2);
            }
        }
    }
}
