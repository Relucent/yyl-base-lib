package com.github.relucent.base.common.matcher;

import org.junit.Assert;
import org.junit.Test;

public class RegexMatcherTest {
    @Test
    public void testMatches() {
        RegexMatcher matcher = new RegexMatcher("^[a-z]+\\.csv$");
        Assert.assertTrue(matcher.matches("test.csv")); // true
        Assert.assertFalse(matcher.matches("Test.csv")); // false
    }
}
