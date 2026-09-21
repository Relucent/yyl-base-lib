package com.github.relucent.base.common.lang;

import org.junit.Assert;
import org.junit.Test;

public class ClassLoaderUtilTest {

	@Test
	public void testLoadClassByName() {
		Assert.assertEquals(String.class, ClassLoaderUtil.loadClass("java.lang.String"));
	}

	@Test
	public void testLoadPrimitive() {
		Assert.assertEquals(int.class, ClassLoaderUtil.loadClass("int"));
		Assert.assertEquals(int[].class, ClassLoaderUtil.loadClass("int[]"));
		Assert.assertEquals(String[].class, ClassLoaderUtil.loadClass("java.lang.String[]"));
	}

	@Test
	public void testLoadInnerClass() {
		// 自动将 "java.lang.Thread.State" 转换为 "java.lang.Thread$State" 加载
		Assert.assertEquals(Thread.State.class, ClassLoaderUtil.loadClass("java.lang.Thread.State"));
	}

	@Test
	public void testLoadPrimitiveClass() {
		Assert.assertEquals(int.class, ClassLoaderUtil.loadPrimitiveClass("int"));
		Assert.assertNull(ClassLoaderUtil.loadPrimitiveClass(""));
		Assert.assertNull(ClassLoaderUtil.loadPrimitiveClass("java.lang.String")); // 非原始类型名
	}

	@Test
	public void testIsPresent() {
		Assert.assertTrue(ClassLoaderUtil.isPresent("java.lang.String"));
		Assert.assertFalse(ClassLoaderUtil.isPresent("com.relucent.nonexistent.ClassXyz"));
	}

	@Test
	public void testGetClassLoader() {
		Assert.assertNotNull(ClassLoaderUtil.getClassLoader());
		Assert.assertNotNull(ClassLoaderUtil.getContextClassLoader());
	}
}
