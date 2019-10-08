package com.github.relucent.base.util.lang;

import java.util.ArrayList;
import java.util.List;

import com.github.relucent.base.util.constants.ArrayConstants;
import com.github.relucent.base.util.constants.CharConstants;
import com.github.relucent.base.util.constants.StringConstants;

/**
 * 字符串工具类
 */
public class StringUtil {

    /**
     * 字符串是否为空
     * @param cs 被检测的字符串
     * @return 是否为空
     */
    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * 字符串是否为非白<br>
     * @param cs 被检测的字符串
     * @return 是否为非空
     */
    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }

    /**
     * 获取字符串的长度，如果为null返回0
     * @param cs 字符串
     * @return 字符串的长度，如果为null返回0
     */
    public static int length(CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }

    /**
     * 返回传入的字符串，或者如果字符串为{@code null}，则返回空字符串("").
     * 
     * <pre>
     * StringUtil.defaultString(null)  = ""
     * StringUtil.defaultString("")    = ""
     * StringUtil.defaultString("bat") = "bat"
     * </pre>
     * 
     * @param string 要检查的字符串
     * @return 传入的字符串，如果是{@code null}，则为返回空字符串("").
     */
    public static String defaultString(final String string) {
        return defaultString(string, StringConstants.EMPTY);
    }

    /**
     * 返回传入的字符串，或者如果字符串为{@code null}，则返回默认字符串{@code defaultString}.
     * 
     * <pre>
     * StringUtils.defaultString(null, "NULL")  = "NULL"
     * StringUtils.defaultString("", "NULL")    = ""
     * StringUtils.defaultString("bat", "NULL") = "bat"
     * </pre>
     * 
     * @param string 要检查的字符串
     * @param defaultString 默认字符串
     * @return 传入的字符串，如果是{@code null}，则返回默认字符串{@code defaultString}.
     */
    public static String defaultString(final String string, final String defaultString) {
        return string == null ? defaultString : string;
    }

    /**
     * 删除字符串两端的空格
     * @param cs 字符串
     * @return 修剪后的字符串
     */
    public static String trim(final CharSequence cs) {
        return cs == null ? null : cs.toString().trim();
    }

    /**
     * 删除字符串两端的空格，如果输入字符串为{@code null}，那么返回空字符串 ("")
     * @param cs 字符串
     * @return 修剪后的字符串
     */
    public static String trimToEmpty(final CharSequence cs) {
        return cs == null ? StringConstants.EMPTY : cs.toString().trim();
    }

    /**
     * 从字符串中删除所有空白
     * @param cs 要处理的字符串
     * @return 没有空格的字符串，如果输入null，则返回{@code null}
     */
    public static String deleteWhitespace(final String cs) {
        if (isEmpty(cs)) {
            return cs;
        }
        final StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < cs.length(); i++) {
            char c = cs.charAt(i);
            if (Character.isWhitespace(c)) {
                continue;
            }
            if (CharConstants.UTF_8_BOM == c) {
                continue;
            }
            buffer.append((char) c);
        }
        return buffer.toString();
    }


    /**
     * 切分字符串 *
     * 
     * <pre>
     * split(null, *)         = null
     * split("", *)           = []
     * split("a b c", null)   = ["a", "b", "c"]
     * split("a.b.c", '.')    = ["a", "b", "c"]
     * split("a..b.c.", '.')  = ["a", "b", "c"]
     * </pre>
     * 
     * @param cs 要处理的字符串
     * @param separator 分隔符字符
     * @return 切分后的字符串数组
     */
    public static String[] split(final CharSequence cs, final String separator) {
        return splitWorker(cs, separator, -1, false);
    }

    /**
     * 切分字符串
     * 
     * <pre>
     * split(null, *, *)            = null
     * split("", *, *)              = []
     * split("ab cd ef", null, 0)   = ["ab", "cd", "ef"]
     * split("ab   cd ef", null, 0) = ["ab", "cd", "ef"]
     * split("ab:cd:ef", ":", 0)    = ["ab", "cd", "ef"]
     * split("ab:cd:ef", ":", 2)    = ["ab", "cd:ef"]
     * split("ab   cd ef", null, 2) = ["ab", "cd ef"]
     * split("ab   cd ef", null, 3) = ["ab", "cd", "ef"]
     * split("ab   cd ef", null, 4) = ["ab", "cd", "ef"]
     * </pre>
     * 
     * @param cs 要处理的字符串
     * @param separator 分隔符字符
     * @param max 返回数组的最大元素数。-1代表没有限制
     * @return 切分后的字符串数组
     */
    public static String[] split(final CharSequence cs, final String separator, final int max) {
        return splitWorker(cs, separator, max, false);
    }

    /**
     * 切分字符串，如果分隔符字符串为null，那么使用空白作为拆分字符
     * 
     * <pre>
     * splitPreserveAllTokens(null, *)         = null
     * splitPreserveAllTokens("", *)           = []
     * splitPreserveAllTokens("a b c", null)   = ["a", "b", "c"]
     * splitPreserveAllTokens("a.b.c", '.')    = ["a", "b", "c"]
     * splitPreserveAllTokens("a..b.c.", '.')  = ["a", "", "b", "c", ""]
     * </pre>
     * 
     * @param cs 要处理的字符串
     * @param separator 分隔符字符
     * @return 切分后的字符串数组
     */
    public static String[] splitPreserveAllTokens(final CharSequence cs, final String separator) {
        return splitWorker(cs, separator, -1, true);
    }

    /**
     * 切分字符串，如果分隔符字符串为null，那么使用空白作为拆分字符 Null separator means use whitespace
     * 
     * <pre>
     * splitPreserveAllTokens(null, *, *)            = null
     * splitPreserveAllTokens("", *, *)              = []
     * splitPreserveAllTokens("ab cd ef", null, 0)   = ["ab", "cd", "ef"]
     * splitPreserveAllTokens("ab   cd ef", null, 0) = ["ab", "", "cd", "ef"]
     * splitPreserveAllTokens("ab:cd:ef", ":", 0)    = ["ab", "cd", "ef"]
     * splitPreserveAllTokens("ab:cd:ef", ":", 2)    = ["ab", "cd:ef"]
     * splitPreserveAllTokens("ab   cd ef", null, 2) = ["ab", "  cd ef"]
     * splitPreserveAllTokens("ab   cd ef", null, 3) = ["ab", "", "cd ef"]
     * splitPreserveAllTokens("ab   cd ef", null, 4) = ["ab", "", "cd", "ef"]
     * </pre>
     * 
     * @param cs 要处理的字符串
     * @param separator 分隔符字符
     * @param max 返回数组的最大元素数。-1代表没有限制
     * @return 切分后的字符串数组
     */
    public static String[] splitPreserveAllTokens(final CharSequence cs, final String separator, final int max) {
        return splitWorker(cs, separator, max, true);
    }

    /**
     * 切分字符串，排除掉空的字符串，如果输入字符串为null，那么也会返回一个长度为0的字符串数组
     * 
     * <pre>
     * splitPurify(null, *)               = []
     * splitPurify("", *)                 = []
     * splitPurify("abc", null)           = ["abc"]
     * splitPurify("ab cd ef", null)      = ["ab", "cd", "ef"]
     * splitPurify("ab:cd:ef", ":")       = ["ab", "cd", "ef"]
     * splitPurify("ab:::cd::ef", ":")    = ["ab", "cd", "ef"]
     * splitPurify(":::ab:cd:ef", ":")    = ["ab", "cd", "ef"]
     * </pre>
     * 
     * @param cs 要处理的字符串
     * @param separator 分隔符字符
     * @return 切分后的字符串数组
     */
    public static String[] splitPurify(final CharSequence cs, final String separator) {
        String[] array = split(cs, separator);
        if (array == null) {
            return ArrayConstants.EMPTY_STRING_ARRAY;
        }
        List<String> result = new ArrayList<>();
        for (String value : array) {
            if (isNotEmpty(value)) {
                result.add(value);
            }
        }
        return result.toArray(ArrayConstants.EMPTY_STRING_ARRAY);
    }

    /**
     * 切分字符串
     * @param cs 要处理的字符串
     * @param separator 分隔符字符串
     * @param max 得到数组的长度，-1 代表没有限制
     * @param preserveAllTokens 保留所有令牌，相邻的分隔符是作为空标记分隔符处理；如果{@code false}，则相邻分离器被视为一个分离器。
     * @return 切分后的字符串数组
     */
    private static String[] splitWorker(final CharSequence cs, final String separator, final int max,
            final boolean preserveAllTokens) {
        if (cs == null) {
            return null;
        }
        final int len = cs.length();
        if (len == 0) {
            return ArrayConstants.EMPTY_STRING_ARRAY;
        }
        final List<String> list = new ArrayList<>();
        int sizePlus1 = 1;
        int i = 0, start = 0;
        boolean match = false;
        boolean lastMatch = false;
        if (separator == null) {
            // Null separator means use whitespace
            while (i < len) {
                if (Character.isWhitespace(cs.charAt(i))) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(cs.subSequence(start, i).toString());
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        } else if (separator.length() == 1) {
            // Optimise 1 character case
            final char sep = separator.charAt(0);
            while (i < len) {
                if (cs.charAt(i) == sep) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(cs.subSequence(start, i).toString());
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        } else {
            // standard case
            while (i < len) {
                if (separator.indexOf(cs.charAt(i)) >= 0) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(cs.subSequence(start, i).toString());
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        }
        if (match || preserveAllTokens && lastMatch) {
            list.add(cs.subSequence(start, i).toString());
        }
        return list.toArray(new String[list.size()]);
    }
}
