package com.github.relucent.base.common.lang;

import com.github.relucent.base.common.constant.CharConstant;

/**
 * 字符工具类<br>
 */
public class CharUtil {

    /**
     * 是否为ASCII字符，ASCII字符位于0~127之间
     * 
     * <pre>
     *   CharUtil.isAscii('a')  = true
     *   CharUtil.isAscii('A')  = true
     *   CharUtil.isAscii('3')  = true
     *   CharUtil.isAscii('-')  = true
     *   CharUtil.isAscii('\n') = true
     *   CharUtil.isAscii('&copy;') = false
     * </pre>
     * 
     * @param ch 被检查的字符
     * @return 如果被检查的字符为ASCII字符，返回{@code true}
     */
    public static boolean isAscii(final char ch) {
        return ch < 128;
    }

    /**
     * 是否为可见ASCII字符，可见字符位于32~126之间
     *
     * <pre>
     *   CharUtil.isAsciiPrintable('a')  = true
     *   CharUtil.isAsciiPrintable('A')  = true
     *   CharUtil.isAsciiPrintable('3')  = true
     *   CharUtil.isAsciiPrintable('-')  = true
     *   CharUtil.isAsciiPrintable('\n') = false
     *   CharUtil.isAsciiPrintable('&copy;') = false
     * </pre>
     *
     * @param ch 被检查的字符
     * @return 如果被检查的字符为ASCII可见字符，返回{@code true}
     */
    public static boolean isAsciiPrintable(final char ch) {
        return ch >= 32 && ch < 127;
    }

    /**
     * 是否为ASCII控制符（不可见字符），控制符位于0~31和127
     *
     * <pre>
     *   CharUtil.isAsciiControl('a')  = false
     *   CharUtil.isAsciiControl('A')  = false
     *   CharUtil.isAsciiControl('3')  = false
     *   CharUtil.isAsciiControl('-')  = false
     *   CharUtil.isAsciiControl('\n') = true
     *   CharUtil.isAsciiControl('&copy;') = false
     * </pre>
     *
     * @param ch 被检查的字符
     * @return 如果被检查的字符为ASCII控制符，返回{@code true}
     */
    public static boolean isAsciiControl(final char ch) {
        return ch < 32 || ch == 127;
    }

    /**
     * 判断是否为字母（包括大写字母和小写字母）<br>
     * 字母包括A~Z和a~z
     *
     * <pre>
     *   CharUtil.isAsciiAlpha('a')  = true
     *   CharUtil.isAsciiAlpha('A')  = true
     *   CharUtil.isAsciiAlpha('3')  = false
     *   CharUtil.isAsciiAlpha('-')  = false
     *   CharUtil.isAsciiAlpha('\n') = false
     *   CharUtil.isAsciiAlpha('&copy;') = false
     * </pre>
     *
     * @param ch 被检查的字符
     * @return 如果被检查的字符为字母，返回{@code true}
     */
    public static boolean isAsciiAlpha(final char ch) {
        return isAsciiAlphaUpper(ch) || isAsciiAlphaLower(ch);
    }

    /**
     * <p>
     * 判断是否为大写字母，大写字母包括A~Z
     * </p>
     *
     * <pre>
     *   CharUtil.isAsciiAlphaUpper('a')  = false
     *   CharUtil.isAsciiAlphaUpper('A')  = true
     *   CharUtil.isAsciiAlphaUpper('3')  = false
     *   CharUtil.isAsciiAlphaUpper('-')  = false
     *   CharUtil.isAsciiAlphaUpper('\n') = false
     *   CharUtil.isAsciiAlphaUpper('&copy;') = false
     * </pre>
     *
     * @param ch 被检查的字符
     * @return 如果被检查的字符为大写字母，返回{@code true}
     */
    public static boolean isAsciiAlphaUpper(final char ch) {
        return ch >= 'A' && ch <= 'Z';
    }

    /**
     * <p>
     * 检查字符是否为小写字母，小写字母指a~z
     * </p>
     *
     * <pre>
     *   CharUtil.isAsciiAlphaLower('a')  = true
     *   CharUtil.isAsciiAlphaLower('A')  = false
     *   CharUtil.isAsciiAlphaLower('3')  = false
     *   CharUtil.isAsciiAlphaLower('-')  = false
     *   CharUtil.isAsciiAlphaLower('\n') = false
     *   CharUtil.isAsciiAlphaLower('&copy;') = false
     * </pre>
     *
     * @param ch 被检查的字符
     * @return 如果被检查的字符为小写字母，返回{@code true}
     */
    public static boolean isAsciiAlphaLower(final char ch) {
        return ch >= 'a' && ch <= 'z';
    }

