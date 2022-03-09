package com.github.relucent.base.common.regex;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.relucent.base.common.lang.StringUtil;

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

    /**
     * 替换所有正则匹配的文本，并使用自定义函数决定如何替换<br>
     * replacement可以通过{@link Matcher} 提取出匹配到的内容的不同部分，然后经过处理，返回需要替换的内容放回原位。
     * 
     * <pre class="code">
     * RegexUtil.replaceAll("a1b2c3d4", Pattern.compile("\\d+"), matcher -> Integer.toString(Integer.parseInt(matcher.group(0)) + 1))
     * // 结果为："a2b3c4d5"
     * </pre>
     * 
     * @param text 要替换的字符串
     * @param pattern 用于匹配的正则式
     * @param replacement 决定如何替换的函数
     * @return 替换后的字符串
     */
    public static String replaceAll(CharSequence text, Pattern pattern, Function<Matcher, String> replacement) {
        if (StringUtil.isEmpty(text)) {
            return text.toString();
        }
        final Matcher matcher = pattern.matcher(text);
        final StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(buffer, replacement.apply(matcher));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }
}
