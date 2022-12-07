package com.github.relucent.base.common.crypto.symmetric;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import com.github.relucent.base.common.crypto.ModeEnum;
import com.github.relucent.base.common.crypto.PaddingEnum;

/**
 * 标准号：GB/T 32907-2016 <br>
 * 中文标准名称：信息安全技术 SM4分组密码算法 <br>
 * 英文标准名称：Information security technology—SM4 block cipher algorthm <br>
 * @see <a href="https://openstd.samr.gov.cn/bzgk/gb/newGbInfo?hcno=7803DE42D3BC5E80B0C3E5D8E873D56A">GB/T 32907-2016</a>
 */
public class Sm4 extends SymmetricCrypto {

	// =================================Fields================================================
	private static final String ALGORITHM_PREFIX = "SM4";

	// =================================Constructors===========================================
	/**
	 * 构造函数
	 * @param secretKey 秘密(对称)密钥，如果为null，表示使用随机密钥
	 */
	protected Sm4(SecretKey secretKey) {
		super(SymmetricAlgorithmEnum.SM4, secretKey);
	}

	/**
	 * 构造函数
	 * @param key 密钥，密钥长度：128(16字节)；如果为null，表示使用随机密钥
	 */
	protected Sm4(byte[] key) {
		super(SymmetricAlgorithmEnum.SM4, key);
	}

	/**
	 * 构造函数
	 * @param mode 模式{@link ModeEnum}
	 * @param padding {@link PaddingEnum}补码方式
	 * @param secretKey 秘密(对称)密钥
	 * @param parameterSpec 算法参数(初始化向量)
	 */
	protected Sm4(ModeEnum mode, PaddingEnum padding, SecretKey secretKey, IvParameterSpec parameterSpec) {
		super(ALGORITHM_PREFIX + "/" + mode.name() + "/" + padding.name(), secretKey, parameterSpec);
	}

	// =================================CreateMethods==========================================
	/**
	 * 创建 SM4 实例，随机秘钥
	 * @return SM4实例
	 */
	public static Sm4 create() {
		return create((SecretKey) null);
	}

	/**
	 * 创建SM4实例
	 * @param secretKey 秘密(对称)密钥
	 * @return SM4实例
	 */
	public static Sm4 create(SecretKey secretKey) {
		return new Sm4(secretKey);
	}

	/**
	 * 创建SM4实例
	 * @param key 密钥，密钥长度：128(16字节)；如果为null，表示使用随机密钥
	 * @return SM4实例
	 */
	public static Sm4 create(byte[] key) {
		return new Sm4(key);
	}

	/**
	 * 创建SM4实例
	 * @param mode 模式 {@link ModeEnum}
	 * @param padding {@link PaddingEnum}补码方式
	 * @param secretKey 秘密(对称)密钥
	 * @param paramsSpec 加密参数的(初始化向量)
	 * @return SM4实例
	 */
	public static Sm4 create(ModeEnum mode, PaddingEnum padding, SecretKey secretKey, IvParameterSpec paramsSpec) {
		return new Sm4(mode, padding, secretKey, paramsSpec);
	}

	// =================================Methods================================================
	/**
	 * 设置偏移向量
	 * @param iv 偏移向量(加盐)
	 * @return this
	 */
	public Sm4 setIvParameter(byte[] iv) {
		setParameter(new IvParameterSpec(iv));
		return this;
	}
}
