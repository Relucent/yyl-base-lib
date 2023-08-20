package com.github.relucent.base.common.lang;

import com.github.relucent.base.common.constant.ArrayConstant;

/**
 * 字符序列{@link CharSequence}工具类<br>
 * @author YYL
 * @see StringUtil
 */
public class CharSequenceUtil {

    /** 表示未找到的索引 */
    private static final int NOT_FOUND = -1;

    /** 转换长度限制 */
    private static final int TO_STRING_LIMIT = 16;

    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected CharSequenceUtil() {
    }

    // -----------------------------------------------------------------------
    /**
     * 返回一个新的{@code CharSequence}，它是该序列的子序列，从指定索引处的{@code char}值开始。
     * @param cs 指定的序列
     * @param start 起始索引
     * @return 新的序列
     */
    public static CharSequence subSequence(final CharSequence cs, final int start) {
        return cs == null ? null : cs.subSequence(start, cs.length());
    }

    /**
     * 字符串是否为空
     * @param cs 被检测的字符串
     * @return 是否为空
     */
    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    // -----------------------------------------------------------------------
    /**
     * 返回字符在指定文本中第一次出现时的索引，从指定索引开始搜索
     * @param text 指定文本
     * @param ch 查找的字符
     * @param start 开始检索位置
     * @return 指定字符第一次出现时的索引, 如果未找到则返回 -1
     */
    static int indexOf(final CharSequence text, final int ch, int start) {
        if (text instanceof String) {
            return ((String) text).indexOf(ch, start);
        }
        final int sz = text.length();
        if (start < 0) {
            start = 0;
        }
        if (ch < Character.MIN_SUPPLEMENTARY_CODE_POINT) {
            for (int i = start; i < sz; i++) {
                if (text.charAt(i) == ch) {
                    return i;
                }
            }
        }
        // supplementary characters (LANG1300)
        if (ch <= Character.MAX_CODE_POINT) {
            final char[] chars = Character.toChars(ch);
            for (int i = start; i < sz - 1; i++) {
                final char high = text.charAt(i);
                final char low = text.charAt(i + 1);
                if (high == chars[0] && low == chars[1]) {
                    return i;
                }
            }
        }
        return NOT_FOUND;
    }

    /**
     * 返回字符串在指定文本中第一次出现时的索引，从指定索引开始搜索
     * @param text 指定文本
     * @param search 查找的字符序列
     * @param start 开始检索位置
     * @return 子字符序列第一次出现时的索引, 如果未找到则返回 -1
     */
    static int indexOf(final CharSequence text, final CharSequence search, final int start) {
        if (text instanceof String) {
            return ((String) text).indexOf(search.toString(), start);
        } else if (text instanceof StringBuilder) {
            return ((StringBuilder) text).indexOf(search.toString(), start);
        } else if (text instanceof StringBuffer) {
            return ((StringBuffer) text).indexOf(search.toString(), start);
        }
        return text.toString().indexOf(search.toString(), start);
    }

    /**
     * 返回指定字符最后一次出现时的索引，从指定索引开始向后搜索。
     * @param cs 要处理的字符序列
     * @param search 查找的字符
     * @param start 开始检索的索引
     * @return 指定字符最后一次出现时的索引, 如果没找到则返回 -1
     */
    static int lastIndexOf(final CharSequence cs, final int search, int start) {
        if (cs instanceof String) {
            return ((String) cs).lastIndexOf(search, start);
        }
        final int sz = cs.length();
        if (start < 0) {
            return NOT_FOUND;
        }
        if (start >= sz) {
            start = sz - 1;
        }
        if (search < Character.MIN_SUPPLEMENTARY_CODE_POINT) {
            for (int i = start; i >= 0; --i) {
                if (cs.charAt(i) == search) {
                    return i;
                }
            }
        }
        // supplementary characters (LANG1300)
        // NOTE - we must do a forward traversal for this to avoid duplicating code points
        if (search <= Character.MAX_CODE_POINT) {
            final char[] chars = Character.toChars(search);
            // make sure it's not the last index
            if (start == sz - 1) {
                return NOT_FOUND;
            }
            for (int i = start; i >= 0; i--) {
                final char high = cs.charAt(i);
                final char low = cs.charAt(i + 1);
                if (chars[0] == high && chars[1] == low) {
                    return i;
                }
            }
        }
        return NOT_FOUND;
    }

