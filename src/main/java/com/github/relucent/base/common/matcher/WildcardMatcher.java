package com.github.relucent.base.common.matcher;

import java.util.regex.Pattern;

/**
 * 通配符匹配工具类，用于支持常见的通配符模式： <br>
 * '*' 匹配任意字符序列，'?' 匹配任意单字符。 <br>
 * 内部将通配符转换为正则表达式进行高效匹配。<br>
 */
public class WildcardMatcher {

    private final Pattern pattern;

    /**
     * 构造方法，将通配符模式编译为正则
     * @param wildcard 支持 * 和 ?，例如 "*.csv", "data-??.txt"
     */
    public WildcardMatcher(String wildcard) {
        String regex = wildcardToRegex(wildcard);
        this.pattern = Pattern.compile(regex);
    }

    /**
     * 判断输入字符串是否匹配通配符模式
     * @param input 待匹配字符串
     * @return true 如果匹配，否则 false
     */
    public boolean matches(String input) {
        return pattern.matcher(input).matches();
    }

    /**
     * 将通配符模式转换为正则表达式
     * @param wildcard 通配符模式
     * @return 正则表达式
     */
    private static String wildcardToRegex(String wildcard) {
        StringBuilder sb = new StringBuilder();
        sb.append("^");
        for (int i = 0; i < wildcard.length(); i++) {
            char c = wildcard.charAt(i);
            switch (c) {
            case '*':
                sb.append(".*");
                break;
            case '?':
                sb.append(".");
                break;
            case '.':
                sb.append("\\.");
                break;
            case '\\':
                sb.append("\\\\");
                break;
            default:
                if ("+()^$|{}[]".indexOf(c) != -1) {
                    sb.append("\\").append(c);
                } else {
                    sb.append(c);
                }
            }
        }
        sb.append("$");
        return sb.toString();
    }
}
