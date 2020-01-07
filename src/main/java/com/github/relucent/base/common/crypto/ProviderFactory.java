package com.github.relucent.base.common.crypto;

import java.security.Provider;

/**
 * {@link Provider}简单工厂类
 */
public class ProviderFactory {

    /** 默认的{@link Provider} */
    private static volatile Provider PROVIDER;

    static {
        Provider provider = null;
        // BouncyCastle就是一个提供了很多哈希算法和加密算法的第三方库，它提供了Java标准库没有的一些算法。
        try {
            provider = new org.bouncycastle.jce.provider.BouncyCastleProvider();
        } catch (Error e) {
            // ignore
        }
        PROVIDER = provider;
    }

    /**
     * 获取默认{@link Provider}<br>
     * 如果系统引用了Bouncy castle（轻量级密码术包），会尝试加载BouncyCastle库的Provider， 否则返回null(表示使用JDK默认 )<br>
     * @return {@link Provider}
     */
    public static Provider getProvider() {
        return PROVIDER;
    }

    /**
     * 设置{@link Provider}
     * @param provider {@link Provider}
     */
    protected static void setProvider(Provider provider) {
        PROVIDER = provider;
    }
}
