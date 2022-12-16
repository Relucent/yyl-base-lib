package com.github.relucent.base.common.crypto.asymmetric;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithID;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.DSAEncoding;
import org.bouncycastle.crypto.signers.PlainDSAEncoding;
import org.bouncycastle.crypto.signers.SM2Signer;
import org.bouncycastle.crypto.signers.StandardDSAEncoding;
import org.bouncycastle.util.BigIntegers;

import com.github.relucent.base.common.codec.Hex;
import com.github.relucent.base.common.crypto.BcUtil;
import com.github.relucent.base.common.crypto.CryptoException;
import com.github.relucent.base.common.crypto.EcKeyUtil;
import com.github.relucent.base.common.lang.AssertUtil;

/**
 * 国密SM2（非对称加密算法）实现，该实现基于BC库<br>
 * SM2算法只支持公钥加密，私钥解密<br>
 * 标准号：GB/T 35276-2017 <br>
 * 中文标准名称：信息安全技术 SM2密码算法使用规范 <br>
 * 英文标准名称：Information security technology—SM2 cryptographic algorithm usage specification<br>
 * @see <a href="https://openstd.samr.gov.cn/bzgk/gb/newGbInfo?hcno=2127A9F19CB5D7F20D17D334ECA63EE5">GB/T 35276-2017</a>
 */
public class Sm2 extends AbstractAsymmetricCrypto<Sm2> {
	// =================================Fields================================================
	/** SM2算法名称 */
	private static final String ALGORITHM = "SM2";
	/** SM2公钥加密引擎 */
	private SM2Engine engine;
	/** SM2数字签名算法 */
	private SM2Signer signer;
	/** 私钥参数 */
	private ECPrivateKeyParameters privateKeyParams;
	/** 公钥参数 */
	private ECPublicKeyParameters publicKeyParams;
	/** 默认的DSA签名编码格式实现 */
	private DSAEncoding encoding = StandardDSAEncoding.INSTANCE;
	/** SM3摘要的实现类 */
	private Digest digest = new SM3Digest();
	/** SM2非对称加密的结果由C1,C2,C3三部分组成。 其中C1是生成随机数的计算出的椭圆曲线点，C2是密文数据，C3是SM3的摘要值。 最开始的国密标准的结果是按 C1C2C3 顺序的，新标准的是按C1C3C2顺序存放。 */
	private SM2Engine.Mode mode = SM2Engine.Mode.C1C3C2;
	/** 同步锁 */
	private final Lock lock = new ReentrantLock();

	// =================================Constructors===========================================
	/**
	 * 构造，生成新的私钥公钥对
	 */
	public Sm2() {
		this(null, (byte[]) null);
	}

	/**
	 * 构造 <br>
	 * 私钥和公钥同时为空时生成一对新的私钥和公钥<br>
	 * 私钥和公钥可以单独传入一个，如此则只能使用此钥匙来做加密或者解密
	 * @param privateKey 私钥，可以使用PKCS#8、D值或PKCS#1规范
	 * @param publicKey 公钥，可以使用X509、Q值或PKCS#1规范
	 */
	public Sm2(byte[] privateKey, byte[] publicKey) {
		this(EcKeyUtil.decodePrivateKeyParams(privateKey), EcKeyUtil.decodePublicKeyParams(publicKey));
	}

	/**
	 * 构造 <br>
	 * 私钥和公钥同时为空时生成一对新的私钥和公钥<br>
	 * 私钥和公钥可以单独传入一个，如此则只能使用此钥匙来做加密或者解密
	 * @param privateKey 私钥
	 * @param publicKey 公钥
	 */
	public Sm2(PrivateKey privateKey, PublicKey publicKey) {
		this(BcUtil.toParams(privateKey), BcUtil.toParams(publicKey));
		if (privateKey != null) {
			this.privateKey = privateKey;
		}
		if (publicKey != null) {
			this.publicKey = publicKey;
		}
	}

	/**
	 * 构造 <br>
	 * 私钥和公钥同时为空时生成一对新的私钥和公钥<br>
	 * 私钥和公钥可以单独传入一个，如此则只能使用此钥匙来做加密或者解密
	 * @param privateKeyHex 私钥16进制
	 * @param publicKeyPointXHex 公钥X16进制
	 * @param publicKeyPointYHex 公钥Y16进制
	 */
	public Sm2(String privateKeyHex, String publicKeyPointXHex, String publicKeyPointYHex) {
		this(BcUtil.toSm2Params(privateKeyHex), BcUtil.toSm2Params(publicKeyPointXHex, publicKeyPointYHex));
	}

