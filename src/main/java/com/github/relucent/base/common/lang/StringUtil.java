package com.github.relucent.base.common.lang;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.github.relucent.base.common.constant.ArrayConstant;
import com.github.relucent.base.common.constant.CharConstant;
import com.github.relucent.base.common.constant.StringConstant;

/**
 * 字符串工具类
 */
public class StringUtil {

    /** 字符串构建器默认构建尺寸 */
    private static final int STRING_BUILDER_SIZE = 256;

    /** 表示未找到的索引 */
    private static final int INDEX_NOT_FOUND = -1;

    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected StringUtil() {
    }

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
     * 检查字符序列是否为空或者空白，空白由{@link Character#isWhitespace(char)}定义。
     * 
     * <pre>
     * StringUtils.isBlank(null)        = true
     * StringUtils.isBlank("")          = true
     * StringUtils.isBlank(" ")         = true
     * StringUtils.isBlank("hello")     = false
     * StringUtils.isBlank("h e l l o") = false
     * StringUtils.isBlank("  hello  ") = false
     * </pre>
     * 
     * @param cs 要检查的字符序列
     * @return 如果字符序列为 null或者空白，则返回 {@code true}
     */
    public static boolean isBlank(final CharSequence cs) {
        final int length = length(cs);
        if (length == 0) {
            return true;
        }
        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查字符序列是否只包含数字字符
     * @param cs 检查的字符序列
     * @return 如果字符串只包含数字字符，则返回{@code true} //
     */
    public static boolean isDigits(final String cs) {
        if (isEmpty(cs)) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(cs.charAt(i))) {
                return false;
            }
        }
        return true;
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
        return defaultString(string, StringConstant.EMPTY);
    }

    /**
     * 返回传入的字符串，或者如果字符串为{@code null}，则返回默认字符串{@code defaultString}.
     * 
     * <pre>
     * StringUtil.defaultString(null, "NULL")  = "NULL"
     * StringUtil.defaultString("", "NULL")    = ""
     * StringUtil.defaultString("bat", "NULL") = "bat"
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
        return cs == null ? StringConstant.EMPTY : cs.toString().trim();
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
            if (CharConstant.UTF_8_BOM == c) {
                continue;
            }
            buffer.append((char) c);
        }
        return buffer.toString();
    }

    /**
     * 将所提供的集合{@code Iterator}元素连接成字符串。
     * @param iterable 要连接在一起的集合
     * @param separator 分隔符
     * @return 连接到一起的字符串，如多集合为{@code null} 返回 null
     */
    public static String join(final Iterable<?> iterable, final String separator) {
        if (iterable == null) {
            return null;
        }
        return join(iterable.iterator(), separator);
    }

    /**
     * 将所提供的集合{@code Iterator}元素连接成字符串。
     * @param iterator 要连接在一起的集合
     * @param separator 分隔符
     * @return 连接到一起的字符串，如多集合为{@code null} 返回 null
     */
    public static String join(final Iterator<?> iterator, final String separator) {
        if (iterator == null) {
            return null;
        }
        if (!iterator.hasNext()) {
            return StringConstant.EMPTY;
        }
        final Object first = iterator.next();
        if (!iterator.hasNext()) {
            return Objects.toString(first, "");
        }
        final StringBuilder buffer = new StringBuilder(STRING_BUILDER_SIZE);
        if (first != null) {
            buffer.append(first);
        }
        while (iterator.hasNext()) {
            if (separator != null) {
                buffer.append(separator);
            }
            final Object obj = iterator.next();
            if (obj != null) {
                buffer.append(obj);
            }
        }
        return buffer.toString();
    }

    /**
     * 将所提供的对象数组拼装成一个字符串，忽略其中的空字符串。
     * @param values 需要成字符串的对象数组
     * @param separator 分隔符
     * @return 连接到一起的字符串，如果数组为{@code null} 返回 null
     */
    public static String joinPurify(final Object[] values, String separator) {
        if (values == null) {
            return null;
        }
        if (separator == null) {
            separator = StringConstant.EMPTY;
        }
        int count = 0;
        final StringBuilder buffer = new StringBuilder(STRING_BUILDER_SIZE);
        for (Object value : values) {
            if (value != null) {
                String string = trim(value.toString());
                if (isNotEmpty(string)) {
                    if (count > 0 && separator != null) {
                        buffer.append(separator);
                    }
                    buffer.append(string);
                    count++;
                }
            }
        }
        return buffer.toString();
    }

