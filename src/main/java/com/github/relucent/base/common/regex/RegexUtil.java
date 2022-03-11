package com.github.relucent.base.common.regex;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.relucent.base.common.lang.StringUtil;

/**
 * 正则相关工具<br>
 */
public class RegexUtil {

    /** 分组表达式($1,$2,$3...) */
    private final static Pattern GROUP_VARIABLE = Pattern.compile("\\$(\\d+)");

    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected RegexUtil() {
    }

    /**
     * 判断是否配正则
     * @param string 字符串
     * @param pattern 匹配模式
     * @return 如果字符串配则返回true，否则返回false
     */
    public static boolean match(String string, Pattern pattern) {
        if (StringUtil.isEmpty(string)) {
            return false;
        }
        return pattern.matcher(string).matches();
    }

    /**
     * 替换所有正则匹配的文本，并使用自定义函数决定如何替换。<br>
     * replacement可以通过{@link Matcher} 提取出匹配到的内容的不同部分，然后经过处理，返回需要替换的内容放回原位。
     * 
     * <pre>
     * RegexUtil.replaceAll("a1b2c3d4", Pattern.compile("\\d+"), matcher -&gt; Integer.toString(Integer.parseInt(matcher.group(0)) + 1))
     * // 结果为："a2b3c4d5"
     * </pre>
     * 
     * @param text 文本
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

    /**
     * 通过正则查找到字符串，并使用自定义替换模板进行替换。<br>
     * 在替换模板中，$1表示分组1的字符串
     * 
     * <pre>
     * replaceAll("hello 1234", "(\\d+)", "#$1"))
     * \\ 结果：hello #1#2#3#4
     * </pre>
     * 
     * @param text 文本
     * @param pattern 用于匹配的正则式
     * @param replacement 替换模板
     * @return 处理后的文本
     */

    public static String replaceAll(final CharSequence text, final Pattern pattern, final String replacement) {
        if (StringUtil.isEmpty(text)) {
            return text.toString();
        }
        final Matcher matcher = pattern.matcher(text);
        if (!matcher.find()) {
            return text.toString();
        }
        final Set<String> variableNumbers = findAll(replacement, GROUP_VARIABLE, 1, new HashSet<>());
        final StringBuffer sb = new StringBuffer();
        do {
            String replaced = replacement;
            for (String number : variableNumbers) {
                int group = Integer.parseInt(number);
                replaced = replacement.replace("$" + number, matcher.group(group));
            }
            matcher.appendReplacement(sb, replaced);
        } while (matcher.find());
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 取得内容中匹配的所有结果
     * @param <T> 集合类型
     * @param text 被查找的文本
     * @param pattern 编译后的正则模式
     * @param group 正则的分组
     * @param collection 存储结果的集合
     * @return 存储结果的集合
     */
    private static <T extends Collection<String>> T findAll(CharSequence text, Pattern pattern, int group, T collection) {
        if (text == null || pattern == null) {
            return null;
        }
        final Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            collection.add(matcher.group(group));
        }
        return collection;
    }
}
