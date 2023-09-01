package com.github.relucent.base.common.reflect.internal;

import org.junit.Assert;
import org.junit.Test;

import com.github.relucent.base.common.reflect.TypeReference;

public class ObjectConstructorTest {
    @Test
    public void testNewInstance() {

        ObjectConstructor<Sample> constructor = ObjectConstructorCache.INSTANCE.get(TypeReference.of(Sample.class));

        Sample sample = constructor.construct();

        Assert.assertNotNull(sample);

        sample.setValue("123");

        Assert.assertEquals(sample.getValue(), "123");
    }

    private static class Sample {

        private String value;

        private Sample(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
