package com.github.relucent.base.common.crypto;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;

/**
 * SM国密算法工具类
 */
public class SmUtil {
	/**
	 * SM2默认曲线
	 */
	public static final String SM2_CURVE_NAME = "sm2p256v1";
	/**
	 * SM2推荐曲线参数
	 */
	public static final ECDomainParameters SM2_DOMAIN_PARAMS = toDomainParams(GMNamedCurves.getByName(SM2_CURVE_NAME));

	/**
	 * SM2国密算法公钥参数的Oid标识
	 */
	public static final ASN1ObjectIdentifier ID_SM2_PUBLIC_KEY_PARAM = new ASN1ObjectIdentifier("1.2.156.10197.1.301");

	/**
	 * 构建ECDomainParameters对象
	 * @param x9ECParameters {@link X9ECParameters}
	 * @return {@link ECDomainParameters}
	 */
	public static ECDomainParameters toDomainParams(X9ECParameters x9ECParameters) {
		return new ECDomainParameters(x9ECParameters.getCurve(), x9ECParameters.getG(), x9ECParameters.getN(), x9ECParameters.getH());
	}
}
