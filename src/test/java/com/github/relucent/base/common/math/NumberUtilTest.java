package com.github.relucent.base.common.math;

import org.junit.Assert;
import org.junit.Test;

import com.github.relucent.base.common.lang.NumberUtil;

public class NumberUtilTest {
    @Test
    public void isNumberTest() {
        Assert.assertTrue(NumberUtil.isNumber("-1"));
        Assert.assertTrue(NumberUtil.isNumber("-1L"));
        Assert.assertTrue(NumberUtil.isNumber("-1.0F"));
        Assert.assertTrue(NumberUtil.isNumber("-1.0D"));
        Assert.assertTrue(NumberUtil.isNumber("1"));
        Assert.assertTrue(NumberUtil.isNumber("1L"));
        Assert.assertTrue(NumberUtil.isNumber("1F"));
        Assert.assertTrue(NumberUtil.isNumber("1D"));
        Assert.assertTrue(NumberUtil.isNumber("0xFF"));
        Assert.assertTrue(NumberUtil.isNumber("0XFF"));
        Assert.assertTrue(NumberUtil.isNumber("07"));
        Assert.assertFalse(NumberUtil.isNumber("08"));
        Assert.assertFalse(NumberUtil.isNumber(""));
    }
}
