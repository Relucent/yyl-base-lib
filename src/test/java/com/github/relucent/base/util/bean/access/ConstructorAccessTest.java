package com.github.relucent.base.util.bean.access;

import org.junit.Assert;
import org.junit.Test;

public class ConstructorAccessTest {

    @Test
    public void testNewInstance() {
        ConstructorAccess<Sample> access = ConstructorAccess.create(Sample.class);
        Sample sample = access.newInstance();
        Assert.assertNotNull(sample);
    }

    private static class Sample {

    }
}