    /**
     * 返回指定字符序列最后一次出现时的索引，从指定索引开始向后搜索。 Used by the lastIndexOf(CharSequence methods) as a green implementation of lastIndexOf
     * @param cs 要处理的字符序列
     * @param search 要检索的字符序列
     * @param start 开始检索的索引
     * @return 指定字符最后一次出现时的索引, 如果没找到则返回 -1
     */
    static int lastIndexOf(final CharSequence cs, final CharSequence search, int start) {
        if (search instanceof String) {
            if (cs instanceof String) {
                return ((String) cs).lastIndexOf((String) search, start);
            } else if (cs instanceof StringBuilder) {
                return ((StringBuilder) cs).lastIndexOf((String) search, start);
            } else if (cs instanceof StringBuffer) {
                return ((StringBuffer) cs).lastIndexOf((String) search, start);
            }
        }

        final int len1 = cs.length();
        final int len2 = search.length();

        if (start > len1) {
            start = len1;
        }

        if (start < 0 || len2 < 0 || len2 > len1) {
            return -1;
        }

        if (len2 == 0) {
            return start;
        }

        if (len2 <= TO_STRING_LIMIT) {
            if (cs instanceof String) {
                return ((String) cs).lastIndexOf(search.toString(), start);
            } else if (cs instanceof StringBuilder) {
                return ((StringBuilder) cs).lastIndexOf(search.toString(), start);
            } else if (cs instanceof StringBuffer) {
                return ((StringBuffer) cs).lastIndexOf(search.toString(), start);
            }
        }

        if (start + len2 > len1) {
            start = len1 - len2;
        }

        final char char0 = search.charAt(0);

        int i = start;
        while (true) {
            while (cs.charAt(i) != char0) {
                i--;
                if (i < 0) {
                    return -1;
                }
            }
            if (checkLaterThan(cs, search, len2, i)) {
                return i;
            }
            i--;
            if (i < 0) {
                return -1;
            }
        }
    }

