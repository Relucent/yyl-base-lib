package com.github.relucent.base.common.convert.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link MapConverter} 单元测试
 */
public class MapConverterTest {

    public static class SampleBean {
        private String name = "default";
        private int age = 0;

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
        // 脏数据约定：source 为 null 时返回 null
        Map<?, ?> result = MapConverter.INSTANCE.convert(null, Map.class);
        Assert.assertNull(result);
    }

    @Test
    public void testConvertMapToMap() {
        Map<String, Object> source = new LinkedHashMap<>();
        source.put("name", "Alice");
        source.put("age", 30);
        Map<?, ?> result = MapConverter.INSTANCE.convert(source, Map.class);
        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("Alice", result.get("name"));
        Assert.assertEquals(30, result.get("age"));
    }

    @Test
    public void testConvertBeanToMap() {
        SampleBean bean = new SampleBean();
        bean.setName("Bob");
        bean.setAge(25);
        Map<?, ?> result = MapConverter.INSTANCE.convert(bean, Map.class);
        Assert.assertNotNull(result);
        Assert.assertEquals("Bob", result.get("name"));
        Assert.assertEquals(25, result.get("age"));
    }

    @Test
    public void testConvertToLinkedHashMap() {
        Map<String, Object> source = new LinkedHashMap<>();
        source.put("a", 1);
        Map<?, ?> result = MapConverter.INSTANCE.convert(source, LinkedHashMap.class);
        Assert.assertTrue(result instanceof LinkedHashMap);
        Assert.assertEquals(1, result.size());
    }
}
