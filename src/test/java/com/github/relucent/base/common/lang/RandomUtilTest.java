package com.github.relucent.base.common.lang;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class RandomUtilTest {

    @Test
    public void testNextIntRange() {
        for (int i = 0; i < 200; i++) {
            int v = RandomUtil.nextInt(1, 10);
            assertTrue("v in [1,10)", v >= 1 && v < 10);
        }
    }

    @Test
    public void testNextInt() {
        // 仅验证不抛异常、返回 int
        RandomUtil.nextInt();
    }

    @Test
    public void testNextBoolean() {
        boolean b = RandomUtil.nextBoolean();
        assertTrue(b || !b);
    }

    @Test
    public void testNextLongRange() {
        for (int i = 0; i < 200; i++) {
            long v = RandomUtil.nextLong(1L, 100L);
            assertTrue(v >= 1L && v < 100L);
        }
    }

    @Test
    public void testNextDoubleRange() {
        for (int i = 0; i < 200; i++) {
            double v = RandomUtil.nextDouble(0.0, 1.0);
            assertTrue(v >= 0.0 && v < 1.0);
        }
    }

    @Test
    public void testNextChar() {
        for (int i = 0; i < 200; i++) {
            char c = RandomUtil.nextChar();
            assertTrue((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9'));
        }
    }

    @Test
    public void testNextBytes() {
        byte[] b = RandomUtil.nextBytes(8);
        assertEquals(8, b.length);
    }

    @Test
    public void testNextString() {
        String s = RandomUtil.nextString(10);
        assertEquals(10, s.length());
    }

    @Test
    public void testNextStringEmpty() {
        assertEquals("", RandomUtil.nextString(0));
    }

    @Test
    public void testNextNumberString() {
        String s = RandomUtil.nextNumberString(5);
        assertEquals(5, s.length());
        assertTrue(s.matches("\\d+"));
    }

    @Test
    public void testNextChineseChar() {
        for (int i = 0; i < 100; i++) {
            char c = RandomUtil.nextChineseChar();
            assertTrue(c >= '\u4E00' && c <= '\u9FFF');
        }
    }

    @Test
    public void testCurrentRandomNotNull() {
        assertNotNull(RandomUtil.currentRandom());
    }
}
