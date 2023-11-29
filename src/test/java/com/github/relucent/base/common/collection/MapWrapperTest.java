package com.github.relucent.base.common.collection;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MapWrapperTest {

	private MapWrapper<String, String> sample;

	@Before
	public void before() {
		Map<String, String> origin = new HashMap<>();
		origin.put("K1", "V1");
		origin.put("K2", "V2");
		origin.put("K3", "V3");
		sample = new MapWrapper<>(origin);
	}

	@Test
	public void test() {
		for (Map.Entry<String, String> entry : sample.entrySet()) {
			Assert.assertEquals(sample.get(entry.getKey()), entry.getValue());
		}
		Assert.assertEquals(sample.put("K1", "V1+"), "V1");
		Assert.assertEquals(sample.remove("K1"), "V1+");
		Assert.assertNull(sample.get("K1"));
	}

}
