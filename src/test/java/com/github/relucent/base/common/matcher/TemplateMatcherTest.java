package com.github.relucent.base.common.matcher;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class TemplateMatcherTest {
    @Test
    public void testMatches() {
        TemplateMatcher matcher = new TemplateMatcher("Hello ${name}, your id is ${id}");
        Map<String, String> values = new HashMap<>();
        values.put("name", "Tom");
        values.put("id", "123");
        Assert.assertTrue(matcher.matches("Hello Tom, your id is 123", values));
        Assert.assertFalse(matcher.matches("Hello Tom, your id is 456", values));
    }
}
