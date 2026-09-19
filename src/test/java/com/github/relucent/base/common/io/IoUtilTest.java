package com.github.relucent.base.common.io;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

public class IoUtilTest {

    @Test
    public void testCopyRoundTrip() throws IOException {
        byte[] data = "io copy round trip".getBytes("UTF-8");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        IoUtil.copy(new ByteArrayInputStream(data), out);
        assertArrayEquals(data, out.toByteArray());
    }

    @Test
    public void testToString() throws IOException {
        String text = "你好，世界";
        String result = IoUtil.toString(new ByteArrayInputStream(text.getBytes("UTF-8")));
        assertEquals(text, result);
    }
}
