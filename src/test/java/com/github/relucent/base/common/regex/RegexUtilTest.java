package com.github.relucent.base.common.regex;

import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

public class RegexUtilTest {
    @Test
    public void testReplaceAll() {
        String actual = RegexUtil.replaceAll("A1B2C3D4", Pattern.compile("\\d+"),
                matcher -> Integer.toString(Integer.parseInt(matcher.group(0)) * 2));
        String expected = "A2B4C6D8";
        System.out.println(actual);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testReplaceAll2() {
        String actual = RegexUtil.replaceAll("A1B2C3D4", Pattern.compile("(\\d)"), "#$1");
        String expected = "A#1B#2C#3D#4";
        System.out.println(actual);
        Assert.assertEquals(expected, actual);
    }
}
