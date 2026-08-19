package com.github.relucent.base.common.convert.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link BeanConverter} 单元测试
 */
public class BeanConverterTest {

	public static class Person {
		private String name;
		private int age;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}
	}

	@Test
	public void testConvertNull() {
		Assert.assertNull(BeanConverter.INSTANCE.convert(null, Person.class));
	}

	@Test
	public void testConvertMapToBean() {
		Map<String, Object> source = new LinkedHashMap<>();
		source.put("name", "Alice");
		source.put("age", 30);
		Object result = BeanConverter.INSTANCE.convert(source, Person.class);
		Assert.assertNotNull(result);
		Assert.assertTrue(result instanceof Person);
		Person person = (Person) result;
		Assert.assertEquals("Alice", person.getName());
		Assert.assertEquals(30, person.getAge());
	}

	@Test
	public void testConvertBeanToBean() {
		Person source = new Person();
		source.setName("Bob");
		source.setAge(25);
		Object result = BeanConverter.INSTANCE.convert(source, Person.class);
		Assert.assertNotNull(result);
		Assert.assertTrue(result instanceof Person);
		Assert.assertNotSame(source, result);
		Person person = (Person) result;
		Assert.assertEquals("Bob", person.getName());
		Assert.assertEquals(25, person.getAge());
	}

	@Test
	public void testConvertPartialMapToBean() {
		// 缺少字段时使用默认值
		Map<String, Object> source = new LinkedHashMap<>();
		source.put("name", "Charlie");
		Object result = BeanConverter.INSTANCE.convert(source, Person.class);
		Assert.assertNotNull(result);
		Person person = (Person) result;
		Assert.assertEquals("Charlie", person.getName());
		Assert.assertEquals(0, person.getAge());
	}
}
