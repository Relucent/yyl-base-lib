package com.github.relucent.base.common.matcher;

import org.junit.Assert;
import org.junit.Test;

public class WildcardMatcherTest {
    @Test
    public void testMatches() {
        WildcardMatcher matcher = new WildcardMatcher("*.csv");
        Assert.assertTrue(matcher.matches("test.csv"));
        Assert.assertFalse(matcher.matches("readme.txt"));
    }
}
