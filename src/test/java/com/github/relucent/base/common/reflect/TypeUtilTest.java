package com.github.relucent.base.common.reflect;

import java.lang.reflect.Type;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

public class TypeUtilTest {

    @Test
    public void testGetTypeArgument() {
        @SuppressWarnings("serial")
        class Sample extends HashMap<String, Integer> {
        }
        Type p0 = TypeUtil.getTypeArgument(Sample.class, 0);
        Type p1 = TypeUtil.getTypeArgument(Sample.class, 1);
        Assert.assertEquals(String.class, p0);
        Assert.assertEquals(Integer.class, p1);
    }
}
