package com.github.relucent.base.common.web;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DownloadModeTest {

    @Test
    public void testAttachmentQuotesFilename() {
        assertEquals("attachment;filename=\"foo.txt\"", DownloadMode.ATTACHMENT.getContentDisposition("foo.txt"));
    }

    @Test
    public void testInlineQuotesFilename() {
        assertEquals("inline;filename=\"bar.dat\"", DownloadMode.INLINE.getContentDisposition("bar.dat"));
    }

    @Test
    public void testQuotedFilenameHandlesSpace() {
        assertEquals("attachment;filename=\"my file.txt\"", DownloadMode.ATTACHMENT.getContentDisposition("my file.txt"));
    }
}
