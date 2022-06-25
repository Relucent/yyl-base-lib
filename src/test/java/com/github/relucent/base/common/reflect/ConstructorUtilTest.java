package com.github.relucent.base.common.reflect;

import java.lang.reflect.Constructor;

import org.junit.Assert;
import org.junit.Test;

public class ConstructorUtilTest {

    @Test
    public void testGetPublicConstructors() {
        Class<?> clazz = Sample.class;
        Constructor<?>[] constructors = ConstructorUtil.getPublicConstructors(clazz);
        Assert.assertEquals(2, constructors.length);
    }

    @Test
    public void testGetDeclaredConstructors() {
        Class<?> clazz = Sample.class;
        Constructor<?>[] constructors = ConstructorUtil.getDeclaredConstructors(clazz);
        Assert.assertEquals(5, constructors.length);
    }

    @Test
    public void testGetConstructor() {
        Class<?> clazz = Sample.class;
        Class<?>[] parameterTypes = { int.class, long.class };
        Constructor<?> constructor = ConstructorUtil.getConstructor(clazz, parameterTypes);
        Assert.assertNotNull(constructor);
    }

    @Test
    public void testGetMatchingConstructor() throws Exception {
        Class<?> clazz = Sample.class;
        Class<?>[] parameterTypes = { Long.class, Long.class, Long.class };
        Constructor<?> actual = ConstructorUtil.getMatchingConstructor(clazz, parameterTypes);
        Constructor<?> expected = Sample.class.getDeclaredConstructor(long[].class);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testInvokeConstructor() throws Exception {
        Class<Sample> clazz = Sample.class;
        Object[] args = { 1, 2L, (short) 3 };
        Sample sample = ConstructorUtil.invokeConstructor(clazz, args);
        System.out.println(sample);
    }

    @Test
    public void testInvokeExactConstructor() throws Exception {
        Class<Sample> clazz = Sample.class;
        long[] args = { 1L, 2L };
        Sample sample = ConstructorUtil.invokeExactConstructor(clazz, (long[]) args);
        System.out.println(sample);
    }

    static class Sample extends SampleSC {
        public Sample() {
            this(1L);
        }

        public Sample(Integer value) {
            super(value.longValue());
        }

        protected Sample(long value) {
            super(value);
        }

        protected Sample(int value, long variable) {
            super(value, variable);
        }

        protected Sample(long... value) {
            super(value);
        }
    }

    static class SampleSC {
        final long[] value;

        protected SampleSC(long... value) {
            this.value = value;
        }
    }
}