    /**
     * 将所提供的集合{@code Iterator}元素连接成字符串，忽略其中的空字符串。
     * @param iterable 要连接在一起的集合
     * @param separator 分隔符
     * @return 连接到一起的字符串，如果集合为{@code null} 返回 null
     */
    public static String joinPurify(final Iterable<?> iterable, final String separator) {
        if (iterable == null) {
            return null;
        }
        return joinPurify(iterable.iterator(), separator);
    }

    /**
     * 将所提供的集合{@code Iterator}元素连接成字符串，忽略其中的空字符串。
     * @param iterator 要连接在一起的集合
     * @param separator 分隔符
     * @return 连接到一起的字符串，如果集合为{@code null} 返回 null
     */
    public static String joinPurify(final Iterator<?> iterator, String separator) {
        if (iterator == null) {
            return null;
        }
        if (!iterator.hasNext()) {
            return StringConstant.EMPTY;
        }
        if (separator == null) {
            separator = StringConstant.EMPTY;
        }
        int count = 0;
        final StringBuilder buffer = new StringBuilder(STRING_BUILDER_SIZE);
        while (iterator.hasNext()) {
            final Object obj = iterator.next();
            if (obj != null) {
                String string = trim(obj.toString());
                if (isNotEmpty(string)) {
                    if (count > 0 && separator != null) {
                        buffer.append(separator);
                    }
                    buffer.append(string);
                    count++;
                }
            }
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
            return ArrayConstant.EMPTY_STRING_ARRAY;
        }
        List<String> result = new ArrayList<>();
        for (String value : array) {
            if (isNotEmpty(value)) {
                result.add(value);
            }
        }
        return result.toArray(ArrayConstant.EMPTY_STRING_ARRAY);
    }

    /**
     * 左填充指定字符的字符串.
     * 
     * <pre>
     * StringUtil.leftPad(null, *, *)     = null
     * StringUtil.leftPad("", 3, 'z')     = "zzz"
     * StringUtil.leftPad("bat", 3, 'z')  = "bat"
     * StringUtil.leftPad("bat", 5, 'z')  = "zzbat"
     * StringUtil.leftPad("bat", 1, 'z')  = "bat"
     * StringUtil.leftPad("bat", -1, 'z') = "bat"
     * </pre>
     *
     * @param cs 需要填充的字符串
     * @param size 要填充到的大小
     * @param padChar 要填充的字符
     * @return 左填充字符串或原始字符串（如果不需要填充）
     */
    public static String leftPad(final CharSequence cs, final int size, final char padChar) {
        if (cs == null) {
            return null;
        }
        final int pads = size - cs.length();
        if (pads <= 0) {
            return cs.toString();
        }
        StringBuilder builder = new StringBuilder(size);
        for (int i = 0; i < pads; i++) {
            builder.append(padChar);
        }
        builder.append(cs);
        return builder.toString();
    }

    /**
     * <p>
     * 用指定字符填充字符串
     * </p>
     * <p>
     * The String is padded to the size of {@code size}.
     * </p>
     *
     * <pre>
     * StringUtil.rightPad(null, *, *)     = null
     * StringUtil.rightPad("", 3, 'z')     = "zzz"
     * StringUtil.rightPad("bat", 3, 'z')  = "bat"
     * StringUtil.rightPad("bat", 5, 'z')  = "batzz"
     * StringUtil.rightPad("bat", 1, 'z')  = "bat"
     * StringUtil.rightPad("bat", -1, 'z') = "bat"
     * </pre>
     * 
     * @param cs 需要填充的字符串
     * @param size 要填充到的大小
     * @param padChar 要填充的字符
     * @return 右填充字符串或原始字符串（如果不需要填充）
     */
    public static String rightPad(final CharSequence cs, final int size, final char padChar) {
        if (cs == null) {
            return null;
        }
        final int pads = size - cs.length();
        if (pads <= 0) {
            return cs.toString();
        }
        StringBuilder builder = new StringBuilder(size);
        builder.append(cs);
        for (int i = 0; i < pads; i++) {
            builder.append(padChar);
        }
        return builder.toString();
    }

