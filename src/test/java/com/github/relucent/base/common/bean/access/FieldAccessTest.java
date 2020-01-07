package com.github.relucent.base.common.bean.access;

import org.junit.Assert;
import org.junit.Test;

import com.github.relucent.base.common.bean.access.FieldAccess;

public class FieldAccessTest {

    @Test
    public void testGetSet() {
        FieldAccess access = FieldAccess.create(Sample.class);
        Sample sample = new Sample();
        sample.value = "A";
        Assert.assertEquals(access.get(sample, "value"), "A");
        access.set(sample, "value", "B");
        Assert.assertEquals(sample.value, "B");
    }

    private static class Sample {
        public String value;
    }
}