	/**
	 * 构造 <br>
	 * 私钥和公钥同时为空时生成一对新的私钥和公钥<br>
	 * 私钥和公钥可以单独传入一个，如此则只能使用此钥匙来做加密或者解密
	 * @param privateKey 私钥
	 * @param publicKeyPointX 公钥X
	 * @param publicKeyPointY 公钥Y
	 */
	public Sm2(byte[] privateKey, byte[] publicKeyPointX, byte[] publicKeyPointY) {
		this(BcUtil.toSm2Params(privateKey), BcUtil.toSm2Params(publicKeyPointX, publicKeyPointY));
	}

	/**
	 * 构造 <br>
	 * 私钥和公钥同时为空时生成一对新的私钥和公钥<br>
	 * 私钥和公钥可以单独传入一个，如此则只能使用此钥匙来做加密或者解密
	 * @param privateKeyParams 私钥，可以为null
	 * @param publicKeyParams 公钥，可以为null
	 */
	public Sm2(ECPrivateKeyParameters privateKeyParams, ECPublicKeyParameters publicKeyParams) {
		super(ALGORITHM, null, null);
		this.privateKeyParams = privateKeyParams;
		this.publicKeyParams = publicKeyParams;
		this.initialize();
	}

	// =================================Methods================================================
	/**
	 * 初始化<br>
	 * 私钥和公钥同时为空时生成一对新的私钥和公钥<br>
	 * 私钥和公钥可以单独传入一个，如此则只能使用此钥匙来做加密（签名）或者解密（校验）
	 */
	public void initialize() {
		if (privateKeyParams == null && this.publicKeyParams == null) {
			// 初始化密钥对（调用父类的方法）
			super.initializeKeys();
			// 获得私钥参数与公钥参数
			this.privateKeyParams = BcUtil.toParams(this.privateKey);
			this.publicKeyParams = BcUtil.toParams(this.publicKey);
		}
	}

	/**
	 * 初始化密钥对
	 */
	@Override
	protected void initializeKeys() {
		// 阻断父类中自动生成密钥对的操作，此操作由本类中进行。
		// 实际加密解密使用的是 KeyParameters，因此不需要额外去生成Key
	}
	// =================================EncryptMethods=========================================

	/**
	 * 使用公钥加密数据<br>
	 * @param data 被加密的数据
	 * @return 加密后的数据
	 */
	public byte[] encrypt(byte[] data) {
		return encrypt(data, KeyType.PUBLIC);
	}

	/**
	 * 加密数据，SM2算法只允许使用公钥加密
	 * @param data 被加密的数据
	 * @param keyType 使用秘钥类型{@link KeyType} ，SM2只能使用公钥加密所以该参数应为{@code KeyType.PUBLIC}}
	 * @return 加密后的数据
	 */
	@Override
	public byte[] encrypt(byte[] data, KeyType keyType) {
		if (KeyType.PUBLIC != keyType) {
			throw new IllegalArgumentException("SM2: Encrypt is only support by public key");
		}
		return encrypt(data, new ParametersWithRandom(getCipherParameters(keyType)));
	}

	/**
	 * 使用公钥加密数据<br>
	 * @param data 被加密的数据
	 * @param pubKeyParameters 公钥参数
	 * @return 加密后的数据
	 */
	public byte[] encrypt(byte[] data, CipherParameters pubKeyParameters) {
		lock.lock();
		try {
			SM2Engine engine = getEngine();
			engine.init(true, pubKeyParameters);
			return engine.processBlock(data, 0, data.length);
		} catch (InvalidCipherTextException e) {
			throw new CryptoException(e);
		} finally {
			lock.unlock();
		}
	}
	// =================================DecryptMethods=========================================

	/**
	 * 使用私钥解密数据<br>
	 * @param data 密文(被解密的数据)，实际包含三部分：ECC公钥、真正的密文、公钥和原文的SM3-HASH值
	 * @return 解密后的数据
	 */
	public byte[] decrypt(byte[] data) {
		return decrypt(data, KeyType.PRIVATE);
	}

	/**
	 * 解密数据<br>
	 * @param data SM2密文，实际包含三部分：ECC公钥、真正的密文、公钥和原文的SM3-HASH值
	 * @param keyType 使用秘钥类型{@link KeyType} ，SM2只能使用私钥解密所以该参数应为{@code KeyType.PRIVATE}}
	 * @return 加密后的数据
	 * @throws CryptoException 包括InvalidKeyException和InvalidCipherTextException的包装异常
	 */
	@Override
	public byte[] decrypt(byte[] data, KeyType keyType) {
		if (KeyType.PRIVATE != keyType) {
			throw new IllegalArgumentException("SM2: Decrypt is only support by private key");
		}
		return decrypt(data, getCipherParameters(keyType));
	}

