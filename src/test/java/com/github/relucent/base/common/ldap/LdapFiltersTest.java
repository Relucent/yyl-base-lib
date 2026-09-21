package com.github.relucent.base.common.ldap;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LdapFiltersTest {

    @Test
    public void testEq() {
        assertEquals("(cn=foo)", LdapFilters.eq("cn", "foo").encode());
    }

    @Test
    public void testEqInt() {
        assertEquals("(uidNumber=1000)", LdapFilters.eq("uidNumber", 1000).encode());
    }

    @Test
    public void testNe() {
        assertEquals("(!(cn=foo))", LdapFilters.ne("cn", "foo").encode());
    }

    @Test
    public void testNeInt() {
        assertEquals("(!(uidNumber=1000))", LdapFilters.ne("uidNumber", 1000).encode());
    }

    @Test
    public void testGt() {
        assertEquals("(&(!(cn=foo))(cn>=foo))", LdapFilters.gt("cn", "foo").encode());
    }

    @Test
    public void testGtInt() {
        assertEquals("(&(!(uidNumber=1000))(uidNumber>=1000))", LdapFilters.gt("uidNumber", 1000).encode());
    }

    @Test
    public void testGe() {
        assertEquals("(cn>=foo)", LdapFilters.ge("cn", "foo").encode());
    }

    @Test
    public void testGeInt() {
        assertEquals("(uidNumber>=1000)", LdapFilters.ge("uidNumber", 1000).encode());
    }

    @Test
    public void testLt() {
        assertEquals("(&(!(cn=foo))(cn<=foo))", LdapFilters.lt("cn", "foo").encode());
    }

    @Test
    public void testLtInt() {
        assertEquals("(&(!(uidNumber=1000))(uidNumber<=1000))", LdapFilters.lt("uidNumber", 1000).encode());
    }

    @Test
    public void testLe() {
        assertEquals("(cn<=foo)", LdapFilters.le("cn", "foo").encode());
    }

    @Test
    public void testLeInt() {
        assertEquals("(uidNumber<=1000)", LdapFilters.le("uidNumber", 1000).encode());
    }

    @Test
    public void testAnd() {
        assertEquals("(&(a=1)(b=2))",
                LdapFilters.and().and(LdapFilters.eq("a", "1")).and(LdapFilters.eq("b", "2")).encode());
    }

    @Test
    public void testOr() {
        assertEquals("(|(a=1)(b=2))",
                LdapFilters.or().or(LdapFilters.eq("a", "1")).or(LdapFilters.eq("b", "2")).encode());
    }

    @Test
    public void testLike() {
        assertEquals("(cn=*foo*)", LdapFilters.like("cn", "foo").encode());
    }

    @Test
    public void testLikeStart() {
        assertEquals("(cn=foo*)", LdapFilters.likeStart("cn", "foo").encode());
    }

    @Test
    public void testLikeEnd() {
        assertEquals("(cn=*foo)", LdapFilters.likeEnd("cn", "foo").encode());
    }

    @Test
    public void testLikeExact() {
        assertEquals("(cn=foo*bar)", LdapFilters.likeExact("cn", "foo*bar").encode());
        assertEquals("(cn=foo)", LdapFilters.likeExact("cn", "foo").encode());
    }

    @Test
    public void testNot() {
        assertEquals("(!(a=1))", LdapFilters.not(LdapFilters.eq("a", "1")).encode());
    }

    @Test
    public void testExpression() {
        assertEquals("(objectClass=*)", LdapFilters.expression("(objectClass=*)").encode());
    }
}
