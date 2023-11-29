package com.github.relucent.base.common.codec;

import org.junit.Assert;
import org.junit.Test;

public class Utf8Test {
	@Test
	public void testEncodeAndDecode() {
		String plaintext = "东风夜放花千树,更吹落,星如雨。";
		byte[] encoded = Utf8.encode(plaintext);
		String decoded = Utf8.decode(encoded);
		Assert.assertEquals(plaintext, decoded);
	}
}
