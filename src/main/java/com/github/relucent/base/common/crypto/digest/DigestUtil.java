package com.github.relucent.base.common.crypto.digest;

/**
 * 摘要算法工具类
 */
public class DigestUtil {

	// =================================MD5====================================================
	public static byte[] md5(byte[] input) {
		return Md5.create().digest(input);
	}

	public static byte[] md5(String input) {
		return Md5.create().digest(input);
	}

	public static String md5Hex(String input) {
		return Md5.create().digestHex(input);
	}

	public static String md5Hex(byte[] input) {
		return Md5.create().digestHex(input);
	}

	// =================================SM3====================================================
	public static byte[] sm3(byte[] input) {
		return Sm3.create().digest(input);
	}

	public static byte[] sm3(String input) {
		return Sm3.create().digest(input);
	}

	public static String sm3Hex(String input) {
		return Sm3.create().digestHex(input);
	}

	public static String sm3Hex(byte[] input) {
		return Sm3.create().digestHex(input);
	}
}
