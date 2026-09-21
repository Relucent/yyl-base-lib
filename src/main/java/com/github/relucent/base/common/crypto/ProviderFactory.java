package com.github.relucent.base.common.crypto;

import java.security.Provider;
import java.security.Security;
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
        // BouncyCastle就是一个提供了很多哈希算法和加密算法的第三方库，它提供了Java标准库没有的一些算法。
        try {
            // provider = new org.bouncycastle.jce.provider.BouncyCastleProvider();
            Class<?> clazz = Class.forName("org.bouncycastle.jce.provider.BouncyCastleProvider");
            Provider provider = (Provider) clazz.getDeclaredConstructor().newInstance();
            Security.addProvider(provider);
            PROVIDER.set(provider);
            IS_USE_BOUNCY_CASTLE.set(provider != null);
        } catch (Throwable e) {
            // ignore
        }
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

    /**
     * 确保 ProviderFactory 已完成初始化，并在 BouncyCastle 存在时尝试注册该 Provider。 <br>
     * 该方法本身无需执行其他操作，调用静态方法会触发 ProviderFactory 的类初始化。 <br>
     * BouncyCastle 为可选依赖，不存在时不会影响类库正常使用。<br>
     */
    public static void ensureInitialized() {
        /* Intentionally empty. The purpose of this method is to trigger class initialization. */
    }

}
