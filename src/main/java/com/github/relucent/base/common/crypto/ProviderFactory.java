package com.github.relucent.base.common.crypto;

import java.security.Provider;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * {@link Provider}简单工厂类
 */
public class ProviderFactory {

    /** 默认的{@link Provider} */
    private static final AtomicReference<Provider> PROVIDER = new AtomicReference<>();
    /** 是否使用了 BouncyCastle */
    private static final AtomicBoolean IS_USE_BOUNCY_CASTLE = new AtomicBoolean();

    static {
        Provider provider = null;
        // BouncyCastle就是一个提供了很多哈希算法和加密算法的第三方库，它提供了Java标准库没有的一些算法。
        try {
            provider = new org.bouncycastle.jce.provider.BouncyCastleProvider();
        } catch (Throwable e) {
            // ignore
        }
        PROVIDER.set(provider);
        ;
        IS_USE_BOUNCY_CASTLE.set(provider != null);
    }

    /**
     * 获取默认{@link Provider}<br>
     * 如果系统引用了Bouncy castle（轻量级密码术包），会尝试加载BouncyCastle库的Provider， 否则返回null(表示使用JDK默认 )<br>
     * @return {@link Provider}
     */
    public static Provider getProvider() {
        return PROVIDER.get();
    }

    /**
     * 是否使用 Bouncy castle（轻量级密码术包）
     * @return 如果使用了Bouncy castle库返回true，否则返回false
     */
    public static boolean isUseBouncyCastle() {
        return IS_USE_BOUNCY_CASTLE.get();
    }

    /**
     * 设置{@link Provider}
     * @param provider {@link Provider}
     */
    protected static void setProvider(Provider provider) {
        PROVIDER.set(provider);
    }
}
