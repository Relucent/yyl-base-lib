package com.github.relucent.base.common.reflect.access;

import org.junit.Assert;
import org.junit.Test;

public class MethodAccessTest {

	@Test
	public void testGetSet() {
		MethodAccess access = MethodAccess.create(Sample.class);
		Sample sample = new Sample();
		sample.setValue("A");
		Assert.assertEquals(access.invoke(sample, "getValue", new Class[0]), "A");
		access.invoke(sample, "setValue", new Class[] { String.class }, "B");
		Assert.assertEquals(sample.getValue(), "B");
	}

	private static class Sample {

		private String value;

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}
}
