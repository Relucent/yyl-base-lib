package com.github.relucent.base.common.crypto.symmetric;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import com.github.relucent.base.common.constant.IoConstant;

/**
 * AES-GCM 加密算法
 */
public class AesGcm {

	// ==============================Constants========================================
	public static final String TRANSFORMATION = "AES/GCM/NoPadding";
	public static final int DEFAULT_GCM_IV_LENGTH = 12;
	public static final int DEFAULT_GCM_TAG_LENGTH = 128;

	// ==============================Fields===========================================
	private final SecretKey key;
	private final SecureRandom random = new SecureRandom();
	private final int ivLength;
	private final int tagLengthBit;

	// ==============================Constructors=====================================
	/**
	 * 构造函数
	 * @param key 秘钥
	 */
	public AesGcm(SecretKey key) {
		this(key, DEFAULT_GCM_IV_LENGTH, DEFAULT_GCM_TAG_LENGTH);
	}

	/**
	 * 构造函数
	 * @param key          秘钥
	 * @param ivLength     IV长度
	 * @param tagLengthBit AEAD tag长度
	 */
	public AesGcm(SecretKey key, int ivLength, int tagLengthBit) {
		this.key = key;
		this.ivLength = ivLength;
		this.tagLengthBit = tagLengthBit;
	}

	// ==============================Methods==========================================
	/**
	 * 加密数据
	 * @param plainText 要加密的数据
	 * @return 加密后的数据
	 * @throws Exception 加密失败
	 */
	public AesGcmPayload encrypt(byte[] plainText) throws Exception {
		byte[] iv = new byte[ivLength];
		random.nextBytes(iv);
		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
		GCMParameterSpec spec = new GCMParameterSpec(tagLengthBit, iv);
		cipher.init(Cipher.ENCRYPT_MODE, key, spec);
		byte[] cipherText = cipher.doFinal(plainText);
		return new AesGcmPayload(iv, cipherText);
	}

	/**
	 * 对流数据进行加密（文件）<br>
	 * 1. 流式处理，不占用大内存<br>
	 * 2. IV 会写入文件头（解密必须读取）<br>
	 * 3. GCM 提供“加密 + 完整性校验”（防篡改）<br>
	 * 文件结构： [12字节IV][密文数据][16字节认证Tag（自动附加）]<br>
	 * @param input  要加密的数据
	 * @param output 加密后的数据
	 * @throws Exception 加密失败
	 */
	public void encrypt(InputStream input, OutputStream output) throws Exception {

		// 1. 生成 IV，GCM 推荐使用 12 字节 IV（性能最优 + 标准推荐）
		byte[] iv = new byte[ivLength];
		SecureRandom random = new SecureRandom();
		random.nextBytes(iv);

		// 2. 写 IV 到文件头（IV 不是密钥，不需要保密，但必须用于解密）
		output.write(iv);

		// 3. 初始化 Cipher
		GCMParameterSpec spec = new GCMParameterSpec(tagLengthBit, iv);
		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
		cipher.init(Cipher.ENCRYPT_MODE, key, spec);

		// 4. 使用流式加密
		try (CipherOutputStream cos = new CipherOutputStream(output, cipher)) {
			byte[] buffer = new byte[IoConstant.BUFFER_SIZE_1024KB]; // 1MB
			int len;
			while ((len = input.read(buffer)) != -1) {
				cos.write(buffer, 0, len);
			}
		}
	}

	/**
	 * 解密数据
	 * @param cipherPayload 被解密的数据(包含IV)
	 * @return 解密后的数据
	 */
	public byte[] decrypt(AesGcmPayload cipherPayload) throws Exception {
		byte[] iv = cipherPayload.getIv();
		byte[] cipherText = cipherPayload.getCipherText();
		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
		GCMParameterSpec spec = new GCMParameterSpec(tagLengthBit, iv);
		cipher.init(Cipher.DECRYPT_MODE, key, spec);
		return cipher.doFinal(cipherText);
	}

	/**
	 * 解密流数据（文件）<br>
	 * 解密必须保证：<br>
	 * 1. IV 与加密时一致（从文件头读取）<br>
	 * 2. 数据未被篡改（GCM 自动校验）<br>
	 * 文件结构： [12字节IV][密文数据][16字节认证Tag（自动附加）]<br>
	 * @param input  要解密的数据
	 * @param output 解密后的数据
	 * @throws Exception 解密失败
	 */
	public void decrypt(InputStream input, OutputStream output) throws Exception {

		// 1. 读取 IV（文件头12字节）
		byte[] iv = new byte[ivLength];

		// 必须完整读取，否则解密失败
		int readIv = input.read(iv);
		if (readIv != ivLength) {
			throw new IllegalArgumentException("Invalid encrypted file: missing IV");
		}

		// 2. 初始化 Cipher（解密模式）
		GCMParameterSpec spec = new GCMParameterSpec(tagLengthBit, iv);

		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
		cipher.init(Cipher.DECRYPT_MODE, key, spec);

		// 3. 流式解密
		try (CipherInputStream cis = new CipherInputStream(input, cipher)) {
			byte[] buffer = new byte[IoConstant.BUFFER_SIZE_1024KB];
			int len;
			while ((len = cis.read(buffer)) != -1) {
				output.write(buffer, 0, len);
			}
		}
	}

	// ==============================InnerClass=======================================
	/**
	 * AES-GCM 加密结果封装类
	 */
	public static class AesGcmPayload {

		/** 初始化向量（IV），AES-GCM 解密时必须使用 */
		private final byte[] iv;

		/** 加密后的密文（CipherText） */
		private final byte[] cipherText;

		/**
		 * 构造函数
		 * @param iv         初始化向量
		 * @param cipherText 密文
		 */
		public AesGcmPayload(byte[] iv, byte[] cipherText) {
			this.iv = iv;
			this.cipherText = cipherText;
		}

		/**
		 * 获取 IV
		 * @return IV
		 */
		public byte[] getIv() {
			return iv;
		}

		/**
		 * 获取密文
		 * @return 密文
		 */
		public byte[] getCipherText() {
			return cipherText;
		}

		/**
		 * 合并 IV 和 CipherText 为一个 byte[]，方便传输或存储<br>
		 * 格式：[IV | CipherText]
		 * @return IV + CipherText 合并后的字节数组
		 */
		public byte[] toBytes() {
			byte[] combined = new byte[iv.length + cipherText.length];
			System.arraycopy(iv, 0, combined, 0, iv.length);
			System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);
			return combined;
		}

		/**
		 * 解析合并的 byte[] 为 AesGcmPayload<br>
		 * 注意：假设默认 AES-GCM IV 长度为 12 字节<br>
		 * @param combined 加密输出 [IV + CipherText]
		 * @return AesGcmPayload 对象
		 */
		public static AesGcmPayload parse(byte[] combined) {
			return parse(combined, DEFAULT_GCM_IV_LENGTH);
		}

		/**
		 * 解析合并的 byte[] 为 AesGcmPayload<br>
		 * @param combined 加密输出 [IV + CipherText]
		 * @param ivLength IV长度
		 * @return AesGcmPayload 对象
		 */
		public static AesGcmPayload parse(byte[] combined, int ivLength) {
			if (combined == null || combined.length < ivLength + 1) {
				throw new IllegalArgumentException("Combined array too short");
			}
			byte[] iv = Arrays.copyOfRange(combined, 0, ivLength);
			byte[] cipherText = Arrays.copyOfRange(combined, ivLength, combined.length);
			return new AesGcmPayload(iv, cipherText);
		}
	}
}
