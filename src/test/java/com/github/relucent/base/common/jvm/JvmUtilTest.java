package com.github.relucent.base.common.jvm;

import org.junit.Assert;
import org.junit.Test;

public class JvmUtilTest {
    @Test
    public void testGetPid() {
        Assert.assertTrue(JvmUtil.getPid() != -1);
    }

    @Test
    public void testGetProcessName() {
        Assert.assertNotNull(JvmUtil.getProcessName());
    }

    @Test
    public void testJvmMeta() {
        Assert.assertNotNull(JvmUtil.getJvmName());
        Assert.assertNotNull(JvmUtil.getJvmVersion());
        Assert.assertNotNull(JvmUtil.getJvmVendor());
        Assert.assertNotNull(JvmUtil.getJvmInfo());
        Assert.assertTrue(JvmUtil.getJvmStartTime() > 0);
        Assert.assertTrue(JvmUtil.getJvmUptime() >= 0);
    }

    @Test
    public void testMemory() {
        Assert.assertTrue(JvmUtil.getMaxMemory() > 0);
        Assert.assertTrue(JvmUtil.getTotalMemory() > 0);
        Assert.assertTrue(JvmUtil.getFreeMemory() >= 0);
        Assert.assertTrue(JvmUtil.getUsedMemory() >= 0);
        Assert.assertTrue(JvmUtil.getUsedMemory() <= JvmUtil.getTotalMemory());
        Assert.assertTrue(JvmUtil.getUsableMemory() >= 0);
        Assert.assertNotNull(JvmUtil.getHeapMemoryUsage());
        Assert.assertNotNull(JvmUtil.getNonHeapMemoryUsage());
    }

    @Test
    public void testClassLoading() {
        Assert.assertTrue(JvmUtil.getLoadedClassCount() > 0);
        Assert.assertTrue(JvmUtil.getTotalLoadedClassCount() > 0);
        Assert.assertTrue(JvmUtil.getUnloadedClassCount() >= 0);
    }
}