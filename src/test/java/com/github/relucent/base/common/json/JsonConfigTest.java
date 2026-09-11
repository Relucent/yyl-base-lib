package com.github.relucent.base.common.json;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link JsonConfig} 测试
 */
public class JsonConfigTest {

	@Test
	public void testDefaultConfig() {
		JsonConfig config = new JsonConfig.Builder().build();
		Assert.assertFalse(config.isIgnoreError());
		Assert.assertFalse(config.isWriteDateAsTimestamps());
		Assert.assertTrue(config.isIgnoreNullValue());
		Assert.assertTrue(config.isTransientSupport());
		Assert.assertTrue(config.isStripTrailingZeros());
		Assert.assertEquals(0, config.getIndentFactor());
	}

	@Test
	public void testBuilder() {
		JsonConfig config = new JsonConfig.Builder()//
				.setIgnoreError(true)//
				.setWriteDateAsTimestamps(true)//
				.setIgnoreNullValue(false)//
				.setTransientSupport(false)//
				.setStripTrailingZeros(false)//
				.setIndentFactor(4)//
				.build();
		Assert.assertTrue(config.isIgnoreError());
		Assert.assertTrue(config.isWriteDateAsTimestamps());
		Assert.assertFalse(config.isIgnoreNullValue());
		Assert.assertFalse(config.isTransientSupport());
		Assert.assertFalse(config.isStripTrailingZeros());
		Assert.assertEquals(4, config.getIndentFactor());
	}

	@Test
	public void testRebuildFromConfig() {
		JsonConfig original = new JsonConfig.Builder()//
				.setIgnoreError(true)//
				.setWriteDateAsTimestamps(true)//
				.setIgnoreNullValue(false)//
				.setIndentFactor(2)//
				.build();
		JsonConfig rebuilt = original.builder()//
				.build();
		Assert.assertEquals(original.isIgnoreError(), rebuilt.isIgnoreError());
		Assert.assertEquals(original.isWriteDateAsTimestamps(), rebuilt.isWriteDateAsTimestamps());
		Assert.assertEquals(original.isIgnoreNullValue(), rebuilt.isIgnoreNullValue());
		Assert.assertEquals(original.isTransientSupport(), rebuilt.isTransientSupport());
		Assert.assertEquals(original.isStripTrailingZeros(), rebuilt.isStripTrailingZeros());
		Assert.assertEquals(original.getIndentFactor(), rebuilt.getIndentFactor());
	}
}
