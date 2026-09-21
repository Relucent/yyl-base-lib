package com.github.relucent.base.common.lang;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class RandomStringUtilTest {

    @Test
    public void testRandomAlphabetic() {
        String s = RandomStringUtil.randomAlphabetic(10);
        assertEquals(10, s.length());
        assertTrue(s.matches("[a-zA-Z]+"));
    }

    @Test
    public void testRandomAlphanumeric() {
        String s = RandomStringUtil.randomAlphanumeric(10);
        assertEquals(10, s.length());
        assertTrue(s.matches("[a-zA-Z0-9]+"));
    }

    @Test
    public void testRandomNumeric() {
        String s = RandomStringUtil.randomNumeric(10);
        assertEquals(10, s.length());
        assertTrue(s.matches("[0-9]+"));
    }

    @Test
    public void testRandomAscii() {
        String s = RandomStringUtil.randomAscii(20);
        assertEquals(20, s.length());
        for (char c : s.toCharArray()) {
            assertTrue(c >= 32 && c <= 126);
        }
    }

    @Test
    public void testRandomGraph() {
        String s = RandomStringUtil.randomGraph(20);
        assertEquals(20, s.length());
        for (char c : s.toCharArray()) {
            assertTrue(c >= 33 && c <= 126);
        }
    }

    @Test
    public void testRandomLetters() {
        String s = RandomStringUtil.random(20, true, false);
        assertEquals(20, s.length());
        assertTrue(s.matches("[a-zA-Z]+"));
    }

    @Test
    public void testRandomWithChars() {
        String s = RandomStringUtil.random(10, "ABC".toCharArray());
        assertEquals(10, s.length());
        assertTrue(s.matches("[ABC]+"));
    }

    @Test
    public void testRandomWithCharsNull() {
        String s = RandomStringUtil.random(5, (char[]) null);
        assertEquals(5, s.length());
    }

    @Test
    public void testRandomEmpty() {
        assertEquals("", RandomStringUtil.random(0));
    }

    @Test
    public void testRandomNegativeThrows() {
        try {
            RandomStringUtil.random(-1);
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testRandomRangeIllegal() {
        try {
            RandomStringUtil.random(5, 10, 5, false, false);
            fail("should throw IllegalArgumentException (end <= start)");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testRandomAlphabeticRange() {
        String s = RandomStringUtil.randomAlphabetic(3, 8);
        assertTrue(s.length() >= 3 && s.length() < 8);
        assertTrue(s.matches("[a-zA-Z]+"));
    }

    @Test
    public void testRandomCountNegativeStillThrows() {
        boolean threw = false;
        try {
            RandomStringUtil.random(-5, "XY".toCharArray());
        } catch (IllegalArgumentException e) {
            threw = true;
        }
        assertTrue(threw);
    }

    @Test
    public void testRandomWithString() {
        String s = RandomStringUtil.random(6, "xyz");
        assertEquals(6, s.length());
        assertTrue(s.matches("[xyz]+"));
        assertFalse(s.isEmpty());
    }
}
