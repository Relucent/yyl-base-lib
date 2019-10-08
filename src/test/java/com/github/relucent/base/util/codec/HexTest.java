package com.github.relucent.base.util.codec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class HexTest {

    @Test
    public void testDecodeAndEncode() throws IOException {

        byte[] original;
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            for (int i = Byte.MIN_VALUE; i <= Byte.MAX_VALUE; i++) {
                output.write((byte) i);
            }
            original = output.toByteArray();
        }

        char[] encoded = Hex.encodeHex(original);
        String encodedString = Hex.encodeHexString(original);
        char[] upperEncoded = Hex.encodeHex(original, false);
        String upperEncodedString = Hex.encodeHexString(original, false);

        Assert.assertEquals(original.length * 2, encoded.length);
        Assert.assertEquals(encodedString.toLowerCase(), encodedString);
        Assert.assertEquals(upperEncodedString.toUpperCase(), upperEncodedString);

        byte[] decoded1 = Hex.decodeHex(encoded);
        byte[] decoded2 = Hex.decodeHex(encodedString);
        byte[] decoded3 = Hex.decodeHex(upperEncoded);
        byte[] decoded4 = Hex.decodeHex(upperEncodedString);

        Assert.assertArrayEquals(decoded1, original);
        Assert.assertArrayEquals(decoded2, original);
        Assert.assertArrayEquals(decoded3, original);
        Assert.assertArrayEquals(decoded4, original);
    }
}
