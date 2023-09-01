package com.github.relucent.base.common.bean;

import org.junit.Assert;
import org.junit.Test;

public class DynaBeanTest {

    @Test
    public void testGetSet() {
        Sample sample = new Sample();
        sample.setValue("A");

        DynaBean dyna = new DynaBean(sample);

        Assert.assertEquals(dyna.get("value"), "A");
        dyna.set("value", "B");
        Assert.assertEquals(sample.getValue(), "B");
    }

    private static class Sample {

        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
