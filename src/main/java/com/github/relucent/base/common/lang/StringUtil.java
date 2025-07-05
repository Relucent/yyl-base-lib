package com.github.relucent.base.common.lang;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.github.relucent.base.common.constant.ArrayConstant;
import com.github.relucent.base.common.constant.CharConstant;
import com.github.relucent.base.common.constant.CharsetConstant;
import com.github.relucent.base.common.constant.StringConstant;

/**
 * 字符串工具类
 * @author YYL
 */
public class StringUtil {

    // ==============================Fields===========================================
    /** 字符串构建器默认构建尺寸 */
    private static final int STRING_BUILDER_SIZE = 256;

    /** 表示未找到的索引 */
    private static final int INDEX_NOT_FOUND = -1;

    // ==============================Constructors=====================================

    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected StringUtil() {
    }

    // ==============================Methods==========================================
    /**
     * 字符串是否为空
     * @param string 被检测的字符串
     * @return 是否为空
     */
    public static boolean isEmpty(final CharSequence string) {
        return string == null || string.length() == 0;
    }

    /**
     * 字符串是否为非白<br>
     * @param string 被检测的字符串
     * @return 是否为非空
     */
    public static boolean isNotEmpty(final CharSequence string) {
        return !isEmpty(string);
    }

    /**
     * 检查字符序列是否为空或者空白，空白由{@link Character#isWhitespace(char)}定义。
     * 
     * <pre>
     * StringUtil.isBlank(null)        = true
     * StringUtil.isBlank("")          = true
     * StringUtil.isBlank(" ")         = true
     * StringUtil.isBlank("hello")     = false
     * StringUtil.isBlank("h e l l o") = false
     * StringUtil.isBlank("  hello  ") = false
     * </pre>
     * 
     * @param string 要检查的字符序列
     * @return 如果字符序列为 {@code null}或者空白，则返回 {@code true}
     */
    public static boolean isBlank(final CharSequence string) {
        final int length = length(string);
        if (length == 0) {
            return true;
        }
        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(string.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查字符序列是否不是空白，空白由{@link Character#isWhitespace(char)}定义。
     * 
     * <pre>
     * StringUtil.isNotBlank(null)        = false
     * StringUtil.isNotBlank("")          = false
     * StringUtil.isNotBlank(" ")         = false
     * StringUtil.isNotBlank("hello")     = true
     * StringUtil.isNotBlank("h e l l o") = true
     * StringUtil.isNotBlank("  hello  ") = true
     * </pre>
     * 
     * @param str 要检查的字符序列
     * @return 如果字符序列为 null或者空白，则返回 {@code true}
     * @param str the CharSequence to check, may be null
     * @return 如果字符序列不为{@code null}、且不为空白，则返回 {@code true}
     */
    public static boolean isNotBlank(final CharSequence str) {
        return !isBlank(str);
    }

    /**
     * 检查字符序列是否只包含数字字符
     * @param str 检查的字符序列
     * @return 如果字符串只包含数字字符，则返回{@code true} //
     */
    public static boolean isDigits(final String str) {
        if (isEmpty(str)) {
            return false;
        }
        final int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取字符串的长度，如果为null返回0
     * @param str 字符串
     * @return 字符串的长度，如果为null返回0
     */
    public static int length(CharSequence str) {
        return str == null ? 0 : str.length();
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
     * @param str 要检查的字符串
     * @return 传入的字符串，如果是{@code null}，则为返回空字符串("").
     */
    public static String defaultString(final String str) {
        return defaultString(str, StringConstant.EMPTY);
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
     * @param str           要检查的字符串
     * @param defaultString 默认字符串
     * @return 传入的字符串，如果是{@code null}，则返回默认字符串{@code defaultString}.
     */
    public static String defaultString(final String str, final String defaultString) {
        return str == null ? defaultString : str;
    }

    /**
     * 删除字符串两端的空格
     * @param str 字符串
     * @return 修剪后的字符串
     */
    public static String trim(final CharSequence str) {
        return str == null ? null : str.toString().trim();
    }

    /**
     * 删除字符串两端的空格，如果输入字符串为{@code null}，那么返回空字符串 ("")
     * @param str 字符串
     * @return 修剪后的字符串
     */
    public static String trimToEmpty(final CharSequence str) {
        return str == null ? StringConstant.EMPTY : str.toString().trim();
    }

    /**
     * 从指定的字符串中获取子字符串
     * 
     * <pre>
     * StringUtils.substring(null, *)   = null
     * StringUtils.substring("", *)     = ""
     * StringUtils.substring("abc", 0)  = "abc"
     * StringUtils.substring("abc", 2)  = "c"
     * StringUtils.substring("abc", 4)  = ""
     * StringUtils.substring("abc", -2) = "bc"
     * StringUtils.substring("abc", -4) = "abc"
     * </pre>
     *
     * @param str   获取子字符串的字符串
     * @param start 从开始的位置开始（负数表示从字符串末尾开始倒数这个数字）
     * @return 子字符串
     */
    public static String substring(final CharSequence str, int start) {
        if (str == null) {
            return null;
        }
        // handle negatives
        if (start < 0) {
            start = str.length() + start;
        }
        if (start < 0) {
            start = 0;
        }
        if (start > str.length()) {
            return StringConstant.EMPTY;
        }
        return str.toString().substring(start);
    }

    /**
     * 从指定的字符串中获取子字符串
     * 
     * <pre>
     * StringUtils.substring(null, *, *)    = null
     * StringUtils.substring("", * ,  *)    = "";
     * StringUtils.substring("abc", 0, 2)   = "ab"
     * StringUtils.substring("abc", 2, 0)   = ""
     * StringUtils.substring("abc", 2, 4)   = "c"
     * StringUtils.substring("abc", 4, 6)   = ""
     * StringUtils.substring("abc", 2, 2)   = ""
     * StringUtils.substring("abc", -2, -1) = "b"
     * StringUtils.substring("abc", -4, 2)  = "ab"
     * </pre>
     *
     * @param str   获取子字符串的字符串
     * @param start 从开始的位置开始
     * @param end   结束位置（不包括）
     * @return 子字符串
     */
    public static String substring(final String str, int start, int end) {
        if (str == null) {
            return null;
        }
        // handle negatives
        if (end < 0) {
            end = str.length() + end;
        }
        if (start < 0) {
            start = str.length() + start;
        }
        if (end > str.length()) {
            end = str.length();
        }
        if (start > str.length()) {
            return StringConstant.EMPTY;
        }
        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }
        return str.substring(start, end);
    }

    /**
     * 截断字符串
     * @param str      源字符串
     * @param maxWidth 结果字符串的最大长度（必须为正整数）
     * @return 截断后的字符串
     */
    public static String truncate(final String str, final int maxWidth) {
        return truncate(str, 0, maxWidth);
    }

    /**
     * 截断字符串
     * @param str      源字符串
     * @param offset   源字符串的左边缘
     * @param maxWidth 结果字符串的最大长度（必须为正整数）
     * @return 截断后的字符串
     */
    public static String truncate(final String str, final int offset, final int maxWidth) {
        if (offset < 0 || maxWidth < 0 || str == null) {
            return null;
        }
        if (offset > str.length()) {
            return StringConstant.EMPTY;
        }
        if (str.length() > maxWidth) {
            final int ix = Math.min(offset + maxWidth, str.length());
            return str.substring(offset, ix);
        }
        return str.substring(offset);
    }

    /**
     * 从字符串中删除所有空白
     * @param str 要处理的字符串
     * @return 没有空格的字符串，如果输入null，则返回{@code null}
     */
    public static String deleteWhitespace(final String str) {
        if (isEmpty(str)) {
            return str;
        }
        final StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
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
     * 大写首字母<br>
     * @param str 字符串
     * @return 首字母大写的字符串
     */
    public static String upperFirst(CharSequence str) {
        if (str == null) {
            return null;
        }
        if (str.length() > 0) {
            char firstChar = str.charAt(0);
            if (Character.isLowerCase(firstChar)) {
                return new StringBuilder()//
                        .append(Character.toUpperCase(firstChar))//
                        .append(str.subSequence(1, str.length()))//
                        .toString();//
            }
        }
        return str.toString();
    }

    /**
     * 小写首字母<br>
     * @param str 字符串
     * @return 首字母小写的字符串
     */
    public static String lowerFirst(CharSequence str) {
        if (str == null) {
            return null;
        }
        if (str.length() > 0) {
            char firstChar = str.charAt(0);
            if (Character.isUpperCase(firstChar)) {
                return new StringBuilder()//
                        .append(Character.toLowerCase(firstChar))//
                        .append(str.subSequence(1, str.length()))//
                        .toString();//
            }
        }
        return str.toString();
    }

    /**
     * 去掉指定前缀
     * @param str    字符串
     * @param prefix 前缀
     * @return 去掉指定前缀的字符串，若前缀不是 preffix， 返回原字符串
     */
    public static String removePrefix(CharSequence str, CharSequence prefix) {
        if (isEmpty(str) || isEmpty(prefix)) {
            return str.toString();
        }
        final String result = str.toString();
        if (result.startsWith(prefix.toString())) {
            return result.substring(prefix.length());
        }
        return result;
    }

    /**
     * 忽略大小写去掉指定前缀
     * @param str    字符串
     * @param prefix 前缀
     * @return 去掉指定前缀的字符串，若前缀不是 prefix， 返回原字符串
     */
    public static String removePrefixIgnoreCase(CharSequence str, CharSequence prefix) {
        if (isEmpty(str) || isEmpty(prefix)) {
            return string(str);
        }

        final String result = str.toString();
        if (CharSequenceUtil.startWithIgnoreCase(str, prefix)) {
            return result.substring(prefix.length());
        }
        return result;
    }

    /**
     * 去掉指定后缀
     * @param str    字符串
     * @param suffix 后缀
     * @return 去掉指定后缀的字符串，若后缀不是 suffix， 返回原字符串
     */
    public static String removeSuffix(CharSequence str, CharSequence suffix) {
        if (isEmpty(str) || isEmpty(suffix)) {
            return str.toString();
        }
        final String result = str.toString();
        if (result.endsWith(suffix.toString())) {
            return result.substring(0, result.length() - suffix.length());
        }
        return result;
    }

    /**
     * 忽略大小写去掉指定后缀
     * @param str    字符串
     * @param suffix 后缀
     * @return 去掉指定后缀的字符串，若后缀不是 suffix，返回原字符串
     */
    public static String removeSuffixIgnoreCase(CharSequence str, CharSequence suffix) {
        if (isEmpty(str) || isEmpty(suffix)) {
            return str.toString();
        }
        final String result = str.toString();
        if (CharSequenceUtil.endWithIgnoreCase(str, suffix)) {
            return result.substring(0, result.length() - suffix.length());
        }
        return result;
    }

    /**
     * 将所提供的集合{@code Iterator}元素连接成字符串。
     * @param iterable  要连接在一起的集合
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
     * @param iterator  要连接在一起的集合
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
     * @param values    需要成字符串的对象数组
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
     * @param iterable  要连接在一起的集合
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
     * @param iterator  要连接在一起的集合
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
     * @param str       要处理的字符串
     * @param separator 分隔符字符
     * @return 切分后的字符串数组
     */
    public static String[] split(final CharSequence str, final String separator) {
        return splitWorker(str, separator, -1, false);
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
     * @param str       要处理的字符串
     * @param separator 分隔符字符
     * @param max       返回数组的最大元素数。-1代表没有限制
     * @return 切分后的字符串数组
     */
    public static String[] split(final CharSequence str, final String separator, final int max) {
        return splitWorker(str, separator, max, false);
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
     * @param str       要处理的字符串
     * @param separator 分隔符字符
     * @return 切分后的字符串数组
     */
    public static String[] splitPreserveAllTokens(final CharSequence str, final String separator) {
        return splitWorker(str, separator, -1, true);
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
     * @param str       要处理的字符串
     * @param separator 分隔符字符
     * @param max       返回数组的最大元素数。-1代表没有限制
     * @return 切分后的字符串数组
     */
    public static String[] splitPreserveAllTokens(final CharSequence str, final String separator, final int max) {
        return splitWorker(str, separator, max, true);
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
     * @param str       要处理的字符串
     * @param separator 分隔符字符
     * @return 切分后的字符串数组
     */
    public static String[] splitPurify(final CharSequence str, final String separator) {
        String[] array = split(str, separator);
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
     * @param str     需要填充的字符串
     * @param size    要填充到的大小
     * @param padChar 要填充的字符
     * @return 左填充字符串或原始字符串（如果不需要填充）
     */
    public static String leftPad(final CharSequence str, final int size, final char padChar) {
        if (str == null) {
            return null;
        }
        final int pads = size - str.length();
        if (pads <= 0) {
            return str.toString();
        }
        StringBuilder builder = new StringBuilder(size);
        for (int i = 0; i < pads; i++) {
            builder.append(padChar);
        }
        builder.append(str);
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
     * @param str     需要填充的字符串
     * @param size    要填充到的大小
     * @param padChar 要填充的字符
     * @return 右填充字符串或原始字符串（如果不需要填充）
     */
    public static String rightPad(final CharSequence str, final int size, final char padChar) {
        if (str == null) {
            return null;
        }
        final int pads = size - str.length();
        if (pads <= 0) {
            return str.toString();
        }
        StringBuilder builder = new StringBuilder(size);
        builder.append(str);
        for (int i = 0; i < pads; i++) {
            builder.append(padChar);
        }
        return builder.toString();
    }

    /**
     * 切分字符串
     * @param str               要处理的字符串
     * @param separator         分隔符字符串
     * @param max               得到数组的长度，-1 代表没有限制
     * @param preserveAllTokens 保留所有令牌，相邻的分隔符是作为空标记分隔符处理；如果{@code false}，则相邻分离器被视为一个分离器。
     * @return 切分后的字符串数组
     */
    private static String[] splitWorker(final CharSequence str, final String separator, final int max,
            final boolean preserveAllTokens) {
        if (str == null) {
            return null;
        }
        final int len = str.length();
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
                if (Character.isWhitespace(str.charAt(i))) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.subSequence(start, i).toString());
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
                if (str.charAt(i) == sep) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.subSequence(start, i).toString());
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
                if (separator.indexOf(str.charAt(i)) >= 0) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.subSequence(start, i).toString());
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
            list.add(str.subSequence(start, i).toString());
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
     * @param text   用于查找的文本
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
     * @param text     用于查找的文本
     * @param search   查找的字符串
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
     * @param text   要检查的文本
     * @param search 要查找的字符串
     * @return 如果文本中包含要查找的字符串则返回 {@code true}，否则返回{@code false}
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
     * @param text     用于查找的文本
     * @param search   查找的字符串
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
     * 指定字符在文本中出现的位置
     * @param text   用于查找的文本
     * @param search 被查找的字符
     * @return 字符第一次出现的位置，如果未找到返回 -1
     */
    public static int indexOf(final CharSequence text, final char search) {
        return indexOf(text, search, 0);
    }

    /**
     * 指定字符在文本中出现的位置
     * @param text     用于查找的文本
     * @param search   被查找的字符
     * @param startPos 开始查找的位置
     * @return 字符第一次出现的位置，如果未找到返回 -1
     */
    public static int indexOf(final CharSequence text, final char search, final int startPos) {
        return CharSequenceUtil.indexOf(text, search, startPos);
    }

    /**
     * 判断文本中是否包含指定字符
     * 
     * <pre>
     * StringUtil.contains(null, *)     = false
     * StringUtil.contains("", *)       = false
     * StringUtil.contains("123", '1')  = true
     * StringUtil.contains("abc", 'a')  = true
     * StringUtil.contains("abc", 'a')  = true
     * StringUtil.contains("abc", 'z')  = false
     * </pre>
     *
     * @param text   要检查的文本
     * @param search 要查找的字符
     * @return 如果文本中包含要查找的字符返回 {@code true}，否则返回{@code false}
     */
    public static boolean contains(final CharSequence text, final char search) {
        return indexOf(text, search) > -1;
    }

    // ==============================ModifyMethods====================================
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
     * @param text         用于查找替换的文本
     * @param searchString 查找的字符串
     * @param replacement  替换的字符串
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
     * @param text         用于查找替换的文本
     * @param searchString 查找的字符串
     * @param replacement  替换的字符串
     * @param max          需要替换的字符串个数，{@code -1} 表示无限制
     * @return 处理后的文本
     */
    public static String replace(final String text, final String searchString, final String replacement,
            final int max) {
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
     * @param text         要处理的文本
     * @param searchString 要替换的文本
     * @param replacement  要替换为的文本
     * @param max          需要替换的字符串个数，{@code -1} 表示无限制
     * @param ignoreCase   查找时是否忽略大小写
     * @return 替换后的文本
     */
    private static String replace(final String text, String searchString, final String replacement, int max,
            final boolean ignoreCase) {
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

    /**
     * 除去字符串头部的空白
     * @param str 要处理的字符串
     * @return 除去空白的字符串
     */
    public static String trimStart(String str) {
        int st = 0;
        int len = str.length();
        while ((st < len) && (str.charAt(st) <= ' ')) {
            st++;
        }
        return ((st > 0) || (len < str.length())) ? str.substring(st, len) : str;
    }

    /**
     * 除去字符串尾部的空白
     * @param str 要处理的字符串
     * @return 除去空白的字符串
     */
    public static String trimEnd(String str) {
        int st = 0;
        int len = str.length();
        while ((st < len) && (str.charAt(len - 1) <= ' ')) {
            len--;
        }
        return ((st > 0) || (len < str.length())) ? str.substring(st, len) : str;
    }

    /**
     * 去除字符串中指定的多个字符，如有多个则全部去除<br>
     * @param str   字符串
     * @param chars 字符列表
     * @return 处理后的字符串
     */
    public static String removeAll(CharSequence str, char... chars) {
        if (isEmpty(null) || ArrayUtil.isEmpty(chars)) {
            return string(str);
        }
        int length = str.length();
        if (length == 0) {
            return StringConstant.EMPTY;
        }
        final StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char c = str.charAt(i);
            if (!ArrayUtil.contains(chars, c)) {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    /**
     * 去除所有换行符（ \r 和 \n）
     * @param str 字符串
     * @return 处理后的字符串
     */
    public static String removeAllLineBreak(CharSequence str) {
        return removeAll(str, CharConstant.CR, CharConstant.LF);
    }

    /**
     * 判断文本中的字符是否全部为大写。<br>
     * 1. 大写字母包括A-Z<br>
     * 2. 其它非字母的Unicode符都算作大写<br>
     * @param str 被检查的文本
     * @return 是否全部为大写
     */
    public static boolean isAllUpperCase(final CharSequence str) {
        if (StringUtil.isEmpty(str)) {
            return false;
        }
        for (int i = 0, length = str.length(); i < length; i++) {
            if (Character.isLowerCase(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断文本中的字符是否全部为小写<br>
     * 1. 小写字母包括a-z<br>
     * 2. 其它非字母的Unicode符都算作大写<br>
     * @param str 被检查的文本
     * @return 是否全部为大写
     */
    public static boolean isAllLowerCase(final CharSequence str) {
        if (StringUtil.isEmpty(str)) {
            return false;
        }
        for (int i = 0, length = str.length(); i < length; i++) {
            if (Character.isUpperCase(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 切换文本中的大小写，大写转小写，小写转大写。
     *
     * <pre>
     * StringUtil.swapCase(null)                    = null
     * StringUtil.swapCase("")                      = ""
     * StringUtil.swapCase("Hello World")           = "hELLO wORLD"
     * </pre>
     *
     * @param str 文本
     * @return 交换后的文本
     */
    public static String swapCase(final String str) {
        if (StringUtil.isEmpty(str)) {
            return str;
        }
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            final char ch = chars[i];
            if (Character.isUpperCase(ch) || Character.isTitleCase(ch)) {
                chars[i] = Character.toLowerCase(ch);
            } else if (Character.isLowerCase(ch)) {
                chars[i] = Character.toUpperCase(ch);
            }
        }
        return new String(chars);
    }

    // ==============================ConvertStringMethods=============================
    /**
     * 将对象转为字符串<br>
     * 
     * <pre>
     * 1、如果参数为{@code
     * null
     * }，则返回{@code
     * null
     * }
     * 2、Byte数组和ByteBuffer会被转换为对应字符串的数组
     * 3、对象数组会调用Arrays.toString方法
     * 4、其余情况会直接调用对象的 toString 方法
     * </pre>
     * 
     * @param obj 对象
     * @return 字符串
     */
    public static String string(Object obj) {
        return string(obj, CharsetConstant.DEFAULT);
    }

    /**
     * 将对象转为字符串，如果参数为{@code null}，则返回{@code null}
     * 
     * <pre>
     * 1、如果参数为{@code
     * null
     * }，则返回{@code
     * null
     * }
     * 2、Byte数组和ByteBuffer会被转换为对应字符串的数组
     * 3、对象数组会调用Arrays.toString方法
     * 4、其余情况会直接调用对象的 toString 方法
     * </pre>
     *
     * @param obj      对象
     * @param encoding 字符集
     * @return 字符串
     */
    public static String string(Object obj, Charset encoding) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        if (obj instanceof CharSequence) {
            return obj.toString();
        }
        if (obj instanceof byte[]) {
            return string((byte[]) obj, encoding);
        }
        if (obj instanceof Byte[]) {
            return string((Byte[]) obj, encoding);
        }
        if (obj instanceof ByteBuffer) {
            return string((ByteBuffer) obj, encoding);
        }
        if (ArrayUtil.isArray(obj)) {
            return ArrayUtil.toString(obj);
        }
        return obj.toString();
    }

    /**
     * 将byte数组转为字符串
     * @param bytes    byte数组
     * @param encoding 字符集
     * @return 字符串
     */
    public static String string(byte[] bytes, String encoding) {
        return string(bytes, getCharsetQuietly(encoding));
    }

    /**
     * 解码字节码
     * @param data     字符串
     * @param encoding 字符集，如果此字段为空，则解码的结果取决于平台
     * @return 解码后的字符串
     */
    public static String string(byte[] data, Charset encoding) {
        return data == null ? null : new String(data, ObjectUtil.defaultIfNull(encoding, CharsetConstant.DEFAULT));
    }

    /**
     * 将Byte数组转为字符串
     * @param bytes    byte数组
     * @param encoding 字符集
     * @return 字符串
     */
    public static String string(Byte[] bytes, String encoding) {
        return string(bytes, getCharsetQuietly(encoding));
    }

    /**
     * 将字节数组转为字符串，使用UTF8编码
     * @param bytes 字节数组
     * @return 字符串
     */
    public static String string(final byte[] bytes) {
        return bytes == null ? null : new String(bytes, CharsetConstant.DEFAULT);
    }

    /**
     * 解码字节码
     * @param data     字符串
     * @param encoding 字符集，如果此字段为空，则解码的结果取决于平台
     * @return 解码后的字符串
     */
    public static String string(Byte[] data, Charset encoding) {
        if (data == null) {
            return null;
        }

        byte[] bytes = new byte[data.length];
        Byte dataByte;
        for (int i = 0; i < data.length; i++) {
            dataByte = data[i];
            bytes[i] = (dataByte == null) ? -1 : dataByte;
        }

        return string(bytes, encoding);
    }

    /**
     * 将编码的byteBuffer数据转换为字符串
     * @param data     数据
     * @param encoding 字符集，如果为空使用当前系统字符集
     * @return 字符串
     */
    public static String string(ByteBuffer data, String encoding) {
        return data == null ? null : string(data, getCharsetQuietly(encoding));
    }

    /**
     * 将编码的byteBuffer数据转换为字符串
     * @param data     数据
     * @param encoding 字符集，如果为空使用当前系统字符集
     * @return 字符串
     */
    public static String string(ByteBuffer data, Charset encoding) {
        return ObjectUtil.defaultIfNull(encoding, CharsetConstant.DEFAULT).decode(data).toString();
    }

    // ==============================EncoderMethods===================================
    /**
     * 将字符串转为字节数组，使用UTF8编码
     * @param string 字符串
     * @return 字节数组
     */
    public static byte[] getBytes(final String string) {
        return string == null ? null : string.getBytes(CharsetConstant.DEFAULT);
    }

    /**
     * 将字符串转为字节数组
     * @param string  字符串
     * @param charset 编码
     * @return 字节数组
     */
    public static byte[] getBytes(final String string, Charset charset) {
        if (string == null) {
            return null;
        }
        if (charset == null) {
            return string.getBytes();
        }
        return string.getBytes(charset);
    }

    // ==============================FormatMethods====================================
    /**
     * 格式化字符串<br>
     * 此方法只是简单将占位符 {} 按照顺序替换为参数<br>
     * 如果想输出 {} 使用 \\转义 { 即可，如果想输出 {} 之前的 \ 使用双转义符 \\\\ 即可<br>
     * 例：<br>
     * 通常使用：format("this is {} for {}", "a", "b") =》 this is a for b<br>
     * 转义{}： format("this is \\{} for {}", "a", "b") =》 this is \{} for a<br>
     * 转义\： format("this is \\\\{} for {}", "a", "b") =》 this is \a for b<br>
     * @param template 字符串模板
     * @param args     参数列表
     * @return 结果
     */
    public static String format(CharSequence template, Object... args) {
        if (template == null) {
            return StringConstant.NULL;
        }
        if (ArrayUtil.isEmpty(args) || isBlank(template)) {
            return template.toString();
        }
        return formatWith(template.toString(), StringConstant.EMPTY_JSON_OBJECT, args);
    }

    /**
     * 格式化字符串<br>
     * 此方法只是简单将指定占位符 按照顺序替换为参数<br>
     * 如果想输出占位符使用 \\转义即可，如果想输出占位符之前的 \ 使用双转义符 \\\\ 即可<br>
     * 例：<br>
     * 通常使用：format("this is {} for {}", "{}", "a", "b") =》 this is a for b<br>
     * 转义{}： format("this is \\{} for {}", "{}", "a", "b") =》 this is {} for a<br>
     * 转义\： format("this is \\\\{} for {}", "{}", "a", "b") =》 this is \a for b<br>
     * @param template    字符串模板
     * @param placeHolder 占位符，例如{}
     * @param args        参数列表
     * @return 结果
     */
    private static String formatWith(String template, String placeHolder, Object... args) {

        if (StringUtil.isBlank(template) || StringUtil.isBlank(placeHolder) || ArrayUtil.isEmpty(args)) {
            return template;
        }

        final int templateLength = template.length();
        final int placeHolderLength = placeHolder.length();

        // 初始化定义好的长度以获得更好的性能
        final StringBuilder sbuf = new StringBuilder(templateLength + 1024);

        int handledPosition = 0;// 记录已经处理到的位置
        int delimIndex;// 占位符所在位置
        for (int argIndex = 0; argIndex < args.length; argIndex++) {
            delimIndex = template.indexOf(placeHolder, handledPosition);
            if (delimIndex == -1) {// 剩余部分无占位符
                if (handledPosition == 0) { // 不带占位符的模板直接返回
                    return template;
                }
                // 字符串模板剩余部分不再包含占位符，加入剩余部分后返回结果
                sbuf.append(template, handledPosition, templateLength);
                return sbuf.toString();
            }

            // 转义符
            if (delimIndex > 0 && template.charAt(delimIndex - 1) == CharConstant.BACKSLASH) {// 转义符
                if (delimIndex > 1 && template.charAt(delimIndex - 2) == CharConstant.BACKSLASH) {// 双转义符
                    // 转义符之前还有一个转义符，占位符依旧有效
                    sbuf.append(template, handledPosition, delimIndex - 1);
                    sbuf.append(StringUtil.string(args[argIndex]));
                    handledPosition = delimIndex + placeHolderLength;
                } else {
                    // 占位符被转义
                    argIndex--;
                    sbuf.append(template, handledPosition, delimIndex - 1);
                    sbuf.append(placeHolder.charAt(0));
                    handledPosition = delimIndex + 1;
                }
            } else {// 正常占位符
                sbuf.append(template, handledPosition, delimIndex);
                sbuf.append(StringUtil.string(args[argIndex]));
                handledPosition = delimIndex + placeHolderLength;
            }
        }

        // 加入最后一个占位符后所有的字符
        sbuf.append(template, handledPosition, templateLength);

        return sbuf.toString();
    }

    /**
     * 格式化文本，使用 {varName} 占位<br>
     * map = {a: "aValue", b: "bValue"} format("{a} and {b}", map) ---=》 aValue and bValue
     * @param template   文本模板，被替换的部分用 {key} 表示
     * @param map        参数值对
     * @param ignoreNull 是否忽略 {@code null} 值，忽略则 {@code null} 值对应的变量不被替换，否则替换为""
     * @return 格式化后的文本
     */
    public static String format(CharSequence template, Map<?, ?> map, boolean ignoreNull) {
        if (template == null) {
            return null;
        }
        if (map == null || map.isEmpty()) {
            return template.toString();
        }

        String templateString = template.toString();
        String value;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            value = StringUtil.string(entry.getValue());
            if (value == null && ignoreNull) {
                continue;
            }
            templateString = StringUtil.replace(templateString, "{" + entry.getKey() + "}", value);
        }
        return templateString;
    }

    // ==============================ToolMethods======================================
    /**
     * 转换为Charset对象
     * @param charsetName 字符集
     * @return Charset
     */
    private static Charset getCharsetQuietly(String charsetName) {
        if (StringUtil.isNotBlank(charsetName)) {
            try {
                return Charset.forName(charsetName);
            } catch (Exception e) {
                // ignore
            }
        }
        return CharsetConstant.DEFAULT;
    }
}
