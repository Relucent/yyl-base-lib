package com.github.relucent.base.common.lang;

import java.util.Locale;

import org.junit.Test;

import static org.junit.Assert.assertSame;

public class LocaleUtilTest {

    @Test
    public void testDefaultLocale() {
        assertSame(Locale.getDefault(), LocaleUtil.defaultLocale(null));

        Locale custom = Locale.CHINA;
        assertSame(custom, LocaleUtil.defaultLocale(custom));
    }
}
