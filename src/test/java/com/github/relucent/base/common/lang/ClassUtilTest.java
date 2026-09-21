package com.github.relucent.base.common.lang;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Assert;
import org.junit.Test;

public class ClassUtilTest {

	// 用于测试 getTypeArgument 的泛型解析
	static class GenericBox<T> {
	}

	static class StringBox extends GenericBox<String> {
	}

	@Test
	public void testGetSimpleName() {
		Assert.assertEquals("String", ClassUtil.getSimpleName(String.class));
		Assert.assertEquals("", ClassUtil.getSimpleName((Class<?>) null));
		Assert.assertEquals("X", ClassUtil.getSimpleName((Class<?>) null, "X"));
		Assert.assertEquals("String", ClassUtil.getSimpleName((Object) "abc"));
	}

	@Test
	public void testGetName() {
		Assert.assertEquals("java.lang.String", ClassUtil.getName(String.class));
		Assert.assertEquals("", ClassUtil.getName((Class<?>) null));
		Assert.assertEquals("java.lang.String", ClassUtil.getName((Object) "abc"));
	}

	@Test
	public void testGetPackageName() {
		Assert.assertEquals("java.lang", ClassUtil.getPackageName(String.class));
		Assert.assertEquals("java.lang", ClassUtil.getPackageName("java.lang.String"));
		Assert.assertEquals("a.b", ClassUtil.getPackageName("a.b.C"));
		Assert.assertEquals("", ClassUtil.getPackageName("int"));
	}

	@Test
	public void testIsAssignable() {
		Assert.assertTrue(ClassUtil.isAssignable(Integer.class, Number.class));
		Assert.assertTrue(ClassUtil.isAssignable(int.class, Integer.class)); // 自动装箱
		Assert.assertTrue(ClassUtil.isAssignable(Integer.TYPE, Long.TYPE)); // int 可加宽到 long
		Assert.assertTrue(ClassUtil.isAssignable((Class<?>) null, String.class)); // null 可赋给引用类型
		Assert.assertFalse(ClassUtil.isAssignable((Class<?>) null, int.class)); // null 不可赋给原始类型
		Assert.assertFalse(ClassUtil.isAssignable(String.class, int.class));
		Assert.assertFalse(ClassUtil.isAssignable(boolean.class, int.class));
	}

	@Test
	public void testPrimitiveWrapper() {
		Assert.assertEquals(Integer.class, ClassUtil.primitiveToWrapper(int.class));
		Assert.assertEquals(int.class, ClassUtil.wrapperToPrimitive(Integer.class));
		Assert.assertNull(ClassUtil.wrapperToPrimitive(String.class));
		Assert.assertTrue(ClassUtil.isPrimitiveWrapper(Integer.class));
		Assert.assertFalse(ClassUtil.isPrimitiveWrapper(int.class));
		Assert.assertTrue(ClassUtil.isPrimitiveOrWrapper(int.class));
		Assert.assertTrue(ClassUtil.isPrimitiveOrWrapper(Integer.class));
		Assert.assertFalse(ClassUtil.isPrimitiveOrWrapper(String.class));
	}

	@Test
	public void testIsNormalClass() {
		Assert.assertTrue(ClassUtil.isNormalClass(String.class));
		Assert.assertTrue(ClassUtil.isNormalClass(ArrayList.class));
		Assert.assertFalse(ClassUtil.isNormalClass(int.class));
		Assert.assertFalse(ClassUtil.isNormalClass(Runnable.class)); // 接口
		// Integer 非接口/非抽象/非枚举/非数组/非注解/非原始/非合成 -> 视为普通类
		Assert.assertTrue(ClassUtil.isNormalClass(Integer.class));
	}

	@Test
	public void testGetDefaultValue() {
		Assert.assertEquals(Integer.valueOf(0), ClassUtil.getDefaultValue(int.class));
		Assert.assertEquals(Boolean.FALSE, ClassUtil.getDefaultValue(boolean.class));
		Assert.assertEquals(Double.valueOf(0.0d), ClassUtil.getDefaultValue(double.class));
		Assert.assertNull(ClassUtil.getDefaultValue(String.class));
	}

	@Test
	public void testGetDefaultImplementation() {
		// 具体类直接返回自身
		Assert.assertEquals(ArrayList.class, ClassUtil.getDefaultImplementation(ArrayList.class));
		// 注册表精确命中
		Assert.assertEquals(ArrayList.class, ClassUtil.getDefaultImplementation(List.class));
		Assert.assertEquals(HashSet.class, ClassUtil.getDefaultImplementation(Set.class));
		// 注意：Map.class 返回 LinkedHashMap（注册表精确命中，与 javadoc 示例一致）
		Assert.assertEquals(LinkedHashMap.class, ClassUtil.getDefaultImplementation(Map.class));
		Assert.assertEquals(ConcurrentHashMap.class, ClassUtil.getDefaultImplementation(ConcurrentHashMap.class));
		// 函数式接口使用 No-op 实现
		Assert.assertEquals(NoOpFunctionFactory.NoOpRunnable.class, ClassUtil.getDefaultImplementation(Runnable.class));
		// 无法推断实现的接口返回 null
		Assert.assertNull(ClassUtil.getDefaultImplementation(Comparable.class));
		Assert.assertNull(ClassUtil.getDefaultImplementation(EnumSet.class)); // EnumSet 需要具体 enum 类型
	}

	@Test
	public void testGetTypeArgument() {
		Assert.assertEquals(String.class, ClassUtil.getTypeArgument(StringBox.class));
		// Object 无任何泛型父/接口，应返回 null
		Assert.assertNull(ClassUtil.getTypeArgument(Object.class));
		// 注：String 实现了 Comparable<String>，getTypeArgument 会取第一个泛型父接口的类型参数，
		// 因此 ClassUtil.getTypeArgument(String.class) 实际返回 String.class（而非 null）。
	}
}
