package com.github.relucent.base.common.bean.access;

import org.junit.Assert;
import org.junit.Test;

import com.github.relucent.base.common.bean.access.ConstructorAccess;

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