	/**
	 * 使用私钥解密数据<br>
	 * @param data 密文(被解密的数据)，实际包含三部分：ECC公钥、真正的密文、公钥和原文的SM3-HASH值
	 * @param privateKeyParameters 私钥参数
	 * @return 解密后的数据
	 */
	public byte[] decrypt(byte[] data, CipherParameters privateKeyParameters) {
		lock.lock();
		final SM2Engine engine = getEngine();
		try {
			engine.init(false, privateKeyParameters);
			return engine.processBlock(data, 0, data.length);
		} catch (InvalidCipherTextException e) {
			throw new CryptoException(e);
		} finally {
			lock.unlock();
		}
	}

	// =================================SignAndVerifyMethods===================================
	/**
	 * 用私钥对信息生成数字签名
	 * @param dataHex 被签名的数据数据
	 * @return 签名
	 */
	public String signHex(String dataHex) {
		return signHex(dataHex, null);
	}

	/**
	 * 用私钥对信息生成数字签名，签名格式为ASN1<br>
	 * @param data 加密数据
	 * @return 签名
	 */
	public byte[] sign(byte[] data) {
		return sign(data, null);
	}

	/**
	 * 用私钥对信息生成数字签名
	 * @param dataHex 被签名的数据数据
	 * @param idHex 可以为null，若为null，则默认withId为字节数组:"1234567812345678".getBytes()
	 * @return 签名
	 */
	public String signHex(String dataHex, String idHex) {
		return Hex.encodeHexString(sign(Hex.decodeHex(dataHex), Hex.decodeHex(idHex)));
	}

