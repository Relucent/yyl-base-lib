package com.github.relucent.base.util.constants;

import java.math.BigInteger;

/**
 * 文件相关常量
 */
public class FileConstants {

    /** 1KB (KiloByte,千字节,千) 2^10B */
    public static final long ONE_KB = 1024L;
    /** 1MB (MegaByte,兆字节,百万) 2^20B */
    public static final long ONE_MB = ONE_KB * ONE_KB;
    /** 1GB (GigaByte,吉字节,十亿) 2^30B */
    public static final long ONE_GB = ONE_KB * ONE_MB;
    /** 1TB (TeraByte,太字节,万亿,兆) 2^40B */
    public static final long ONE_TB = ONE_KB * ONE_GB;
    /** 1PB (PetaByte,拍字节,千万亿,千兆) 2^50B */
    public static final long ONE_PB = ONE_KB * ONE_TB;
    /** 1EB (ExaByte,艾字节,百亿亿) 2^60B */
    public static final long ONE_EB = ONE_KB * ONE_PB;

    /** 1KB (KiloByte,千字节,千) 2^10B */
    public static final BigInteger ONE_KB_BI = BigInteger.valueOf(ONE_KB);
    /** 1MB (MegaByte,兆字节,百万) 2^20B */
    public static final BigInteger ONE_MB_BI = ONE_KB_BI.multiply(ONE_KB_BI);
    /** 1GB (GigaByte,吉字节,十亿) 2^30B */
    public static final BigInteger ONE_GB_BI = ONE_KB_BI.multiply(ONE_MB_BI);
    /** 1TB (TeraByte,太字节,万亿,兆) 2^40B */
    public static final BigInteger ONE_TB_BI = ONE_KB_BI.multiply(ONE_GB_BI);
    /** 1PB (PetaByte,拍字节,千万亿,千兆) 2^50B */
    public static final BigInteger ONE_PB_BI = ONE_KB_BI.multiply(ONE_TB_BI);
    /** 1EB (ExaByte,艾字节,百亿亿) 2^60B */
    public static final BigInteger ONE_EB_BI = ONE_KB_BI.multiply(ONE_PB_BI);

    /** 1ZB (ZettaByte,泽字节,十万亿亿) 2^70B */
    public static final BigInteger ONE_ZB_BI = ONE_KB_BI.multiply(ONE_EB_BI);
    /** 1YB (YottaByte,一亿亿亿字节) 2^80B */
    public static final BigInteger ONE_YB_BI = ONE_KB_BI.multiply(ONE_ZB_BI);
}