    /**
     * 是否匹配
     * @param cs 要处理的字符序列
     * @param search 要检索的字符序列
     * @param len2 匹配长度
     * @param start1 匹配位置
     * @return 是否匹配
     */
    private static boolean checkLaterThan(final CharSequence cs, final CharSequence search, final int len2, final int start1) {
        for (int i = 1, j = len2 - 1; i <= j; i++, j--) {
            if (cs.charAt(start1 + i) != search.charAt(i) || cs.charAt(start1 + j) != search.charAt(j)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 将给定的CharSequence转换为char[]。
     * @param source 要处理的{@code CharSequence}
     * @return 结果字符数组，从不为null.
     */
    public static char[] toCharArray(final CharSequence source) {
        final int len = StringUtil.length(source);
        if (len == 0) {
            return ArrayConstant.EMPTY_CHAR_ARRAY;
        }
        if (source instanceof String) {
            return ((String) source).toCharArray();
        }
        final char[] array = new char[len];
        for (int i = 0; i < len; i++) {
            array[i] = source.charAt(i);
        }
        return array;
    }

    /**
     * 区域匹配
     * @param cs 要处理的字符序列 {@code CharSequence}
     * @param ignoreCase 是否不区分大小写
     * @param thisStart 字符序列{@code cs}开始的索引位置
     * @param substring 要查找的字符序列{@code CharSequence}
     * @param start 字符序列{@code substring}开始的索引
     * @param length 区域的字符长度
     * @return 区域是否匹配
     */
    static boolean regionMatches(final CharSequence cs, final boolean ignoreCase, final int thisStart, final CharSequence substring, final int start,
            final int length) {
        if (cs instanceof String && substring instanceof String) {
            return ((String) cs).regionMatches(ignoreCase, thisStart, (String) substring, start, length);
        }
        int index1 = thisStart;
        int index2 = start;
        int tmpLen = length;

        // Extract these first so we detect NPEs the same as the java.lang.String version
        final int srcLen = cs.length() - thisStart;
        final int otherLen = substring.length() - start;

        // Check for invalid parameters
        if (thisStart < 0 || start < 0 || length < 0) {
            return false;
        }

        // Check that the regions are long enough
        if (srcLen < length || otherLen < length) {
            return false;
        }

        while (tmpLen-- > 0) {
            final char c1 = cs.charAt(index1++);
            final char c2 = substring.charAt(index2++);

            if (c1 == c2) {
                continue;
            }

            if (!ignoreCase) {
                return false;
            }

            // The real same check as in String.regionMatches():
            final char u1 = Character.toUpperCase(c1);
            final char u2 = Character.toUpperCase(c2);
            if (u1 != u2 && Character.toLowerCase(u1) != Character.toLowerCase(u2)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 是否以指定字符串开头，忽略大小写
     * @param str 被监测字符串
     * @param prefix 开头字符串
     * @return 是否以指定字符串开头
     */
    public static boolean startWithIgnoreCase(CharSequence str, CharSequence prefix) {
        return startWith(str, prefix, true);
    }

    /**
     * 字符串是否以给定字符开始
     * @param cs 字符串
     * @param c 字符
     * @return 是否开始
     */
    public static boolean startWith(CharSequence cs, char c) {
        return !isEmpty(cs) && c == cs.charAt(0);
    }

    /**
     * 是否以指定字符串开头<br>
     * 如果给定的字符串和开头字符串都为null则返回true，否则任意一个值为null返回false
     * @param str 被监测字符串
     * @param prefix 开头字符串
     * @param ignoreCase 是否忽略大小写
     * @return 是否以指定字符串开头
     */
    public static boolean startWith(CharSequence str, CharSequence prefix, boolean ignoreCase) {
        return startWith(str, prefix, ignoreCase, false);
    }

    /**
     * 是否以指定字符串开头<br>
     * 如果给定的字符串和开头字符串都为null则返回true，否则任意一个值为null返回false<br>
     * 
     * <pre>
     *     CharSequenceUtil.startWith("123", "123", false, true);   -- false
     *     CharSequenceUtil.startWith("ABCDEF", "abc", true, true); -- true
     *     CharSequenceUtil.startWith("abc", "abc", true, true);    -- false
     * </pre>
     *
     * @param string 被监测字符串
     * @param prefix 开头字符串
     * @param ignoreCase 是否忽略大小写
     * @param ignoreEquals 是否忽略字符串相等的情况
     * @return 是否以指定字符串开头
     */
    public static boolean startWith(CharSequence string, CharSequence prefix, boolean ignoreCase, boolean ignoreEquals) {
        if (string == null || prefix == null) {
            if (ignoreEquals) {
                return false;
            }
            return string == null && prefix == null;
        }

        boolean isStartWith = string.toString().regionMatches(ignoreCase, 0, prefix.toString(), 0, prefix.length());

        if (isStartWith) {
            return !ignoreEquals || !equals(string, prefix, ignoreCase);
        }
        return false;
    }

    /**
     * 比较两个字符串是否相等，规则如下
     * <ul>
     * <li>str1和str2都为{@code null}</li>
     * <li>忽略大小写使用{@link String#equalsIgnoreCase(String)}判断相等</li>
     * <li>不忽略大小写使用{@link String#contentEquals(CharSequence)}判断相等</li>
     * </ul>
     * @param str1 要比较的字符串1
     * @param str2 要比较的字符串2
     * @param ignoreCase 是否忽略大小写
     * @return 如果两个字符串相同，或者都是{@code null}，则返回{@code true}
     */
    public static boolean equals(CharSequence str1, CharSequence str2, boolean ignoreCase) {
        if (str1 == null) {
            return str2 == null;
        }
        if (str2 == null) {
            return false;
        }
        if (ignoreCase) {
            return str1.toString().equalsIgnoreCase(str2.toString());
        } else {
            return str1.toString().contentEquals(str2);
        }
    }
}
