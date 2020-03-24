package com.github.relucent.base.common.jvm;

import org.junit.Assert;
import org.junit.Test;

public class JvmUtilTest {
	@Test
	public void testGetPid() {
		Assert.assertTrue(JvmUtil.getPid() != -1);
	}
}