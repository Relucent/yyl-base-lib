package com.github.relucent.base.common.matcher;

import java.util.regex.Pattern;

/**
 * 正则匹配工具类，用于判断字符串是否符合指定正则表达式。<br>
 * 支持预编译 Pattern，重复匹配效率高。<br>
 */
public class RegexMatcher {

    private final Pattern pattern;

    /**
     * 构造方法，将正则表达式编译为 Pattern
     * @param regex 正则表达式，例如 "^[a-z]+\\.csv$"
     */
    public RegexMatcher(String regex) {
        this.pattern = Pattern.compile(regex);
    }

    /**
     * 判断输入字符串是否匹配正则
     * @param input 待匹配字符串
     * @return true 如果匹配，否则 false
     */
    public boolean matches(String input) {
        return pattern.matcher(input).matches();
    }
}
