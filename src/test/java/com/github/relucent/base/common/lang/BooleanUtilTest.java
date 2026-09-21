package com.github.relucent.base.common.lang;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class BooleanUtilTest {

    @Test
    public void testToPrimitiveBoolean() {
        assertTrue(BooleanUtil.toPrimitiveBoolean("true"));
        assertTrue(BooleanUtil.toPrimitiveBoolean("YES"));
        assertTrue(BooleanUtil.toPrimitiveBoolean(" 1 "));
        assertTrue(BooleanUtil.toPrimitiveBoolean("是"));
        assertTrue(BooleanUtil.toPrimitiveBoolean("√"));

        assertFalse(BooleanUtil.toPrimitiveBoolean("false"));
        assertFalse(BooleanUtil.toPrimitiveBoolean("no"));
        assertFalse(BooleanUtil.toPrimitiveBoolean(""));
        assertFalse(BooleanUtil.toPrimitiveBoolean(null));
        assertFalse(BooleanUtil.toPrimitiveBoolean("abc"));
    }

    @Test
    public void testToBoolean() {
        assertEquals(Boolean.TRUE, BooleanUtil.toBoolean("TRUE"));
        assertEquals(Boolean.TRUE, BooleanUtil.toBoolean(" 是 "));
        assertEquals(Boolean.FALSE, BooleanUtil.toBoolean("no"));
        assertEquals(Boolean.FALSE, BooleanUtil.toBoolean("0"));

        assertNull(BooleanUtil.toBoolean("maybe"));
        assertNull(BooleanUtil.toBoolean(""));
        assertNull(BooleanUtil.toBoolean(null));
    }
}
