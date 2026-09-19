package com.github.relucent.base.common.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class ContentTypeEnumTest {

    @Test
    public void testWildCardMimeTypeIsValid() {
        assertEquals("application/octet-stream", ContentTypeEnum.WILD_CARD.mimeType());
    }

    @Test
    public void testGetByExtensionDefaultReturnsWildCard() {
        assertSame(ContentTypeEnum.WILD_CARD, ContentTypeEnum.getByExtension("no-such-extension"));
    }

    @Test
    public void testGetByExtensionKnown() {
        assertEquals(ContentTypeEnum.PNG, ContentTypeEnum.getByExtension("png"));
        assertEquals(ContentTypeEnum.JSON, ContentTypeEnum.getByExtension("json"));
    }
}
