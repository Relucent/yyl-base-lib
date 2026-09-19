package com.github.relucent.base.common.web;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.github.relucent.base.common.io.IoRuntimeException;

public class DownloadFileTest {

	@Test
	public void testWriteToCopiesContent() {
		byte[] content = "hello world".getBytes(StandardCharsets.UTF_8);
		try (DownloadFile file = new DownloadFile("a.txt", "text/plain", content)) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			file.writeTo(out);
			assertArrayEquals(content, out.toByteArray());
		}
	}

	@Test
	public void testWriteToPropagatesIoErrorInsteadOfSwallowing() {
		InputStream broken = new InputStream() {
			@Override
			public int read() throws IOException {
				throw new IOException("simulated read failure");
			}
		};
		try (DownloadFile file = new DownloadFile("a.txt", "text/plain", broken, 10L)) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			assertThrows(IoRuntimeException.class, () -> file.writeTo(out));
		}
	}
}
