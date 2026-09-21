package com.github.relucent.base.common.ldap;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.junit.Assert;
import org.junit.Test;

public class LdapUtilTest {

    /** 可记录 close 调用的最小 NamingEnumeration 桩 */
    private static class FakeEnumeration implements NamingEnumeration<Object> {
        boolean closed = false;

        @Override
        public void close() throws NamingException {
            closed = true;
        }

        @Override
        public boolean hasMore() throws NamingException {
            return false;
        }

        @Override
        public Object next() throws NamingException {
            return null;
        }

        @Override
        public boolean hasMoreElements() {
            return false;
        }

        @Override
        public Object nextElement() {
            return null;
        }
    }

    @Test
    public void testCloseQuietlyCloses() {
        FakeEnumeration fake = new FakeEnumeration();
        LdapUtil.closeQuietly(fake);
        Assert.assertTrue("closeQuietly 应调用 enumeration.close()", fake.closed);
    }

    @Test
    public void testCloseQuietlyNullSafe() {
        // 传入 null 不应抛出任何异常
        LdapUtil.closeQuietly((NamingEnumeration<?>) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteNullDnThrows() throws NamingException {
        // dn 为 null 时应抛出 IllegalArgumentException(不触碰 ctx)
        LdapUtil.delete(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteEmptyDnThrows() throws NamingException {
        LdapUtil.delete("", null);
    }
}
