package com.github.relucent.base.common.lang;

import org.junit.Assert;
import org.junit.Test;

public class StringUtilTest {

    @Test
    public void testIsEmpty() {
        Assert.assertTrue(StringUtil.isEmpty(""));
        Assert.assertTrue(StringUtil.isEmpty(null));
        Assert.assertFalse(StringUtil.isEmpty(" "));
        Assert.assertFalse(StringUtil.isEmpty("null"));
        Assert.assertFalse(StringUtil.isEmpty("abc"));
    }

    @Test
    public void testIsNotEmpty() {
        Assert.assertFalse(StringUtil.isNotEmpty(""));
        Assert.assertFalse(StringUtil.isNotEmpty(null));
        Assert.assertTrue(StringUtil.isNotEmpty(" "));
        Assert.assertTrue(StringUtil.isNotEmpty("null"));
        Assert.assertTrue(StringUtil.isNotEmpty("abc"));
    }

    @Test
    public void testLength() {
        Assert.assertEquals(StringUtil.length(""), 0);
        Assert.assertEquals(StringUtil.length(null), 0);
        Assert.assertEquals(StringUtil.length("123"), 3);
        Assert.assertEquals(StringUtil.length("\n"), 1);
    }

    @Test
    public void testTrim() {
        Assert.assertNull(StringUtil.trim(null));
        Assert.assertEquals(StringUtil.trim(""), "");
        Assert.assertEquals(StringUtil.trim(" abc "), "abc");
        Assert.assertEquals(StringUtil.trim("\nABC\t"), "ABC");
        Assert.assertEquals(StringUtil.trim("\nA B C\t"), "A B C");
    }

    @Test
    public void testTrimToEmpty() {
        Assert.assertEquals(StringUtil.trimToEmpty(null), "");
        Assert.assertEquals(StringUtil.trimToEmpty(""), "");
        Assert.assertEquals(StringUtil.trimToEmpty(" abc "), "abc");
        Assert.assertEquals(StringUtil.trimToEmpty("\nABC\t"), "ABC");
        Assert.assertEquals(StringUtil.trimToEmpty("\nA B C\t"), "A B C");
    }

    @Test
    public void testDeleteWhitespace() {
        Assert.assertNull(StringUtil.deleteWhitespace(null));
        Assert.assertEquals(StringUtil.deleteWhitespace(""), "");
        Assert.assertEquals(StringUtil.deleteWhitespace(" abc "), "abc");
        Assert.assertEquals(StringUtil.deleteWhitespace("\nABC\t"), "ABC");
        Assert.assertEquals(StringUtil.deleteWhitespace("\nA B C\t"), "ABC");
    }

    @Test
    public void testSplit2() {
        Assert.assertNull(StringUtil.split(null, "*"));
        Assert.assertEquals(StringUtil.split("", "*").length, 0);
        Assert.assertArrayEquals(StringUtil.split("a b c", null), strings("a", "b", "c"));
        Assert.assertArrayEquals(StringUtil.split("a.b.c", "."), strings("a", "b", "c"));
        Assert.assertArrayEquals(StringUtil.split("a..b.c.", "."), strings("a", "b", "c"));
    }

    @Test
    public void testSplit3() {
        Assert.assertNull(StringUtil.split(null, "*", (int) (Math.random() * 100)));
        Assert.assertEquals(StringUtil.split("", "*", (int) (Math.random() * 100)).length, 0);
        Assert.assertArrayEquals(StringUtil.split("ab cd ef", null, 0), strings("ab", "cd", "ef"));
        Assert.assertArrayEquals(StringUtil.split("ab  cd ef", null, 0), strings("ab", "cd", "ef"));
        Assert.assertArrayEquals(StringUtil.split("ab:cd:ef", ":", 0), strings("ab", "cd", "ef"));
        Assert.assertArrayEquals(StringUtil.split("ab:cd:ef", ":", 2), strings("ab", "cd:ef"));
        Assert.assertArrayEquals(StringUtil.split("ab cd ef", null, 2), strings("ab", "cd ef"));
        Assert.assertArrayEquals(StringUtil.split("ab cd ef", null, 3), strings("ab", "cd", "ef"));
        Assert.assertArrayEquals(StringUtil.split("ab  cd ef", null, 4), strings("ab", "cd", "ef"));
    }

