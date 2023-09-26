package com.github.relucent.base.common.lang;

import com.github.relucent.base.common.constant.CharConstant;

/**
 * 命名规则封装
 */
public class NamingCaseUtil {

    /**
     * 将驼峰式命名（Camel-Case）的字符串转换为下划线命名（Under-Score-Case）。<br>
     * 下划线命名法也被称为蛇形命名法(Snake-Case)。<br>
     * 规则为：<br>
     * 1、单字之间以下划线隔开；<br>
     * 2、每个单字的首字母亦用小写字母。<br>
     * 
     * <pre>
     * 例如：
     * HelloWorld           hello_world
     * Hello_World          hello_world
     * HelloWorld_test      hello_world_test
     * </pre>
     *
     * @param name 转换前的驼峰式命名的字符串，也可以为下划线形式
     * @return 转换后下划线方式命名的字符串
     */
    public static String toUnderlineCase(final String name) {
        return toSymbolCase(name, CharConstant.UNDERLINE);
    }

    /**
     * 将命名字符串转换为短横连接命名（Kebab-Case）。<br>
     * 规则为：<br>
     * 1、单字之间横线线隔开；<br>
     * 2、每个单字的首字母亦用小写字母。<br>
     * 
     * <pre>
     * HelloWorld           hello-world
     * Hello_World          hello-world
     * HelloWorld_test      hello-world-test
     * </pre>
     *
     * @param name 转换前的命名字符串（驼峰式命名，或者下划线命名）
     * @return 转换后下划线方式命名的字符串
     */
    public static String toKebabCase(final String name) {
        return toSymbolCase(name, CharConstant.DASHED);
    }

