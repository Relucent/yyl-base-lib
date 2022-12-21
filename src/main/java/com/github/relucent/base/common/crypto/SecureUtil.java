package com.github.relucent.base.common.crypto;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Provider;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import com.github.relucent.base.common.io.FileUtil;

/**
 * 安全相关工具类
 */
public class SecureUtil {

	/**
	 * Java密钥库(Java Key Store，JKS)KEY_STORE
	 */
	public static final String KEY_TYPE_JKS = "JKS";
	/**
     * PKCS12是公钥加密标准，它规定了可包含所有私钥、公钥和证书。其以二进制格式存储，也称为 PFX 文件
     */
	public static final String KEY_TYPE_PKCS12 = "pkcs12";
	/**
	 * Certification类型：X.509
	 */
	public static final String CERT_TYPE_X509 = "X.509";

	/**
	 * 读取密钥库(Java Key Store，JKS) KeyStore文件<br>
	 * KeyStore文件用于数字证书的密钥对保存<br>
	 * @param keyFile 证书文件
	 * @param password 密码
	 * @return {@link KeyStore}
	 */
	public static KeyStore readJKSKeyStore(File keyFile, char[] password) {
		return readKeyStore(KEY_TYPE_JKS, keyFile, password);
	}

	/**
	 * 读取密钥库(Java Key Store，JKS) KeyStore文件<br>
	 * KeyStore文件用于数字证书的密钥对保存<br>
	 * @param input 数据流
	 * @param password 密码
	 * @return {@link KeyStore}
	 */
	public static KeyStore readJKSKeyStore(InputStream input, char[] password) {
		return readKeyStore(KEY_TYPE_JKS, input, password);
	}

	/**
	 * 读取PKCS12 KeyStore文件<br>
	 * KeyStore文件用于数字证书的密钥对保存
	 * @param keyFile 证书文件
	 * @param password 密码
	 * @return {@link KeyStore}
	 */
	public static KeyStore readPKCS12KeyStore(File keyFile, char[] password) {
		return readKeyStore(KEY_TYPE_PKCS12, keyFile, password);
	}

	/**
	 * 读取PKCS12 KeyStore文件， KeyStore文件用于数字证书的密钥对保存
	 * @param input 数据流
	 * @param password 密码
	 * @return {@link KeyStore}
	 */
	public static KeyStore readPKCS12KeyStore(InputStream input, char[] password) {
		return readKeyStore(KEY_TYPE_PKCS12, input, password);
	}

	/**
	 * 读取KeyStore文件， KeyStore文件用于数字证书的密钥对保存
	 * @param type 类型
	 * @param keyFile 证书文件
	 * @param password 密码，null表示无密码
	 * @return {@link KeyStore}
	 */
	public static KeyStore readKeyStore(String type, File keyFile, char[] password) {
		try (InputStream input = FileUtil.openInputStream(keyFile)) {
			return readKeyStore(type, input, password);
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * 读取KeyStore文件， KeyStore文件用于数字证书的密钥对保存
	 * @param type 类型
	 * @param input 数据流
	 * @param password 密码，null表示无密码
	 * @return {@link KeyStore}
	 */
	public static KeyStore readKeyStore(String type, InputStream input, char[] password) {
		KeyStore keyStore;
		try {
			keyStore = KeyStore.getInstance(type);
			keyStore.load(input, password);
		} catch (Exception e) {
			throw new CryptoException(e);
		}
		return keyStore;
	}

	/**
	 * 读取X.509 证书文件
	 * @param input 证书的数据流
	 * @return X.509 证书
	 */
	public static Certificate readX509Certificate(InputStream input) {
		return readCertificate(CERT_TYPE_X509, input);
	}

	/**
	 * 读取证书文件
	 * @param type 类型，例如X.509
	 * @param input 证书的数据流
	 * @param password 密码
	 * @param alias 别名
	 * @return {@link Certificate}
	 */
	public static Certificate readCertificate(String type, InputStream input, char[] password, String alias) {
		final KeyStore keyStore = readKeyStore(type, input, password);
		try {
			return keyStore.getCertificate(alias);
		} catch (KeyStoreException e) {
			throw new CryptoException(e);
		}
	}

	/**
	 * 读取Certification文件<br>
	 * Certification为证书文件<br>
	 * @param type 类型，例如X.509
	 * @param input 证书的数据流
	 * @return {@link Certificate}
	 */
	public static Certificate readCertificate(String type, InputStream input) {
		try {
			return getCertificateFactory(type).generateCertificate(input);
		} catch (CertificateException e) {
			throw new CryptoException(e);
		}
	}

	/**
	 * 获得 Certification
	 * @param keyStore {@link KeyStore}
	 * @param alias 别名
	 * @return {@link Certificate}
	 */
	public static Certificate getCertificate(KeyStore keyStore, String alias) {
		try {
			return keyStore.getCertificate(alias);
		} catch (Exception e) {
			throw new CryptoException(e);
		}
	}

	/**
	 * 获取{@link CertificateFactory}
	 * @param type 类型，例如X.509
	 * @return {@link CertificateFactory}
	 */
	public static CertificateFactory getCertificateFactory(String type) {
		final Provider provider = ProviderFactory.getProvider();
		try {
			if (provider == null) {
				return CertificateFactory.getInstance(type);
			}
			return CertificateFactory.getInstance(type, provider);
		} catch (CertificateException e) {
			throw new CryptoException(e);
		}
	}
}
