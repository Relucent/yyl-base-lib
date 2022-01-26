package com.github.relucent.base.common.identifier;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class NanoIdTest {

    @Test
    public void uniqueTest() {
        int count = 10000;
        final Set<String> idSet = new HashSet<>(count);
        for (int i = 0; i < 10000; i++) {
            idSet.add(NanoId.randomNanoId());
        }
        Assert.assertEquals(count, idSet.size());
    }

    @Test
    public void successTest() {
        final Random random = new Random(0);
        final char[] alphabet = "_-0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        final int size = 21;
        final String[] expecteds = { //
                "uOuVSfniU9hXYKTpQ7HxU", //
                "HBg6Fbbj9KP_x0pwbDJqJ", //
                "MOPKLeaHdwx4KgqemBZuQ", //
                "Ouo_6xR9tdq01qCQUSre9", //
                "pKDD80g1zAW4ks6U-0uXI",// "
        };
        for (final String expected : expecteds) {
            final String actual = NanoId.randomNanoId(random, alphabet, size);
            Assert.assertEquals(expected, actual);
        }
    }

    @Test
    public void variousSizeTest() {
        for (int size = 1; size <= 1000; size++) {
            final String id = NanoId.randomNanoId(size);
            Assert.assertEquals(size, id.length());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void randomNanoId_EmptyAlphabet_ExceptionThrown() {
        NanoId.randomNanoId(new char[0], -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void limit256AlphabetExceptionThrown() {
        final char[] largeAlphabet = new char[256];
        for (int i = 0; i < 256; i++) {
            largeAlphabet[i] = (char) i;
        }
        NanoId.randomNanoId(largeAlphabet, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeSizeExceptionThrown() {
        NanoId.randomNanoId(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void zeroSizeExceptionThrown() {
        NanoId.randomNanoId(0);
    }
}
