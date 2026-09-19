package com.github.relucent.base.common.io;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

public class GzipIoUtilTest {

    @Test
    public void testGzipUngzipRoundTrip() throws IOException {
        byte[] original = "hello gzip world".getBytes("UTF-8");
        ByteArrayOutputStream compressed = new ByteArrayOutputStream();
        GzipIoUtil.gzip(new ByteArrayInputStream(original), compressed);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GzipIoUtil.ungzip(new ByteArrayInputStream(compressed.toByteArray()), out);
        assertArrayEquals(original, out.toByteArray());
    }

    @Test
    public void testGzipBufferedTargetNotLosingData() throws IOException {
        byte[] original = "buffered target must not lose data".getBytes("UTF-8");
        // 用 BufferedOutputStream 包裹，且不主动 close，验证 gzip 内部已 flush 底层 target
        ByteArrayOutputStream sink = new ByteArrayOutputStream();
        BufferedOutputStream buffered = new BufferedOutputStream(sink);
        GzipIoUtil.gzip(new ByteArrayInputStream(original), buffered);

        byte[] compressed = sink.toByteArray();
        assertTrue("缓冲流未 flush 导致压缩数据为空/不完整", compressed.length > 0);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GzipIoUtil.ungzip(new ByteArrayInputStream(compressed), out);
        assertArrayEquals(original, out.toByteArray());
    }
}
