package com.github.relucent.base.common.convert.impl;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link LocaleConverter} 单元测试
 */
public class LocaleConverterTest {

    @Test
    public void testConvertFromLocale() {
        Locale source = Locale.CHINA;
        Assert.assertSame(source, LocaleConverter.INSTANCE.convert(source, Locale.class));
    }

    @Test
    public void testConvertFromLanguage() {
        Assert.assertEquals(new Locale("en"), LocaleConverter.INSTANCE.convert("en", Locale.class));
    }

    @Test
    public void testConvertFromLanguageAndCountry() {
        Assert.assertEquals(new Locale("zh", "CN"), LocaleConverter.INSTANCE.convert("zh_CN", Locale.class));
    }

    @Test
    public void testConvertFromBlank() {
        Assert.assertNull(LocaleConverter.INSTANCE.convert("", Locale.class));
        Assert.assertNull(LocaleConverter.INSTANCE.convert(null, Locale.class));
    }

    @Test
    public void testConvertTruncatesExtraSegments() {
        // 固化既有语义：超过三段的输入只取前三段
        Assert.assertEquals(new Locale("a", "b", "c"), LocaleConverter.INSTANCE.convert("a_b_c_d", Locale.class));
    }
}
