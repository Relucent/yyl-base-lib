package com.github.relucent.base.util.net;

import org.junit.Assert;
import org.junit.Test;

public class NetworkHelperTest {
    @Test
    public void testIsValidPort() {
        Assert.assertFalse(NetworkHelper.isValidPort(-1));
        Assert.assertFalse(NetworkHelper.isValidPort(0));
        Assert.assertFalse(NetworkHelper.isValidPort(65536));
        for (int i = 1; i <= 65535; i++) {
            Assert.assertTrue(NetworkHelper.isValidPort(i));
        }
    }
}