    /**
     * <p>
     * 检查是否为数字字符，数字字符指0~9
     * </p>
     *
     * <pre>
     *   CharUtil.isAsciiNumber('a')  = false
     *   CharUtil.isAsciiNumber('A')  = false
     *   CharUtil.isAsciiNumber('3')  = true
     *   CharUtil.isAsciiNumber('-')  = false
     *   CharUtil.isAsciiNumber('\n') = false
     *   CharUtil.isAsciiNumber('&copy;') = false
     * </pre>
     *
     * @param ch 被检查的字符
     * @return 如果被检查的字符为数字字符，返回{@code true}
     */
    public static boolean isAsciiNumber(final char ch) {
        return ch >= '0' && ch <= '9';
    }

    /**
     * 是否为字母或数字，包括A~Z、a~z、0~9
     *
     * <pre>
     *   CharUtil.isAsciiAlphaOrNumber('a')  = true
     *   CharUtil.isAsciiAlphaOrNumber('A')  = true
     *   CharUtil.isAsciiAlphaOrNumber('3')  = true
     *   CharUtil.isAsciiAlphaOrNumber('-')  = false
     *   CharUtil.isAsciiAlphaOrNumber('\n') = false
     *   CharUtil.isAsciiAlphaOrNumber('&copy;') = false
     * </pre>
     *
     * @param ch 被检查的字符
     * @return true表示为字母或数字，包括A~Z、a~z、0~9
     */
    public static boolean isAsciiAlphaOrNumber(final char ch) {
        return isAsciiAlpha(ch) || isAsciiNumber(ch);
    }

    /**
     * 是否空白符<br>
     * 空白符包括空格、制表符、全角空格和不间断空格<br>
     * @param c 字符
     * @return 是否空白符
     * @see Character#isWhitespace(int)
     * @see Character#isSpaceChar(int)
     */
    public static boolean isBlankChar(final char c) {
        return Character.isWhitespace(c) //
                || Character.isSpaceChar(c)//
                || c == '\u0000'// Null
                || c == '\ufeff' //
                || c == '\u202a' //
                || c == '\u3164'// 朝鲜文
                || c == '\u2800'// 盲文图案空白
                || c == '\u180e';// 蒙古文元音分隔符
    }

    /**
     * 判断是否为EMOJI表情符<br>
     * @param ch 被检查的字符
     * @return 是否为EMOJI
     */
    public static boolean isEmoji(final char ch) {
        return !((ch == 0x0) || //
                (ch == 0x9) || //
                (ch == 0xA) || //
                (ch == 0xD) || //
                ((ch >= 0x20) && (ch <= 0xD7FF)) || //
                ((ch >= 0xE000) && (ch <= 0xFFFD)) || //
                ((ch >= 0x100000) && (ch <= 0x10FFFF)));
    }

    /**
     * 是否为Windows或者Linux（Unix）文件分隔符<br>
     * Windows平台下分隔符为\，Linux（Unix）为/
     * @param ch 被检查的字符
     * @return 是否为Windows或者Linux（Unix）文件分隔符
     */
    public static boolean isFileSeparator(final char ch) {
        return CharConstant.SLASH == ch || CharConstant.BACKSLASH == ch;
    }

    /**
     * 给定类名是否为字符类，字符类包括： Character.class 和 char.class
     * @param clazz 被检查的类
     * @return true表示为字符类
     */
    public static boolean isCharClass(final Class<?> clazz) {
        return clazz == Character.class || clazz == char.class;
    }

    /**
     * 比较两个字符是否相同
     * @param c1 字符1
     * @param c2 字符2
     * @param ignoreCase 是否忽略大小写
     * @return 是否相同
     */
    public static boolean equals(final char c1, final char c2, final boolean ignoreCase) {
        return ignoreCase ? Character.toLowerCase(c1) == Character.toLowerCase(c2) : c1 == c2;
    }
}
