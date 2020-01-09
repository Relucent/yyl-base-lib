package com.github.relucent.base.common.net;

import org.junit.Assert;
import org.junit.Test;

import com.github.relucent.base.common.net.NetworkUtil;

public class NetworkUtilTest {

    @Test
    public void testIsValidIPv4() {
        Assert.assertFalse(NetworkUtil.isValidIPv4("0.0.0.0"));
        Assert.assertFalse(NetworkUtil.isValidIPv4("127.0.0.1"));
        Assert.assertFalse(NetworkUtil.isValidIPv4("255.255.255.255"));
        Assert.assertFalse(NetworkUtil.isValidIPv4("100.200.400.400"));
        Assert.assertFalse(NetworkUtil.isValidIPv4("a.b.c.d"));
        Assert.assertFalse(NetworkUtil.isValidIPv4("192.168.0"));
        Assert.assertTrue(NetworkUtil.isValidIPv4("192.168.0.1"));
        Assert.assertTrue(NetworkUtil.isValidIPv4("10.1.1.1"));
    }

    @Test
    public void testIsValidPort() {
        Assert.assertFalse(NetworkUtil.isValidPort(-1));
        Assert.assertFalse(NetworkUtil.isValidPort(0));
        Assert.assertFalse(NetworkUtil.isValidPort(65536));
        for (int i = 1; i <= 65535; i++) {
            Assert.assertTrue(NetworkUtil.isValidPort(i));
        }
    }
}
