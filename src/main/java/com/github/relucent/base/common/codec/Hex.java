package com.github.relucent.base.common.codec;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.github.relucent.base.common.lang.StringUtil;

/**
 * 十六进制编码工具类
 */
public class Hex {

	// =================================Fields================================================
	/** 默认字符编码 */
	public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
	/** 十六进制输出(小写) */
	private static final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    /** 十六进制输出(大写) */
	private static final char[] DIGITS_UPPER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	// =================================Constructors===========================================
	/**
	 * 工具类私有构造
	 */
	protected Hex() {
	}

	// =================================Methods================================================
	/**
	 * 判断字符串是否是一个十六进制值的字符串
	 * @param data 待验证的字符串
	 * @return 如果字符串是一个十六进制值的字符串，则返回{@code true}
	 */
	public static boolean isHex(CharSequence data) {
		if (StringUtil.isEmpty(data)) {
			return false;
		}
		final int len = data.length();
		for (int i = 0; i < len; i++) {
			char ch = data.charAt(i);
			// ![0-9a-fA-F]
			if (!('0' <= ch && ch <= '9' || 'a' <= ch && ch <= 'f' || 'A' <= ch && ch <= 'F')) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 将字节数组按顺序转换为表示每个字节的十六进制值的字符数组。返回的数组将是传递数组长度的两倍，因为它需要两个字符来表示任何给定的字节。
	 * @param data 字节数组
	 * @return 表示十六进制的字符数组
	 */
	public static char[] encodeHex(final byte[] data) {
		return encodeHex(data, true);
	}

	/**
	 * 将字节数组按顺序转换为表示每个字节的十六进制值的字符数组。返回的数组将是传递数组长度的两倍，因为它需要两个字符来表示任何给定的字节。
	 * @param data 字节数组
	 * @param toLowerCase {@code true}转换为小写, {@code false} 转换为大写
	 * @return 表示十六进制的字符数组
	 */
	public static char[] encodeHex(final byte[] data, final boolean toLowerCase) {
		return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
	}

	/**
	 * 将字节数组按顺序转换为表示每个字节的十六进制值的字符串。返回的字符串将是传递数组长度的两倍，因为它需要两个字符来表示任何给定的字节。
	 * @param data 字节数组
	 * @return 包含十六进制字符的字符串
	 */
	public static String encodeHexString(final byte[] data) {
		return new String(encodeHex(data));
	}

	/**
	 * 将字节数组按顺序转换为表示每个字节的十六进制值的字符串。返回的字符串将是传递数组长度的两倍，因为它需要两个字符来表示任何给定的字节。
	 * @param data 字节数组
	 * @param toLowerCase {@code true}转换为小写, {@code false} 转换为大写
	 * @return 包含十六进制字符的字符串
	 */
	public static String encodeHexString(final byte[] data, final boolean toLowerCase) {
		return new String(encodeHex(data, toLowerCase));
	}

	/**
	 * 将十六进制字符数组转换为字符串。返回的数组将是传递字符串长度的一半，因为它需要两个字符来表示任何给定的字节。如果传递字符串长度是奇数，则会引发异常。
	 * @param data 包含十六进制数字的字符串
	 * @return 字节数组，包含从所提供的字符串中解码的二进制数据。
	 */
	public static byte[] decodeHex(final String data) {
		return decodeHex(data.toCharArray());
	}

	/**
	 * 将表示十六进制值的字符数组转换为这些值的字节数组。返回的数组将是传递数组长度的一半，因为它需要两个字符来表示任何给定的字节。如果传递的char数组包含奇数个元素，则会引发异常。
	 * @param data 包含十六进制数字的字符数组
	 * @return 字节数组，包含从所提供的字符数组中解码的二进制数据。
	 */
	public static byte[] decodeHex(final char[] data) {
		final int len = data.length;
		if ((len & 0x01) != 0) {
			throw new DecoderException("Odd number of characters.");
		}
		final byte[] out = new byte[len >> 1];
		// two characters form the hex value.
		for (int i = 0, j = 0; j < len; i++) {
			int f = toDigit(data[j], j) << 4;
			j++;
			f = f | toDigit(data[j], j);
			j++;
			out[i] = (byte) (f & 0xFF);
		}
		return out;
	}

	/**
	 * 将字节数组按顺序转换为表示每个字节的十六进制值的字符数组。返回的数组将是传递数组长度的两倍，因为它需要两个字符来表示任何给定的字节。
	 * @param data 字节数组
	 * @param toDigits 输入输出字母表(length&amp;=16)
	 * @return 表示十六进制的字符数组
	 */
	private static char[] encodeHex(final byte[] data, final char[] toDigits) {
		final int l = data.length;
		final char[] out = new char[l << 1];
		// two characters form the hex value.
		for (int i = 0, j = 0; i < l; i++) {
			out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
			out[j++] = toDigits[0x0F & data[i]];
		}
		return out;
	}

	/**
	 * 将十六进制字符转换为整数。
	 * @param ch 要转换为整数的字符
	 * @param index 源中字符的索引，用于出现异常时候显示位置
	 * @return 整数
	 */
	private static int toDigit(final char ch, final int index) {
		final int digit = Character.digit(ch, 16);
		if (digit == -1) {
			throw new DecoderException("Illegal hexadecimal character " + ch + " at index " + index);
		}
		return digit;
	}
}
