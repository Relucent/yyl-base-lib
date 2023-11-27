package com.github.relucent.base.common.lang;

import org.junit.Assert;
import org.junit.Test;

public class CharSequenceUtilTest {
	@Test
	public void testEndWith() {
		Assert.assertTrue(CharSequenceUtil.endWith("123", "123"));
		Assert.assertTrue(CharSequenceUtil.endWith("abcdef", "def"));
		Assert.assertTrue(CharSequenceUtil.endWith("ABCDEF", "DEF"));
		Assert.assertFalse(CharSequenceUtil.endWith("ABCDEF", "def"));
		Assert.assertFalse(CharSequenceUtil.endWith("ABCDEF", "ABC"));
		Assert.assertFalse(CharSequenceUtil.endWith("ABCDEF", "ABCDEFG"));
	}

	@Test
	public void testEndWithIgnoreCase() {
		Assert.assertTrue(CharSequenceUtil.endWithIgnoreCase("123", "123"));
		Assert.assertTrue(CharSequenceUtil.endWithIgnoreCase("abcdef", "def"));
		Assert.assertTrue(CharSequenceUtil.endWithIgnoreCase("ABCDEF", "DEF"));
		Assert.assertFalse(CharSequenceUtil.endWithIgnoreCase("ABCDEF", "abc"));
		Assert.assertFalse(CharSequenceUtil.endWithIgnoreCase("ABCDEF", "ABC"));
		Assert.assertFalse(CharSequenceUtil.endWithIgnoreCase("ABCDEF", "ABCDEFG"));
	}
}
