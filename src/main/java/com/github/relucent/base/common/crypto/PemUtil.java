package com.github.relucent.base.common.crypto;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemObjectGenerator;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

import com.github.relucent.base.common.constant.CharsetConstant;
import com.github.relucent.base.common.crypto.asymmetric.KeyUtil;
import com.github.relucent.base.common.io.IoRuntimeException;
import com.github.relucent.base.common.io.IoUtil;
import com.github.relucent.base.common.lang.StringUtil;

/**
 * PEM(Privacy Enhanced Mail)格式相关工具类。（基于Bouncy Castle）<br>
 * PEM一般为文本格式，以 -----BEGIN... 开头，以 -----END... 结尾，中间的内容是 BASE64 编码。<br>
 * 这种格式可以保存证书和私钥，有时我们也把PEM格式的私钥的后缀改为 .key 以区别证书与私钥。<br>
 */
public class PemUtil {

	/**
	 * 读取PEM格式的私钥
	 * @param pemStream PEM流
	 * @return {@link PrivateKey}
	 */
	public static PrivateKey readPemPrivateKey(InputStream pemStream) {
		return (PrivateKey) readPemKey(pemStream);
    }

	/**
	 * 读取PEM格式的公钥
	 * @param pemStream PEM流
	 * @return {@link PublicKey}
	 */
	public static PublicKey readPemPublicKey(InputStream pemStream) {
		return (PublicKey) readPemKey(pemStream);
	}

	/**
	 * 从PEM文件中读取公钥或私钥， 根据类型返回 {@link PublicKey} 或者 {@link PrivateKey}
	 * @param keyStream PEM流
	 * @return {@link Key}，null表示无法识别的密钥类型
	 */
	public static Key readPemKey(InputStream keyStream) {
		final PemObject object = readPemObject(keyStream);
		final String type = object.getType();
		if (StringUtil.isNotBlank(type)) {
			if (type.endsWith("PRIVATE KEY")) {
				return KeyUtil.generateRSAPrivateKey(object.getContent());
			} else if (type.endsWith("PUBLIC KEY")) {
				return KeyUtil.generateRSAPublicKey(object.getContent());
			} else if (type.endsWith("CERTIFICATE")) {
				return KeyUtil.readPublicKeyFromCert(new ByteArrayInputStream(object.getContent()));
			}
		}
		// 表示无法识别的密钥类型
		return null;
	}

	/**
	 * 从PEM流中读取公钥或私钥
	 * @param keyStream PEM流
	 * @return 密钥（公钥或私钥）内容
	 */
	public static byte[] readPem(InputStream keyStream) {
		PemObject pemObject = readPemObject(keyStream);
		if (null != pemObject) {
			return pemObject.getContent();
		}
		return null;
	}

	/**
	 * 读取PEM文件中的信息，包括类型、头信息和密钥内容
	 * @param keyStream PEM 字符流
	 * @return {@link PemObject}
	 */
	public static PemObject readPemObject(InputStream keyStream) {
		return readPemObject(IoUtil.toReader(keyStream, CharsetConstant.UTF_8));
	}

	/**
	 * 读取PEM文件中的信息，包括类型、头信息和密钥内容
	 * @param reader PEM 字符流
	 * @return {@link PemObject}
	 */
	public static PemObject readPemObject(Reader reader) {
		PemReader pemReader = null;
		try {
			pemReader = new PemReader(reader);
			return pemReader.readPemObject();
		} catch (IOException e) {
			throw new IoRuntimeException(e);
		} finally {
			IoUtil.closeQuietly(pemReader);
		}
	}

	/**
	 * 写出PEM密钥（私钥、公钥、证书）
	 * @param type 密钥类型（私钥、公钥、证书）
	 * @param content 密钥内容
	 * @param keyStream PEM 输出流
	 */
	public static void writePemObject(String type, byte[] content, OutputStream keyStream) {
		writePemObject(new PemObject(type, content), keyStream);
	}

	/**
	 * 写出PEM密钥 （私钥、公钥、证书）
	 * @param pemObject PEM对象，包括密钥和密钥类型等信息
	 * @param keyStream PEM流
	 */
	public static void writePemObject(PemObjectGenerator pemObject, OutputStream keyStream) {
		PemWriter writer = null;
		try {
			writer = new PemWriter(IoUtil.toWriter(keyStream, CharsetConstant.UTF_8));
			writer.writeObject(pemObject);
		} catch (IOException e) {
			throw new IoRuntimeException(e);
		} finally {
			IoUtil.closeQuietly(writer);
		}
	}
}
