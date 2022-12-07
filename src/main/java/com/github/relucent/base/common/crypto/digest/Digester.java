package com.github.relucent.base.common.crypto.digest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;

import com.github.relucent.base.common.codec.Hex;
import com.github.relucent.base.common.constant.IoConstant;
import com.github.relucent.base.common.crypto.CryptoException;
import com.github.relucent.base.common.crypto.ProviderFactory;

/**
 * 消息摘要算法 (Message-Digest Algorithm)工具类<br>
 * 此类为应用程序提供信息摘要算法的功能，如 MD5或 SHA算法。<br>
 * 注意：该类的实例不保证线程安全，应当避免多线程同时调用同一个实例(每个线程使用独立的实例，或者在调用时候增加同步锁)。<br>
 */
public class Digester {

	// =================================Fields================================================
	/** 算法名称 */
	protected String algorithm;
	/** 摘要算法的功能类(该对象非线程安全) */
	protected MessageDigest messageDigest;
	/** 盐值 */
	protected byte[] salt;
	/** 加盐位置，默认0 */
	protected int saltPosition;
	/** 散列次数 */
	protected int digestCount;

	// =================================Constructors===========================================
	/**
	 * 构造函数
	 * @param algorithm 算法
	 */
	public Digester(DigestAlgorithm algorithm) {
		this(algorithm.getValue(), null, 0, 0, null);
	}

	/**
	 * 构造函数
	 * @param algorithm 算法
	 * @param provider 算法提供者，null表示JDK默认，可以引入第三方包(例如BouncyCastle)提供更多算法支持
	 */
	public Digester(DigestAlgorithm algorithm, Provider provider) {
		this(algorithm.getValue(), null, 0, 0, provider);
	}

	/**
	 * 构造函数
	 * @param algorithm 算法
	 * @param salt 盐值 (默认加盐位置在头部)
	 */
	public Digester(DigestAlgorithm algorithm, byte[] salt) {
		this(algorithm.getValue(), salt, 0, 1, null);
	}

	/**
	 * 构造函数
	 * @param algorithm 算法
	 * @param salt 盐值
	 * @param saltPosition 加盐位置，既将盐值字符串放置在数据的index数，默认0
	 * @param digestCount 摘要次数，当此值小于等于1,默认为1。
	 * @param provider 算法提供者，null表示JDK默认，可以引入第三方包(例如BouncyCastle)提供更多算法支持
	 */
	protected Digester(DigestAlgorithm algorithm, byte[] salt, int saltPosition, int digestCount, Provider provider) {
		this(algorithm.getValue(), salt, saltPosition, digestCount, provider);
	}

	/**
	 * 构造函数
	 * @param algorithm 算法
	 * @param salt 盐值
	 * @param saltPosition 加盐位置，既将盐值字符串放置在数据的index数，默认0
	 * @param digestCount 摘要次数，当此值小于等于1,默认为1。
	 * @param provider 算法提供者，null表示JDK默认，可以引入第三方包(例如BouncyCastle)提供更多算法支持
	 */
	protected Digester(String algorithm, byte[] salt, int saltPosition, int digestCount, Provider provider) {
		try {
			if (provider == null) {
				provider = ProviderFactory.getProvider();
			}
			if (provider == null) {
				messageDigest = MessageDigest.getInstance(algorithm);
			} else {
				messageDigest = MessageDigest.getInstance(algorithm, provider);
			}
		} catch (NoSuchAlgorithmException e) {
			throw new CryptoException(e);
		}
		this.salt = salt;
		this.saltPosition = saltPosition;
		this.digestCount = digestCount;
	}

	// =================================Methods================================================
	/**
	 * 生成数据的摘要
	 * @param input 被摘要数据
	 * @return 摘要字节数组
	 */
	public byte[] digest(String input) {
		return digest(input, StandardCharsets.UTF_8);
	}

	/**
	 * 生成摘要
	 * @param input 被摘要数据
	 * @param charsetName 编码
	 * @return 摘要字节数组
	 */
	public byte[] digest(String input, String charsetName) {
		return digest(input, Charset.forName(charsetName));
	}

	/**
	 * 生成数据的摘要
	 * @param input 被摘要数据
	 * @param charset 摘要数据
	 * @return 摘要字节数组
	 */
	public byte[] digest(String input, Charset charset) {
		return digest(input.getBytes(charset));
	}

	/**
	 * 生成数据的摘要，并转为16进制字符串
	 * @param input 被摘要数据
	 * @return 摘要16进制字符串
	 */
	public String digestHex(String input) {
		return digestHex(input, StandardCharsets.UTF_8);
	}

	/**
	 * 生成数据的摘要，并转为16进制字符串
	 * @param input 被摘要数据
	 * @param charsetName 编码
	 * @return 摘要16进制字符串
	 */
	public String digestHex(String input, String charsetName) {
		return digestHex(input, Charset.forName(charsetName));
	}

	/**
	 * 生成数据的摘要，并转为16进制字符串
	 * @param input 被摘要数据
	 * @param charset 编码
	 * @return 摘要
	 */
	public String digestHex(String input, Charset charset) {
		byte[] data = input.getBytes(charset);
		return digestHex(data);
	}

