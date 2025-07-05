package com.github.relucent.base.common.lang;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Test;

public class ObjectUtilTest {

    enum SampleEnum {
        A, B
    }

    static class NoArgClass {
        String value = "test";
    }

    static class ArgClass {
        int x;
        String y;

        public ArgClass(int x, String y) {
            this.x = x;
            this.y = y;
        }
    }

    @Test
    public void testPrimitiveTypes() {
        Assert.assertEquals(0, (int) ObjectUtil.newInstanceIfPossible(int.class));
        Assert.assertEquals(0L, (long) ObjectUtil.newInstanceIfPossible(long.class));
        Assert.assertEquals(false, (boolean) ObjectUtil.newInstanceIfPossible(boolean.class));
    }

    @Test
    public void testArrayType() {
        int[] intArray = ObjectUtil.newInstanceIfPossible(int[].class);
        Assert.assertNotNull(intArray);
        Assert.assertEquals(0, intArray.length);

        String[] strArray = ObjectUtil.newInstanceIfPossible(String[].class);
        Assert.assertNotNull(strArray);
        Assert.assertEquals(0, strArray.length);
    }

    @Test
    public void testEnumType() {
        SampleEnum e = ObjectUtil.newInstanceIfPossible(SampleEnum.class);
        Assert.assertNotNull(e);
        Assert.assertEquals(SampleEnum.A, e); // 返回第一个枚举
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testInterfaceOrAbstract() {
        Map map = ObjectUtil.newInstanceIfPossible(Map.class);
        Assert.assertNotNull(map);
        Assert.assertTrue(map instanceof HashMap);

        SortedMap smap = ObjectUtil.newInstanceIfPossible(SortedMap.class);
        Assert.assertNotNull(smap);
        Assert.assertTrue(smap instanceof TreeMap);

        List list = ObjectUtil.newInstanceIfPossible(List.class);
        Assert.assertNotNull(list);
        Assert.assertTrue(list instanceof ArrayList);

        Set set = ObjectUtil.newInstanceIfPossible(Set.class);
        Assert.assertNotNull(set);
        Assert.assertTrue(set instanceof HashSet);
    }

    @Test
    public void testNormalClasses() {
        NoArgClass obj1 = ObjectUtil.newInstanceIfPossible(NoArgClass.class);
        Assert.assertNotNull(obj1);
        Assert.assertEquals("test", obj1.value);

        ArgClass obj2 = ObjectUtil.newInstanceIfPossible(ArgClass.class);
        Assert.assertNotNull(obj2);
        Assert.assertEquals(0, obj2.x); // 默认值
        Assert.assertNull(obj2.y); // 默认值
    }

    @Test
    public void testNullClass() {
        Object obj = ObjectUtil.newInstanceIfPossible(null);
        Assert.assertNull(obj);
    }

    @Test
    public void testNonInstantiable() {
        Object obj = ObjectUtil.newInstanceIfPossible(AbstractMap.class);
        Assert.assertNotNull(obj);
        Assert.assertTrue(obj instanceof AbstractMap);
    }

    @Test
    public void testFunctionalInterfaceNoop() {
        Runnable actuals = ObjectUtil.newInstanceIfPossible(Runnable.class);
        Assert.assertNotNull(actuals);
        // should be a NoOpRunnable (or subclass), but running must not throw
        actuals.run();
    }

}