    /**
     * 切分字符串
     * @param cs 要处理的字符串
     * @param separator 分隔符字符串
     * @param max 得到数组的长度，-1 代表没有限制
     * @param preserveAllTokens 保留所有令牌，相邻的分隔符是作为空标记分隔符处理；如果{@code false}，则相邻分离器被视为一个分离器。
     * @return 切分后的字符串数组
     */
    private static String[] splitWorker(final CharSequence cs, final String separator, final int max, final boolean preserveAllTokens) {
        if (cs == null) {
            return null;
        }
        final int len = cs.length();
        if (len == 0) {
            return ArrayConstant.EMPTY_STRING_ARRAY;
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

    /**
     * 查找字符串在文本中第一次出现的位置
     * 
     * <pre>
     * StringUtil.indexOf(null, *)          = -1
     * StringUtil.indexOf(*, null)          = -1
     * StringUtil.indexOf("", "")           = 0
     * StringUtil.indexOf("", *)            = -1
     * StringUtil.indexOf("aabaabaa", "a")  = 0
     * StringUtil.indexOf("aabaabaa", "b")  = 2
     * StringUtil.indexOf("aabaabaa", "ab") = 1
     * </pre>
     * 
     * @param text 用于查找的文本
     * @param search 查找的字符串
     * @return 字符串第一次出现的位置，如果未找到返回 -1
     */
    public static int indexOf(final CharSequence text, final CharSequence search) {
        return CharSequenceUtil.indexOf(text, search, 0);
    }

    /**
     * 查找字符串在文本中第一次出现的位置
     * 
     * <pre>
     * StringUtil.indexOf(null, *, *)          = -1
     * StringUtil.indexOf(*, null, *)          = -1
     * StringUtil.indexOf("", "", 0)           = 0
     * StringUtil.indexOf("", *, 0)            = -1
     * StringUtil.indexOf("aabaabaa", "a", 0)  = 0
     * StringUtil.indexOf("aabaabaa", "b", 0)  = 2
     * StringUtil.indexOf("aabaabaa", "ab", 0) = 1
     * StringUtil.indexOf("aabaabaa", "b", 3)  = 5
     * StringUtil.indexOf("aabaabaa", "b", 9)  = -1
     * StringUtil.indexOf("aabaabaa", "b", -1) = 2
     * StringUtil.indexOf("aabaabaa", "", 2)   = 2
     * StringUtil.indexOf("abc", "", 9)        = 3
     * </pre>
     * 
     * @param text 用于查找的文本
     * @param search 查找的字符串
     * @param startPos 开始查找的位置
     * @return 字符串第一次出现的位置，如果未找到返回 -1
     */
    public static int indexOf(final CharSequence text, final CharSequence search, final int startPos) {
        if (text == null || search == null) {
            return INDEX_NOT_FOUND;
        }
        return CharSequenceUtil.indexOf(text, search, startPos);
    }

    /**
     * 判断文本中是否包含指定字符串
     * 
     * <pre>
     * StringUtil.contains(null, *)     = false
     * StringUtil.contains(*, null)     = false
     * StringUtil.contains("", "")      = true
     * StringUtil.contains("abc", "")   = true
     * StringUtil.contains("abc", "a")  = true
     * StringUtil.contains("abc", "z")  = false
     * </pre>
     *
     * @param text 要检查的文本
     * @param search 要查找的字符串
     * @return 如果文本中包含要查找的字符串则返回 true，否则返回false
     */
    public static boolean contains(final CharSequence text, final CharSequence search) {
        return indexOf(text, search) >= 0;
    }

    /**
     * 查找字符串在文本中第一次出现的位置（忽略大小写）
     * 
     * <pre>
     * StringUtil.indexOfIgnoreCase(null, *, *)          = -1
     * StringUtil.indexOfIgnoreCase(*, null, *)          = -1
     * StringUtil.indexOfIgnoreCase("", "", 0)           = 0
     * StringUtil.indexOfIgnoreCase("aabaabaa", "A", 0)  = 0
     * StringUtil.indexOfIgnoreCase("aabaabaa", "B", 0)  = 2
     * StringUtil.indexOfIgnoreCase("aabaabaa", "AB", 0) = 1
     * StringUtil.indexOfIgnoreCase("aabaabaa", "B", 3)  = 5
     * StringUtil.indexOfIgnoreCase("aabaabaa", "B", 9)  = -1
     * StringUtil.indexOfIgnoreCase("aabaabaa", "B", -1) = 2
     * StringUtil.indexOfIgnoreCase("aabaabaa", "", 2)   = 2
     * StringUtil.indexOfIgnoreCase("abc", "", 9)        = -1
     * </pre>
     *
     * @param text 用于查找的文本
     * @param search 查找的字符串
     * @param startPos 开始查找的位置
     * @return 字符串第一次出现的位置，如果未找到返回 -1
     */
    public static int indexOfIgnoreCase(final CharSequence text, final CharSequence search, int startPos) {
        if (text == null || search == null) {
            return INDEX_NOT_FOUND;
        }
        if (startPos < 0) {
            startPos = 0;
        }
        final int endLimit = text.length() - search.length() + 1;
        if (startPos > endLimit) {
            return INDEX_NOT_FOUND;
        }
        if (search.length() == 0) {
            return startPos;
        }
        for (int i = startPos; i < endLimit; i++) {
            if (CharSequenceUtil.regionMatches(text, true, i, search, 0, search.length())) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 替换字符串
     * 
     * <pre>
     * StringUtil.replace(null, *, *)        = null
     * StringUtil.replace("", *, *)          = ""
     * StringUtil.replace("any", null, *)    = "any"
     * StringUtil.replace("any", *, null)    = "any"
     * StringUtil.replace("any", "", *)      = "any"
     * StringUtil.replace("aba", "a", null)  = "aba"
     * StringUtil.replace("aba", "a", "")    = "b"
     * StringUtil.replace("aba", "a", "z")   = "zbz"
     * </pre>
     *
     * @param text 用于查找替换的文本
     * @param searchString 查找的字符串
     * @param replacement 替换的字符串
     * @return 处理后的文本
     */
    public static String replace(final String text, final String searchString, final String replacement) {
        return replace(text, searchString, replacement, -1);
    }

    /**
     * 替换字符串
     * 
     * <pre>
     * StringUtil.replace(null, *, *, *)         = null
     * StringUtil.replace("", *, *, *)           = ""
     * StringUtil.replace("any", null, *, *)     = "any"
     * StringUtil.replace("any", *, null, *)     = "any"
     * StringUtil.replace("any", "", *, *)       = "any"
     * StringUtil.replace("any", *, *, 0)        = "any"
     * StringUtil.replace("abaa", "a", null, -1) = "abaa"
     * StringUtil.replace("abaa", "a", "", -1)   = "b"
     * StringUtil.replace("abaa", "a", "z", 0)   = "abaa"
     * StringUtil.replace("abaa", "a", "z", 1)   = "zbaa"
     * StringUtil.replace("abaa", "a", "z", 2)   = "zbza"
     * StringUtil.replace("abaa", "a", "z", -1)  = "zbzz"
     * </pre>
     *
     * @param text 用于查找替换的文本
     * @param searchString 查找的字符串
     * @param replacement 替换的字符串
     * @param max 需要替换的字符串个数，{@code -1} 表示无限制
     * @return 处理后的文本
     */
    public static String replace(final String text, final String searchString, final String replacement, final int max) {
        return replace(text, searchString, replacement, max, false);
    }

    /**
     * 替换字符串
     * 
     * <pre>
     * StringUtil.replace(null, *, *, *, false)         = null
     * StringUtil.replace("", *, *, *, false)           = ""
     * StringUtil.replace("any", null, *, *, false)     = "any"
     * StringUtil.replace("any", *, null, *, false)     = "any"
     * StringUtil.replace("any", "", *, *, false)       = "any"
     * StringUtil.replace("any", *, *, 0, false)        = "any"
     * StringUtil.replace("abaa", "a", null, -1, false) = "abaa"
     * StringUtil.replace("abaa", "a", "", -1, false)   = "b"
     * StringUtil.replace("abaa", "a", "z", 0, false)   = "abaa"
     * StringUtil.replace("abaa", "A", "z", 1, false)   = "abaa"
     * StringUtil.replace("abaa", "A", "z", 1, true)   = "zbaa"
     * StringUtil.replace("abAa", "a", "z", 2, true)   = "zbza"
     * StringUtil.replace("abAa", "a", "z", -1, true)  = "zbzz"
     * </pre>
     *
     * @param text 要处理的文本
     * @param searchString 要替换的文本
     * @param replacement 要替换为的文本
     * @param max 需要替换的字符串个数，{@code -1} 表示无限制
     * @param ignoreCase 查找时是否忽略大小写
     * @return 替换后的文本
     */
    private static String replace(final String text, String searchString, final String replacement, int max, final boolean ignoreCase) {
        if (isEmpty(text) || isEmpty(searchString) || replacement == null || max == 0) {
            return text;
        }
        if (ignoreCase) {
            searchString = searchString.toLowerCase();
        }
        int start = 0;
        int end = ignoreCase ? indexOfIgnoreCase(text, searchString, start) : indexOf(text, searchString, start);
        if (end == INDEX_NOT_FOUND) {
            return text;
        }
        final int replLength = searchString.length();
        int increase = Math.max(replacement.length() - replLength, 0);
        increase *= max < 0 ? 16 : Math.min(max, 64);
        final StringBuilder buf = new StringBuilder(text.length() + increase);
        while (end != INDEX_NOT_FOUND) {
            buf.append(text, start, end).append(replacement);
            start = end + replLength;
            if (--max == 0) {
                break;
            }
            end = ignoreCase ? indexOfIgnoreCase(text, searchString, start) : indexOf(text, searchString, start);
        }
        buf.append(text, start, text.length());
        return buf.toString();
    }
}
