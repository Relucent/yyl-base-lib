package com.github.relucent.base.common.ldap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class LdapEntryTest {

    @Test
    public void testPutGetCaseInsensitiveKey() {
        LdapEntry entry = new LdapEntry("cn=foo,dc=example");
        entry.put("cn", "foo");
        assertEquals("foo", entry.get("CN")); // 键大小写不敏感
    }

    @Test
    public void testPutNumberPreservesType() {
        LdapEntry entry = new LdapEntry("dn");
        entry.put("uidNumber", 1000);
        Object value = entry.get("uidNumber");
        assertTrue(value instanceof Integer);
        assertEquals(1000, ((Integer) value).intValue());
    }

    @Test
    public void testGetString() {
        LdapEntry entry = new LdapEntry("dn");
        entry.put("cn", "foo");
        assertEquals("foo", entry.getString("cn"));
        assertNull(entry.getString("missing"));
    }

    @Test
    public void testPutNullKeySkipped() {
        LdapEntry entry = new LdapEntry("dn");
        entry.put(null, "x");
        entry.put("", "y");
        assertTrue(entry.isEmpty());
    }

    @Test
    public void testPutAll() {
        LdapEntry entry = new LdapEntry("dn");
        List<Object> values = new ArrayList<Object>(Arrays.asList("a", "b"));
        entry.putAll("mail", values);
        assertEquals(values, entry.getAll("mail"));
        assertEquals("a", entry.get("mail"));
    }

    @Test
    public void testToStringContainsAttributes() {
        LdapEntry entry = new LdapEntry("cn=foo,dc=example");
        entry.put("cn", "foo");
        entry.put("sn", "bar");
        String str = entry.toString();
        assertTrue(str.contains("dn:cn=foo,dc=example"));
        assertTrue(str.contains("cn=[foo]"));
        assertTrue(str.contains("sn=[bar]"));
        assertFalse(entry.isEmpty());
    }
}
