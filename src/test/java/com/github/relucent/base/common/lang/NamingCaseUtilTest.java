package com.github.relucent.base.common.lang;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.github.relucent.base.common.constant.CharConstant;

public class NamingCaseUtilTest {

    @Test
    public void toCamelCaseTest() {
        Map<String, String> sample = new LinkedHashMap<>();
        sample.put("Hello_World", "helloWorld");
        sample.put("hello_world", "helloWorld");
        sample.put("HelloWorld", "HelloWorld");
        sample.forEach((name, expected) -> {
            String actual = NamingCaseUtil.toCamelCase(name);
            Assert.assertEquals(expected, actual);
        });
    }

    @Test
    public void toUnderLineCaseTest() {
        Map<String, String> sample = new LinkedHashMap<>();
        sample.put("Hello_World", "hello_world");
        sample.put("_Hello_World_", "_hello_world_");
        sample.put("_HelloWorld", "_hello_world");
        sample.put("HelloWORLD", "hello_WORLD");
        sample.forEach((name, expected) -> {
            String actual = NamingCaseUtil.toUnderlineCase(name);
            System.out.println(actual);
            Assert.assertEquals(expected, actual);
        });
    }

    @Test
    public void toCamelCaseDashedTest() {
        Map<String, String> sample = new LinkedHashMap<>();
        sample.put("Hello-World", "helloWorld");
        sample.forEach((name, expected) -> {
            String actual = NamingCaseUtil.toCamelCase(name, CharConstant.DASHED);
            Assert.assertEquals(expected, actual);
        });
    }
}
