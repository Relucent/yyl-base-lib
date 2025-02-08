package com.github.relucent.base.common.codec;

/**
 * Base64工具类，提供Base64的编码和解码功能。<br>
 * Base64是一种用64个字符来表示任意二进制数据的方法，常用于在URL、Cookie、网页中传输少量二进制数据。<br>
 * Base64要求把每3个8Bit的字节转换为4个6Bit的字节（3*8=4*6=24），然后把6Bit再添两位高位0，组成四个8Bit的字节，转换后的字符串理论上将要比原来增加1/3。<br>
 */
public class Base64 {

	/**
	 * 将字节数组编码成Base64字符串
	 * @param data 字节数组
	 * @return Base64字符串
	 */
	public static String encode(byte[] data) {
		// javax.xml.bind.DatatypeConverter.printBase64Binary(data); #JDK7-
		return java.util.Base64.getEncoder().encodeToString(data);
	}

	/**
	 * 将Base64字符串解码成字节数组
	 * @param base64 Base64字符串
	 * @return 字节数组
	 */
	public static byte[] decode(String base64) {
		// javax.xml.bind.DatatypeConverter.parseBase64Binary(base64); JDK7-
		return java.util.Base64.getDecoder().decode(base64);
	}
}
