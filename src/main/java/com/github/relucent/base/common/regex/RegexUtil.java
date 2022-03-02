package com.github.relucent.base.common.regex;

import java.util.regex.Pattern;

/**
 * 正则相关工具<br>
 */
public class RegexUtil {

    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected RegexUtil() {
    }

    /**
     * 给定内容与正则是否匹配
     * @param pattern 模式
     * @param content 内容
     * @return 匹配则返回true，否则返回false
     */
    public static boolean matches(Pattern pattern, CharSequence content) {
        if (content == null || pattern == null) {
            return false;
        }
        return pattern.matcher(content).matches();
    }
}