    /**
     * 将驼峰式命名的字符串转换为指定符号的连接方式。<br>
     * @param name 转换前的命名的字符串
     * @param symbol 连接符
     * @return 转换后符号连接方式命名的字符串
     */
    public static String toSymbolCase(final String name, final char symbol) {
        if (name == null) {
            return null;
        }
        final int length = name.length();
        final StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char c = name.charAt(i);
            // 当前字母是大写
            if (Character.isUpperCase(c)) {
                // 非首字母
                if (i > 0) {
                    final char preChar = (i > 0) ? name.charAt(i - 1) : 0;
                    // 前一个字符为分隔符
                    if (preChar == symbol) {
                        // 已经是最后一个字符，或者后一个字符为小写
                        if (i == length - 1 || Character.isLowerCase(name.charAt(i + 1))) {
                            // 普通首字母大写，如_Abc -> _abc
                            c = Character.toLowerCase(c);
                        }
                    }
                    // 前一个字符是小写
                    else if (Character.isLowerCase(preChar)) {
                        buffer.append(symbol);
                        // 已经是最后一个字符，或者后一个字符为小写，或者后续是数字
                        // 例如：aBcd -> a_bcd
                        if (i == length - 1 || Character.isLowerCase(name.charAt(i + 1)) || CharUtil.isAsciiNumber(name.charAt(i + 1))) {
                            c = Character.toLowerCase(c);
                        }
                    }
                    // 前一个字符是大写
                    else {
                        // 不是最后一个字符，并且后续的字符为小写
                        if (i < length - 1 && Character.isLowerCase(name.charAt(i + 1))) {
                            // 普通首字母大写，如ABcc -> a_bcc
                            buffer.append(symbol);
                            c = Character.toLowerCase(c);
                        }
                    }
                }
                // 首字母
                else {
                    // 已经是最后一个字符，或者后一个字符为小写
                    if (i == length - 1 || Character.isLowerCase(name.charAt(i + 1))) {
                        // 普通首字母大写，如Abc -> abc
                        c = Character.toLowerCase(c);
                    }
                }
            }
            buffer.append(c);
        }
        return buffer.toString();
    }

    /**
     * 将下划线方式命名的字符串转换为帕斯卡式命名（Pascal）。<br>
     * 帕斯卡式命名法源自于Pascal语言的命名惯例，也被称为大驼峰式命名法（Upper Camel Case）。<br>
     * 规则为：<br>
     * 1、单字之间不以空格断开或连接号（-）、底线（_）连接；<br>
     * 2、第一个单词首字母采用大写字母；<br>
     * 3、后续单词的首字母亦用大写字母，例如：FirstName、LastName。
     * 
     * <pre>
     * hello_world          HelloWorld
     * </pre>
     * 
     * @param name 转换前的命名字符串
     * @return 转换后的驼峰式命名字符串
     */
    public static String toPascalCase(final String name) {
        return StringUtil.upperFirst(toCamelCase(name));
    }

    /**
     * 将下划线方式命名的字符串转换为驼峰式命名（Camel-Case）。<br>
     * 规则为：<br>
     * 1、单字之间不以空格或任何连接符断开；<br>
     * 2、第一个单字首字母采用小写字母；<br>
     * 3、后续单字的首字母亦用大写字母。<br>
     * 
     * <pre>
     * hello_world          helloWorld
     * </pre>
     * 
     * @param name 转换前的命名字符串
     * @return 转换后的驼峰式命名的字符串
     */
    public static String toCamelCase(final String name) {
        return toCamelCase(name, CharConstant.UNDERLINE);
    }

    /**
     * 将连接符方式命名的字符串转换为驼峰式。如果转换前的下划线大写方式命名的字符串为空，则返回空字符串。
     * @param name 转换前的自定义方式命名的字符串
     * @param symbol 原字符串中的连接符连接符
     * @return 转换后的驼峰式命名的字符串
     */
    public static String toCamelCase(final String name, final char symbol) {
        return toCamelCase(name, symbol, true);
    }

    /**
     * 将连接符方式命名的字符串转换为驼峰式命名 。<br>
     * @param name 转换前的自定义方式命名的字符串
     * @param symbol 原字符串中的连接符连接符
     * @param otherCharToLower 其他非连接符后的字符是否需要转为小写
     * @return 转换后的驼峰式命名的字符串
     */
    public static String toCamelCase(final String name, final char symbol, final boolean otherCharToLower) {
        if (name == null) {
            return null;
        }

        if (!StringUtil.contains(name, symbol)) {
            return name;
        }

        final int length = name.length();
        final StringBuilder buffer = new StringBuilder(length);
        boolean upperCase = false;
        for (int i = 0; i < length; i++) {
            final char c = name.charAt(i);
            if (c == symbol) {
                upperCase = true;
            } else if (upperCase) {
                buffer.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                buffer.append(otherCharToLower ? Character.toLowerCase(c) : c);
            }
        }
        return buffer.toString();
    }

    /**
     * 检查字符串中的字母是否全部为大写，判断依据如下：<br>
     * 1. 大写字母包括A-Z；<br>
     * 2. 其它非字母的Unicode符都算作大写。<br>
     * @param string 被检查的字符串
     * @return 是否全部为小写
     */
    public static boolean isUpperCase(final String string) {
        return StringUtil.isAllUpperCase(string);
    }

    /**
     * 检查字符串中的字母是否全部为小写，判断依据如下：<br>
     * 1. 小写字母包括a-z<br>
     * 2. 其它非字母的Unicode符都算作大写<br>
     * @param string 被检查的字符串
     * @return 是否全部为小写
     */
    public static boolean isLowerCase(final String string) {
        return StringUtil.isAllLowerCase(string);
    }

    /**
     * 切换给定字符串中的大小写。大写转小写，小写转大写。<br>
     * 
     * <pre>
     * NamingCaseUtil.swapCase(null)                 = null
     * NamingCaseUtil.swapCase("")                   = ""
     * NamingCaseUtil.swapCase("The dog has a BONE") = "tHE DOG HAS A bone"
     * </pre>
     *
     * @param string 字符串
     * @return 交换后的字符串
     */
    public static String swapCase(final String string) {
        return StringUtil.swapCase(string);
    }
}
