package com.github.relucent.base.common.io;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

import java.io.InvalidClassException;

import org.junit.Test;

public class SerializeOptionsTest {

    @Test
    public void testDefaultAllowlistIncludesLowRiskTypes() {
        final SerializeOptions options = new SerializeOptions();
        // 构造即内置低危值类型白名单，下列类型无需显式 accept 即可通过
        final String[] allowed = {
                "java.lang.String",
                "java.lang.Integer",
                "java.math.BigDecimal",
                "java.util.Date",
                "java.util.UUID",
                "java.time.LocalDate",
                "java.time.Instant",
                "java.time.ZonedDateTime"
        };
        for (final String name : allowed) {
            try {
                options.checkClassName(name);
            } catch (final InvalidClassException e) {
                fail("默认白名单应放行低危类型 " + name);
            }
        }
    }

    @Test
    public void testUnknownClassRejectedByDefault() {
        final SerializeOptions options = new SerializeOptions();
        // 非默认白名单且无 gadget 嫌疑的常见容器，默认仍应拒绝（fail-closed 内核保留）
        assertThrows(InvalidClassException.class, () -> options.checkClassName("java.util.ArrayList"));
    }

    @Test
    public void testAllowUnlistedOptIn() {
        final SerializeOptions options = new SerializeOptions();
        options.setAllowUnlisted(true);
        try {
            options.checkClassName("java.lang.String");
            options.checkClassName("java.beans.EventHandler");
        } catch (final InvalidClassException e) {
            fail("setAllowUnlisted(true) 应放行白名单外的类: " + e.getMessage());
        }
    }

    @Test
    public void testBlacklistRejects() {
        final SerializeOptions options = new SerializeOptions();
        options.refuse(String.class);
        assertThrows(InvalidClassException.class, () -> options.checkClassName("java.lang.String"));
    }

    @Test
    public void testWhitelistAllowsListedAndRejectsUnlisted() {
        final SerializeOptions options = new SerializeOptions();
        options.accept(Integer.class);
        try {
            options.checkClassName("java.lang.Integer");
        } catch (final InvalidClassException e) {
            fail("白名单内的类应放行");
        }
        // ArrayList 不在默认白名单，也未显式 accept，应被拒绝
        assertThrows(InvalidClassException.class, () -> options.checkClassName("java.util.ArrayList"));
    }

    @Test
    public void testKnownDeserializationGadgetsBlockedByDefault() {
        final SerializeOptions options = new SerializeOptions();
        assertThrows(InvalidClassException.class, () -> options.checkClassName("java.beans.EventHandler"));
        assertThrows(InvalidClassException.class, () -> options.checkClassName("java.util.PriorityQueue"));
        assertThrows(InvalidClassException.class, () -> options.checkClassName("java.rmi.server.UnicastRemoteObject"));
    }

    @Test
    public void testClearAllowlistResetsToStrictEmpty() {
        final SerializeOptions options = new SerializeOptions();
        options.clearAllowlist();
        // 复位后即便默认放行的 String 也会被拒绝
        assertThrows(InvalidClassException.class, () -> options.checkClassName("java.lang.String"));
    }
}
