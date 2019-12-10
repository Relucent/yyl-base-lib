package com.github.relucent.base.util.net;

import org.junit.Assert;
import org.junit.Test;

public class NetworkHelperTest {

    @Test
    public void testIsValidIPv4() {
        Assert.assertFalse(NetworkHelper.isValidIPv4("0.0.0.0"));
        Assert.assertFalse(NetworkHelper.isValidIPv4("127.0.0.1"));
        Assert.assertFalse(NetworkHelper.isValidIPv4("255.255.255.255"));
        Assert.assertFalse(NetworkHelper.isValidIPv4("100.200.400.400"));
        Assert.assertFalse(NetworkHelper.isValidIPv4("a.b.c.d"));
        Assert.assertFalse(NetworkHelper.isValidIPv4("192.168.0"));
        Assert.assertTrue(NetworkHelper.isValidIPv4("192.168.0.1"));
        Assert.assertTrue(NetworkHelper.isValidIPv4("10.1.1.1"));
    }

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
