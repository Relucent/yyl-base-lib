package com.github.relucent.base.common.lang;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class AssertUtilTest {

    @Test
    public void testIsNull() {
        AssertUtil.isNull(null);
        try {
            AssertUtil.isNull("x");
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testNotNull() {
        AssertUtil.notNull("x");
        try {
            AssertUtil.notNull(null);
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testNotBlank() {
        AssertUtil.notBlank("x");
        try {
            AssertUtil.notBlank("   ");
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            AssertUtil.notBlank(null);
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testIsTrue() {
        AssertUtil.isTrue(true);
        try {
            AssertUtil.isTrue(false);
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testIsFalse() {
        AssertUtil.isFalse(false);
        try {
            AssertUtil.isFalse(true);
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testNotEmpty() {
        AssertUtil.notEmpty("x");
        try {
            AssertUtil.notEmpty("");
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            AssertUtil.notEmpty(null);
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
	public void testNoNullElements() {
		AssertUtil.noNullElements(new Object[] { "a", "b" });
		AssertUtil.noNullElements((Object[]) null); // null 数组视为无 null 元素，不抛异常
		try {
            AssertUtil.noNullElements(new Object[] { "a", null });
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testFail() {
        try {
            AssertUtil.fail("boom");
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("boom", e.getMessage());
        }
    }
}
