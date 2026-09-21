package com.github.relucent.base.common.lang;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class ServiceLoaderUtilTest {

	/** SPI 服务接口（测试用） */
	public static interface DemoService {
		String hello();
	}

	/** SPI 服务实现（测试用），需在 META-INF/services 中注册 */
	public static class DemoServiceImpl implements DemoService {
		@Override
		public String hello() {
			return "hello";
		}
	}

	/** 没有 SPI 实现的接口 */
	public static interface UnknownService {
	}

	@Test
	public void testLoadFirst() {
		DemoService svc = ServiceLoaderUtil.loadFirst(DemoService.class);
		Assert.assertNotNull(svc);
		Assert.assertEquals("hello", svc.hello());
	}

	@Test
	public void testLoadList() {
		List<DemoService> list = ServiceLoaderUtil.loadList(DemoService.class);
		Assert.assertFalse(list.isEmpty());
		Assert.assertEquals("hello", list.get(0).hello());
	}

	@Test
	public void testLoadUnknown() {
		// 无 SPI 实现应返回空结果，不抛异常
		Assert.assertNull(ServiceLoaderUtil.loadFirst(UnknownService.class));
		Assert.assertTrue(ServiceLoaderUtil.loadList(UnknownService.class).isEmpty());
	}
}
