package com.github.relucent.base.common.lang;

import java.util.Locale;

/**
 * 地区工具类
 */
public class LocaleUtil {

    private LocaleUtil() {
    }

    /**
     * 如果参数不为空则直接返回，如果参数为空则返回默认的地区{@link locale#getDefault()}
     * @param locale 地区
     * @return 参数不为空则直接返回，如果参数为空则返回默认的地区
     */
    public static Locale defaultLocale(final Locale locale) {
        return locale != null ? locale : Locale.getDefault();
    }
}
