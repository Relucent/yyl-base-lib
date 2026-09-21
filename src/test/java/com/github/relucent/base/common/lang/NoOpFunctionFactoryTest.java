package com.github.relucent.base.common.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;

import org.junit.Test;

public class NoOpFunctionFactoryTest {

	@FunctionalInterface
	interface CustomFI {
		String doSomething(String input);

		default int count() {
			return 0;
		}
	}

	@Test
	public void testIsFunctionalInterface() {
		assertTrue(NoOpFunctionFactory.isFunctionalInterface(Runnable.class));
		assertTrue(NoOpFunctionFactory.isFunctionalInterface(Function.class));
		assertFalse(NoOpFunctionFactory.isFunctionalInterface(String.class));
		assertFalse(NoOpFunctionFactory.isFunctionalInterface(List.class));
		assertFalse(NoOpFunctionFactory.isFunctionalInterface(null));
	}

	@Test
	public void testGetNoOpInstanceBuiltinRunnable() {
		Object r = NoOpFunctionFactory.getNoOpInstance(Runnable.class);
		assertTrue(r instanceof Runnable);
		((Runnable) r).run(); // 不抛异常
	}

	@Test
	public void testGetNoOpInstanceCallable() throws Exception {
		Callable<?> c = NoOpFunctionFactory.getNoOpInstance(Callable.class);
		assertNull(c.call());
	}

	@Test
	public void testGetNoOpInstanceProxy() {
		CustomFI fi = NoOpFunctionFactory.getNoOpInstance(CustomFI.class);
		assertNotNull(fi);
		assertNull(fi.doSomething("x")); // 对象返回类型 -> null
		assertEquals(0, fi.count()); // 基本类型返回 -> 默认值 0
	}

	@Test
	public void testGetNoOpInstanceNonFI() {
		assertNull(NoOpFunctionFactory.getNoOpInstance(List.class));
	}

	@Test
	public void testGetNoOpInstanceNull() {
		assertNull(NoOpFunctionFactory.getNoOpInstance(null));
	}

	@Test
	public void testProxyFunctionApply() {
		@SuppressWarnings("unchecked")
		Function<String, Integer> f = NoOpFunctionFactory.getNoOpInstance(Function.class);
		assertNull(f.apply("anything"));
		try {
			f.andThen(Object::toString);
		} catch (Exception e) {
			fail("default method should be usable");
		}
	}
}