	/**
	 * 用私钥对信息生成数字签名，签名格式为ASN1<br>
	 * @param data 被签名的数据数据
	 * @param id 可以为null，若为null，则默认withId为字节数组:"1234567812345678".getBytes()
	 * @return 签名
	 */
	public byte[] sign(byte[] data, byte[] id) {
		lock.lock();
		final SM2Signer signer = getSigner();
		try {
			CipherParameters param = new ParametersWithRandom(getCipherParameters(KeyType.PRIVATE));
			if (id != null) {
				param = new ParametersWithID(param, id);
			}
			signer.init(true, param);
			signer.update(data, 0, data.length);
			return signer.generateSignature();
		} catch (org.bouncycastle.crypto.CryptoException e) {
			throw new CryptoException(e);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 用公钥检验数字签名的合法性
	 * @param dataHex 数据签名后的数据
	 * @param signHex 签名
	 * @return 是否验证通过
	 */
	public boolean verifyHex(String dataHex, String signHex) {
		return verifyHex(dataHex, signHex, null);
	}

	/**
	 * 用公钥检验数字签名的合法性
	 * @param data 签名后的数据
	 * @param sign 签名
	 * @return 是否验证通过
	 */
	public boolean verify(byte[] data, byte[] sign) {
		return verify(data, sign, null);
	}

	/**
	 * 用公钥检验数字签名的合法性
	 * @param dataHex 数据签名后的数据的Hex值
	 * @param signHex 签名的Hex值
	 * @param idHex ID的Hex值
	 * @return 是否验证通过
	 */
	public boolean verifyHex(String dataHex, String signHex, String idHex) {
		return verify(Hex.decodeHex(dataHex), Hex.decodeHex(signHex), Hex.decodeHex(idHex));
	}

	/**
	 * 用公钥检验数字签名的合法性
	 * @param data 数据签名后的数据
	 * @param sign 签名
	 * @param id 可以为null，若为null，则默认withId为字节数组:"1234567812345678".getBytes()
	 * @return 是否验证通过
	 */
	public boolean verify(byte[] data, byte[] sign, byte[] id) {
		lock.lock();
		final SM2Signer signer = getSigner();
		try {
			CipherParameters param = getCipherParameters(KeyType.PUBLIC);
			if (id != null) {
				param = new ParametersWithID(param, id);
			}
			signer.init(false, param);
			signer.update(data, 0, data.length);
			return signer.verifySignature(sign);
		} finally {
			lock.unlock();
		}
	}

	// =================================SettingMethods=========================================
	/**
	 * 设置私钥
	 * @param privateKey 私钥
	 * @return this
	 */
	@Override
	public Sm2 setPrivateKey(PrivateKey privateKey) {
		super.setPrivateKey(privateKey);

		// 重新初始化密钥参数，防止重新设置密钥时导致密钥无法更新
		this.privateKeyParams = BcUtil.toParams(privateKey);

		return this;
	}

	/**
	 * 设置私钥参数
	 * @param privateKeyParams 私钥参数
	 * @return this
	 */
	public Sm2 setPrivateKeyParams(ECPrivateKeyParameters privateKeyParams) {
		this.privateKeyParams = privateKeyParams;
		return this;
	}

	/**
	 * 设置公钥
	 * @param publicKey 公钥
	 * @return this
	 */
	@Override
	public Sm2 setPublicKey(PublicKey publicKey) {
		super.setPublicKey(publicKey);

		// 重新初始化密钥参数，防止重新设置密钥时导致密钥无法更新
		this.publicKeyParams = BcUtil.toParams(publicKey);

		return this;
	}

	/**
	 * 设置公钥参数
	 * @param publicKeyParams 公钥参数
	 * @return this
	 */
	public Sm2 setPublicKeyParams(ECPublicKeyParameters publicKeyParams) {
		this.publicKeyParams = publicKeyParams;
		return this;
	}

	/**
	 * 设置DSA signatures的编码为PlainDSAEncoding
	 * @return this
	 */
	public Sm2 usePlainEncoding() {
		return setEncoding(PlainDSAEncoding.INSTANCE);
	}

	/**
	 * 设置DSA signatures的编码
	 * @param encoding {@link DSAEncoding}实现
	 * @return this
	 */
	public Sm2 setEncoding(DSAEncoding encoding) {
		this.encoding = encoding;
		this.signer = null;
		return this;
	}

	/**
	 * 设置摘要算法
	 * @param digest {@link Digest}实现
	 * @return this
	 */
	public Sm2 setDigest(Digest digest) {
		this.digest = digest;
		this.engine = null;
		this.signer = null;
		return this;
	}

	/**
	 * 设置SM2模式，最开始的国密标准的结果是按 C1C2C3 顺序的， 新标准的是按C1C3C2顺序存放
	 * @param mode {@link SM2Engine.Mode}
	 * @return this
	 */
	public Sm2 setMode(SM2Engine.Mode mode) {
		this.mode = mode;
		this.engine = null;
		return this;
	}

	/**
	 * 获得私钥D值（编码后的私钥）
	 * @return D值
	 */
	public byte[] getD() {
		return BigIntegers.asUnsignedByteArray(getDBigInteger());
	}

	/**
	 * 获得私钥D值（编码后的私钥）
	 * @return D值
	 */
	public String getDHex() {
		return String.format("%064x", new BigInteger(1, getD()));
	}

	/**
	 * 获得私钥D值
	 * @return D值
	 */
	public BigInteger getDBigInteger() {
		return this.privateKeyParams.getD();
	}

	/**
	 * 获得公钥Q值（编码后的公钥）
	 * @param isCompressed 是否压缩
	 * @return Q值
	 */
	public byte[] getQ(boolean isCompressed) {
		return this.publicKeyParams.getQ().getEncoded(isCompressed);
	}

	// =================================ToolMethods============================================
	/**
	 * 获取密钥类型对应的加密参数对象{@link CipherParameters}
	 * @param keyType Key类型枚举，包括私钥或公钥
	 * @return {@link CipherParameters}
	 */
	private CipherParameters getCipherParameters(KeyType keyType) {
		switch (keyType) {
		case PUBLIC:
			AssertUtil.notNull(this.publicKeyParams, "PublicKey must be not null !");
			return this.publicKeyParams;
		case PRIVATE:
			AssertUtil.notNull(this.privateKeyParams, "PrivateKey must be not null !");
			return this.privateKeyParams;
		}
		return null;
	}

	/**
	 * 获取{@link SM2Engine}，此对象为懒加载模式
	 * @return {@link SM2Engine}
	 */
	private SM2Engine getEngine() {
		if (this.engine == null) {
			AssertUtil.notNull(this.digest, "digest must be not null !");
			this.engine = new SM2Engine(this.digest, this.mode);
		}
		this.digest.reset();
		return this.engine;
	}

	/**
	 * 获取{@link SM2Signer}，此对象为懒加载模式
	 * @return {@link SM2Signer}
	 */
	private SM2Signer getSigner() {
		if (this.signer == null) {
			AssertUtil.notNull(this.digest, "digest must be not null !");
			this.signer = new SM2Signer(this.encoding, this.digest);
		}
		this.digest.reset();
		return this.signer;
	}
}
