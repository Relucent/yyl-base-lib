package com.github.relucent.base.common.ldap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import javax.naming.NamingException;

import org.junit.Test;

public class LdapEncoderTest {

    @Test
    public void testFilterEncodeNull() {
        assertNull(LdapEncoder.filterEncode(null));
    }

    @Test
    public void testFilterEncodeSpecial() {
        // 特殊字符应按 RFC4515 转义
        assertEquals("\\28cn\\29", LdapEncoder.filterEncode("(cn)"));
        assertEquals("\\2a", LdapEncoder.filterEncode("*"));
        assertEquals("\\5c", LdapEncoder.filterEncode("\\"));
    }

    @Test
    public void testFilterEncodeLeadingHash() {
        // 首字符 '#' 必须转义(RFC4515)，避免被解析为 BER 二进制值
        assertEquals("\\23abc", LdapEncoder.filterEncode("#abc"));
        assertEquals("abc", LdapEncoder.filterEncode("abc"));
    }

    @Test
    public void testNameEncodeDecodeRoundtrip() throws NamingException {
        String name = "cn=John Doe,ou=People";
        String encoded = LdapEncoder.nameEncode(name);
        assertEquals(name, LdapEncoder.nameDecode(encoded));
    }

    @Test
    public void testNameDecodeHexEscape() throws NamingException {
        // \4a -> 'J'
        assertEquals("J", LdapEncoder.nameDecode("\\4a"));
    }

    @Test
    public void testNameDecodeInvalidHexThrows() {
        // 非法的十六进制转义应抛出 NamingException(而非未声明的 NumberFormatException)
        try {
            LdapEncoder.nameDecode("a\\XYb");
            fail("Expected NamingException");
        } catch (NamingException e) {
            // ok
        }
    }
}