    @Test
    public void testSplitPreserveAllTokens2() {
        Assert.assertNull(StringUtil.splitPreserveAllTokens(null, "*"));
        Assert.assertEquals(StringUtil.splitPreserveAllTokens("", "*").length, 0);
        Assert.assertArrayEquals(StringUtil.splitPreserveAllTokens("a b c", null), strings("a", "b", "c"));
        Assert.assertArrayEquals(StringUtil.splitPreserveAllTokens("a.b.c", "."), strings("a", "b", "c"));
        Assert.assertArrayEquals(StringUtil.splitPreserveAllTokens("a..b.c.", "."), strings("a", "", "b", "c", ""));
    }

    @Test
    public void testSplitPreserveAllTokens3() {
        Assert.assertNull(StringUtil.splitPreserveAllTokens(null, "*", (int) (Math.random() * 100)));
        Assert.assertEquals(StringUtil.splitPreserveAllTokens("", "*", (int) (Math.random() * 100)).length, 0);
        Assert.assertArrayEquals(StringUtil.splitPreserveAllTokens("ab cd ef", null, 0), strings("ab", "cd", "ef"));
        Assert.assertArrayEquals(StringUtil.splitPreserveAllTokens("ab  cd ef", null, 0), strings("ab", "", "cd", "ef"));
        Assert.assertArrayEquals(StringUtil.splitPreserveAllTokens("ab:cd:ef", ":", 0), strings("ab", "cd", "ef"));
        Assert.assertArrayEquals(StringUtil.splitPreserveAllTokens("ab:cd:ef", ":", 2), strings("ab", "cd:ef"));
        Assert.assertArrayEquals(StringUtil.splitPreserveAllTokens("ab cd ef", null, 2), strings("ab", "cd ef"));
        Assert.assertArrayEquals(StringUtil.splitPreserveAllTokens("ab cd ef", null, 3), strings("ab", "cd", "ef"));
        Assert.assertArrayEquals(StringUtil.splitPreserveAllTokens("ab  cd ef", null, 4), strings("ab", "", "cd", "ef"));
    }

    @Test
    public void testSplitPurify() {
        Assert.assertArrayEquals(StringUtil.splitPurify(null, ""), strings());
        Assert.assertArrayEquals(StringUtil.splitPurify(null, null), strings());
        Assert.assertArrayEquals(StringUtil.splitPurify("abc", null), strings("abc"));
        Assert.assertArrayEquals(StringUtil.splitPurify("ab cd ef", null), strings("ab", "cd", "ef"));
        Assert.assertArrayEquals(StringUtil.splitPurify("ab:cd:ef", ":"), strings("ab", "cd", "ef"));
        Assert.assertArrayEquals(StringUtil.splitPurify("ab:::cd::ef", ":"), strings("ab", "cd", "ef"));
        Assert.assertArrayEquals(StringUtil.splitPurify(":::ab:cd:ef", ":"), strings("ab", "cd", "ef"));
    }

    @Test
    public void testLeftPad() {
        Assert.assertEquals("00001", StringUtil.leftPad("1", 5, '0'));
        Assert.assertEquals("123", StringUtil.leftPad("123", 2, '0'));
    }

    @Test
    public void testRightPad() {
        Assert.assertEquals("10000", StringUtil.rightPad("1", 5, '0'));
        Assert.assertEquals("123", StringUtil.rightPad("123", 2, '0'));
    }

    private final String[] strings(final String... strings) {
        return strings;
    }
}
