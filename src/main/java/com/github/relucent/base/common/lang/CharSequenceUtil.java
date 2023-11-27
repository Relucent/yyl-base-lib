package com.github.relucent.base.common.lang;

import com.github.relucent.base.common.constant.ArrayConstant;

/**
 * 字符序列{@link CharSequence}工具类<br>
 * @author YYL
 * @see StringUtil
 */
public class CharSequenceUtil {

	// ==============================Fields===========================================
	/** 表示未找到的索引 */
	private static final int NOT_FOUND = -1;

	/** 转换长度限制 */
	private static final int TO_STRING_LIMIT = 16;

	// ==============================Constructors=====================================
	/**
	 * 工具类方法，实例不应在标准编程中构造。
	 */
	protected CharSequenceUtil() {
	}

	// ==============================Methods==========================================
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
	 * 获取字符串的长度，如果为null返回0
	 * @param cs 字符串
	 * @return 字符串的长度，如果为null返回0
	 */
	public static int length(CharSequence cs) {
		return cs == null ? 0 : cs.length();
	}

	/**
	 * 字符串是否为空
	 * @param cs 被检测的字符串
	 * @return 是否为空
	 */
	public static boolean isEmpty(CharSequence cs) {
		return cs == null || cs.length() == 0;
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
	 * 测试此字符串是否以指定的前缀开头<br>
	 * 
	 * <pre>
	 *  CharSequenceUtil.startWith("123", "123");    -- true
	 *  CharSequenceUtil.startWith("abcdef", "abc"); -- true
	 *  CharSequenceUtil.startWith("ABCDEF", "abc"); -- false
	 *  CharSequenceUtil.startWith("ABCDEF", "def"); -- false
	 * </pre>
	 *
	 * @param string 检测的字符串
	 * @param prefix 比较的前缀
	 * @return 字符串是否以指定前缀开头
	 */
	public static boolean startWith(CharSequence string, CharSequence prefix) {
		return (string == null || prefix == null) ? false : string.toString().startsWith(prefix.toString());
	}

	/**
	 * 测试此字符串是否以指定的前缀开头，忽略大小写<br>
	 * 
	 * <pre>
	 *  CharSequenceUtil.startWith("123", "123");    -- true
	 *  CharSequenceUtil.startWith("abcdef", "abc"); -- true
	 *  CharSequenceUtil.startWith("ABCDEF", "abc"); -- true
	 *  CharSequenceUtil.startWith("ABCDEF", "def"); -- false
	 * </pre>
	 *
	 * @param string 检测的字符串
	 * @param prefix 比较的前缀
	 * @return 字符串是否以指定前缀开头（忽略大小写）
	 */
	public static boolean startWithIgnoreCase(final CharSequence string, final CharSequence prefix) {
		if (string == null || prefix == null) {
			return false;
		}
		return regionMatches(string, true, 0, prefix, 0, prefix.length());
	}

	/**
	 * 测试此字符串是否以指定的后缀结尾<br>
	 * 
	 * <pre>
	 *  CharSequenceUtil.endWith("123", "123");    -- true
	 *  CharSequenceUtil.endWith("abcdef", "def"); -- true
	 *  CharSequenceUtil.endWith("ABCDEF", "def"); -- false
	 *  CharSequenceUtil.endWith("ABCDEF", "DEF"); -- true
	 *  CharSequenceUtil.endWith("ABCDEF", "abc"); -- false
	 *  CharSequenceUtil.endWith("ABCDEF", "ABCDEFG"); -- false
	 * </pre>
	 *
	 * @param string 检测的字符串
	 * @param suffix 比较的后缀
	 * @return 字符串是否以指定后缀结尾头
	 */
	public static boolean endWith(CharSequence string, CharSequence suffix) {
		return (string == null || suffix == null) ? false : string.toString().endsWith(suffix.toString());
	}

	/**
	 * 测试此字符串是否以指定的后缀结尾，忽略大小写<br>
	 * 
	 * <pre>
	 *  CharSequenceUtil.endWithIgnoreCase("123", "123");    -- true
	 *  CharSequenceUtil.endWithIgnoreCase("abcdef", "def"); -- true
	 *  CharSequenceUtil.endWithIgnoreCase("ABCDEF", "def"); -- true
	 *  CharSequenceUtil.endWithIgnoreCase("ABCDEF", "DEF"); -- true
	 *  CharSequenceUtil.endWithIgnoreCase("ABCDEF", "abc"); -- false
	 *  CharSequenceUtil.endWithIgnoreCase("ABCDEF", "ABCDEFG"); -- false
	 * </pre>
	 *
	 * @param string 检测的字符串
	 * @param suffix 比较的后缀
	 * @return 字符串是否以指定后缀结尾头
	 */
	public static boolean endWithIgnoreCase(CharSequence string, CharSequence suffix) {
		if (string == null || suffix == null) {
			return false;
		}
		return regionMatches(string, true, string.length() - suffix.length(), suffix, 0, suffix.length());
	}

	/**
	 * 比较两个CharSequences是否相同，区分大小写
	 * 
	 * <pre>
	 * #equals(null, null)   = true
	 * #.equals(null, "abc")  = false
	 * #.equals("abc", null)  = false
	 * #.equals("abc", "abc") = true
	 * #.equals("abc", "ABC") = false
	 * </pre>
	 *
	 * @param cs1 第一个CharSequence，可以是 {@code null}
	 * @param cs2 第二个CharSequence，可以是 {@code null}
	 * @return 如果两个CharSequences相同（区分大小写），或者都是{@code null}则返回{@code true}
	 * @see Object#equals(Object)
	 * @see #equalsIgnoreCase(CharSequence, CharSequence)
	 */
	public static boolean equals(final CharSequence cs1, final CharSequence cs2) {
		if (cs1 == cs2) {
			return true;
		}
		if (cs1 == null || cs2 == null) {
			return false;
		}
		if (cs1.length() != cs2.length()) {
			return false;
		}
		if (cs1 instanceof String && cs2 instanceof String) {
			return cs1.equals(cs2);
		}
		// 逐步比较
		final int length = cs1.length();
		for (int i = 0; i < length; i++) {
			if (cs1.charAt(i) != cs2.charAt(i)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 比较两个CharSequences是否相同，忽略大小写
	 *
	 * <pre>
	 * #equalsIgnoreCase(null, null)   = true
	 * #equalsIgnoreCase(null, "abc")  = false
	 * #equalsIgnoreCase("abc", null)  = false
	 * #equalsIgnoreCase("abc", "abc") = true
	 * #equalsIgnoreCase("abc", "ABC") = true
	 * </pre>
	 * 
	 * @param cs1 第一个CharSequence，可以是 {@code null}
	 * @param cs2 第二个CharSequence，可以是 {@code null}
	 * @return 如果两个CharSequences相同（忽略大小写），或者都是{@code null}则返回{@code true}
	 * @see Object#equals(Object)
	 * @see #equalsIgnoreCase(CharSequence, CharSequence)
	 */
	public static boolean equalsIgnoreCase(final CharSequence cs1, final CharSequence cs2) {
		if (cs1 == cs2) {
			return true;
		}
		if (cs1 == null || cs2 == null) {
			return false;
		}
		if (cs1.length() != cs2.length()) {
			return false;
		}
		return regionMatches(cs1, true, 0, cs2, 0, cs1.length());
	}

	/**
	 * 将 CharSequence转换为字符数组<br>
	 * 如果传入参数为{@code null}，则返回一个 0 长度的字符数组
	 * @param source 要处理的{@code CharSequence}
	 * @return 字符数组，不会为null.
	 */
	public static char[] toCharArray(final CharSequence source) {
		final int len = length(source);
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
	 * 指定范围内查找指定字符
	 * @param cs 要处理的字符序列 {@code CharSequence}
	 * @param ch 被查找的字符
	 * @param start 起始位置，如果小于0，从0开始查找
	 * @param end 终止位置
	 * @param ignoreCase 是否不区分大小写
	 * @return 位置
	 */
	static int indexOf(final CharSequence cs, final char ch, final int start, final int end, final boolean ignoreCase) {
		int from = Math.max(start, 0);
		int to = Math.min(end, cs.length());
		if (!ignoreCase && cs instanceof String && ((String) cs).length() == to) {
			return ((String) cs).indexOf(ch, from);
		}
		for (int i = from; i < to; i++) {
			if (CharUtil.equals(ch, cs.charAt(i), ignoreCase)) {
				return i;
			}
		}
		return NOT_FOUND;
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
}