	/**
	 * 生成摘要，并转为16进制字符串<br>
	 * @param input 被摘要数据
	 * @return 摘要
	 */
	public String digestHex(byte[] input) {
		byte[] hash = digest(input);
		return Hex.encodeHexString(hash);
	}

	/**
	 * 生成摘要
	 * @param input 输入字节数组
	 * @return 摘要字节数组
	 */
	public byte[] digest(byte[] input) {
		// 使用指定的字节数组更新摘要
		doUpdate(input);
		// 来完成哈希计算。
		byte[] hash = digestAndReset();
		// 重复计算
		return doRepeatDigest(hash);
	}

	/**
	 * 生成摘要
	 * @param input {@link InputStream} 输入数据流
	 * @return 摘要字节数组
	 * @throws IOException 出现IO异常时抛出
	 */
	public byte[] digest(InputStream input) throws IOException {
		// 使用指定的输入数据流更新摘要
		doUpdate(input);
		// 来完成哈希计算。
		byte[] hash = digestAndReset();
		// 重复计算
		return doRepeatDigest(hash);
	}

	/**
	 * 使用指定的字节数组更新摘要
	 * @param input 字节数组
	 */
	private void doUpdate(byte[] input) {
		// 无加盐
		if (salt == null || salt.length == 0) {
			messageDigest.update(input);
		}
		// 加盐在开头
		else if (saltPosition <= 0) {
			messageDigest.update(salt);
			messageDigest.update(input);
		}
		// 加盐在末尾
		else if (saltPosition >= input.length) {
			messageDigest.update(input);
			messageDigest.update(salt);
		}
		// 加盐在中间
		else {
			messageDigest.update(input, 0, saltPosition);
			messageDigest.update(salt);
			messageDigest.update(input, saltPosition, input.length - saltPosition);
		}
	}

	/**
	 * 使用指定输入数据流更新摘要
	 * @param input {@link InputStream} 输入数据流
	 * @return 摘要字节数组
	 * @throws IOException 出现IO异常时抛出
	 */
	private void doUpdate(InputStream input) throws IOException {
		byte[] buffer = new byte[IoConstant.DEFAULT_BUFFER_SIZE];
		// 无加盐
		if (salt == null || salt.length == 0) {
			int n = 0;
			while (IoConstant.EOF != (n = input.read(buffer))) {
				messageDigest.update(buffer, 0, n);
			}
		}
		// 有加盐
		else {
			long count = 0;
			int n = 0;

			// 加盐在开头
			if (saltPosition < 0) {
				messageDigest.update(salt);
			}

			// 加盐在中间
			while (IoConstant.EOF != (n = input.read(buffer))) {
				if (count <= saltPosition && saltPosition < count + n) {
					int offset = (int) (saltPosition - count);
					if (offset != 0) {
						messageDigest.update(buffer, 0, offset);
					}
					messageDigest.update(salt);
					messageDigest.update(buffer, offset, n - offset);
				} else {
					messageDigest.update(buffer, 0, n);
				}
				count += n;
			}

			// 加盐在末尾
			if (count < saltPosition) {
				messageDigest.update(salt);
			}
		}
	}

	/**
	 * 重复计算摘要，取决于{@link #digestCount} 值<br>
	 * 每次计算摘要前都会重置{@link #messageDigest}
	 * @param input 第一次的摘要数据
	 * @return 摘要字节数组
	 */
	private byte[] doRepeatDigest(byte[] input) {
		for (int i = 1; i < digestCount; i++) {
			messageDigest.update(input);
			input = digestAndReset();
		}
		return input;
	}

	/**
	 * 来完成哈希计算，并重置摘要。
	 * @return
	 */
	private byte[] digestAndReset() {
		try {
			return messageDigest.digest();
		} finally {
			messageDigest.reset();
		}
	}

	// =================================SetMethods=============================================
	/**
	 * 设置加盐内容
	 * @param salt 盐值
	 * @return this
	 */
	public Digester setSalt(byte[] salt) {
		this.salt = salt;
		return this;
	}

	/**
	 * 设置加盐的位置，只有盐值存在时有效<br>
	 * @param saltPosition 盐的位置
	 * @return this
	 */
	public Digester setSaltPosition(int saltPosition) {
		this.saltPosition = saltPosition;
		return this;
	}

	/**
	 * 设置重复计算摘要值次数
	 * @param digestCount 摘要值次数
	 * @return this
	 */
	public Digester setDigestCount(int digestCount) {
		this.digestCount = digestCount;
		return this;
	}

	// =================================GetMethods=============================================
	/**
	 * 返回算法名称(字符串表示)
	 * @return 算法名称
	 */
	public String getAlgorithm() {
		return algorithm;
	}

	/**
	 * 获得 {@link MessageDigest}
	 * @return {@link MessageDigest}
	 */
	public MessageDigest getMessageDigest() {
		return messageDigest;
	}

	/**
	 * 获取散列长度，0表示不支持此方法
	 * @return 散列长度，0表示不支持此方法
	 */
	public int getDigestLength() {
		return messageDigest.getDigestLength();
	}
}
