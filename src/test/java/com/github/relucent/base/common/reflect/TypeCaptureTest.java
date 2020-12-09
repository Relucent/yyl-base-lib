package com.github.relucent.base.common.reflect;

import java.text.ParseException;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

public class TypeCaptureTest {
    @Test
    public void testCapture() throws ParseException {
        TypeCapture<Date> typeCapture = new TypeCapture<Date>() {
        };
        Assert.assertEquals(Date.class, typeCapture.capture());
    }
}
