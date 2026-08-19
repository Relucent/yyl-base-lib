package com.github.relucent.base.common.convert.impl;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link CharacterConverter} 单元测试
 */
public class CharacterConverterTest {

	@Test
	public void testConvertNull() {
		Assert.assertNull(CharacterConverter.INSTANCE.convert(null, Character.class));
	}

	@Test
	public void testConvertCharacter() {
		Assert.assertEquals(Character.valueOf('A'),
				CharacterConverter.INSTANCE.convert(Character.valueOf('A'), Character.class));
	}

	@Test
	public void testConvertBoolean() {
		Assert.assertEquals(Character.valueOf((char) 1),
				CharacterConverter.INSTANCE.convert(Boolean.TRUE, Character.class));
		Assert.assertEquals(Character.valueOf((char) 0),
				CharacterConverter.INSTANCE.convert(Boolean.FALSE, Character.class));
	}

	@Test
	public void testConvertString() {
		Assert.assertEquals(Character.valueOf('A'), CharacterConverter.INSTANCE.convert("ABC", Character.class));
		Assert.assertEquals(Character.valueOf('a'), CharacterConverter.INSTANCE.convert("a", Character.class));
	}

	@Test
	public void testConvertEmptyString() {
		Assert.assertNull(CharacterConverter.INSTANCE.convert("", Character.class));
	}

	@Test
	public void testConvertNumber() {
		// Number.toString() 取首字符
		Assert.assertEquals(Character.valueOf('1'), CharacterConverter.INSTANCE.convert(123, Character.class));
	}

	@Test
	public void testConvertWithDefault() {
		Assert.assertEquals(Character.valueOf('X'),
				CharacterConverter.INSTANCE.convert("", Character.class, Character.valueOf('X')));
	}
}
