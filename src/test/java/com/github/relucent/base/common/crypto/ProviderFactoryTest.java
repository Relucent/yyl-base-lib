package com.github.relucent.base.common.crypto;

import java.security.Provider;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.Test;

public class ProviderFactoryTest {

    @Test
    public void testGetProvider() {
        Provider provider = ProviderFactory.getProvider();
        if (ProviderFactory.isUseBouncyCastle()) {
            Assert.assertEquals(provider.getName(), BouncyCastleProvider.PROVIDER_NAME);
        }
    }
}
