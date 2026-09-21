package com.github.relucent.base.common.ldap;

import org.junit.Assert;
import org.junit.Test;

public class LdapConfigTest {

    @Test
    public void testSetSecure() {
        LdapConfig config = new LdapConfig();
        config.setSecure(true);
        Assert.assertTrue(config.isSecure());
        config.setSecure(false);
        Assert.assertFalse(config.isSecure());
    }

    @Test
    public void testOtherDefaults() {
        LdapConfig config = new LdapConfig();
        Assert.assertEquals(389, config.getPort());
        Assert.assertEquals("simple", config.getSecurityAuthentication());
        Assert.assertEquals("com.sun.jndi.ldap.LdapCtxFactory", config.getInitialContextFactory());
    }
}
