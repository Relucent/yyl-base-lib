package com.github.relucent.base.common.lang;

import java.util.Arrays;
import java.util.Iterator;

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
	public void testIsBlank() {
		Assert.assertTrue(StringUtil.isBlank(""));
		Assert.assertTrue(StringUtil.isBlank(" "));
		Assert.assertFalse(StringUtil.isBlank("hello"));
		Assert.assertFalse(StringUtil.isBlank("h e l l o"));
		Assert.assertFalse(StringUtil.isBlank(" hello "));
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
	public void testJoin1() {
		Assert.assertNull(StringUtil.join((Iterable<?>) null, null));
		Assert.assertEquals("AAABBBCCCDDD", StringUtil.join(Arrays.asList("AAA", "BBB", "CCC", "DDD"), null));
		Assert.assertEquals("AAA||BBB||CCC||DDD", StringUtil.join(Arrays.asList("AAA", "BBB", "CCC", "DDD"), "||"));
	}

	@Test
	public void testJoinPurify() {
		String separator = "||";
		String[] sample1 = { "A", " ", "B", "", "C", null, "D" };
		Iterable<?> sample2 = Arrays.asList(sample1);
		Iterator<?> sample3 = sample2.iterator();
		String expected = "A||B||C||D";
		Assert.assertEquals(expected, StringUtil.joinPurify(sample1, separator));
		Assert.assertEquals(expected, StringUtil.joinPurify(sample2, separator));
		Assert.assertEquals(expected, StringUtil.joinPurify(sample3, separator));
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

	@Test
	public void testReplace() {
		Assert.assertEquals("A1**C3D4E5", StringUtil.replace("A1B2C3D4E5", "B2", "**"));
		Assert.assertEquals("A1B2C3D4E5", StringUtil.replace("A1B2C3D4E5", "F6", "**"));
	}

	@Test
	public void testRemovePrefix() {
		Assert.assertEquals(StringUtil.removePrefix("12345678", "1234"), "5678");
		Assert.assertEquals(StringUtil.removePrefix("ABCDEFG", "ABCDEFG"), "");
		Assert.assertEquals(StringUtil.removePrefix("ABCDEFG", "ABCDEFGH"), "ABCDEFG");
		Assert.assertEquals(StringUtil.removePrefix("ABCDEFG", "EFG"), "ABCDEFG");
		Assert.assertEquals(StringUtil.removePrefix("ABCDEFG", "abc"), "ABCDEFG");
		Assert.assertEquals(StringUtil.removePrefixIgnoreCase("ABCDEFG", "abc"), "DEFG");
	}

	@Test
	public void testRemoveSuffix() {
		Assert.assertEquals(StringUtil.removeSuffix("12345678", "1234"), "12345678");
		Assert.assertEquals(StringUtil.removeSuffix("ABCDEFG", "ABCDEFG"), "");
		Assert.assertEquals(StringUtil.removeSuffix("ABCDEFG", "EFG"), "ABCD");
		Assert.assertEquals(StringUtil.removeSuffix("ABCDEFG", "efg"), "ABCDEFG");
		Assert.assertEquals(StringUtil.removeSuffix("ABCDEFG", "abc"), "ABCDEFG");
		Assert.assertEquals(StringUtil.removeSuffixIgnoreCase("ABCDEFG", "efg"), "ABCD");
	}

	private final String[] strings(final String... strings) {
		return strings;
	}
}
