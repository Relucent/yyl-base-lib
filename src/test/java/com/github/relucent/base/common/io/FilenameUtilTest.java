package com.github.relucent.base.common.io;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FilenameUtilTest {

    @Test
    public void testRelativeExactMatch() {
        assertEquals("", FilenameUtil.relative("/home/user", "/home/user"));
    }

    @Test
    public void testRelativeChild() {
        assertEquals("x", FilenameUtil.relative("/home/user", "/home/user/x"));
    }

    @Test
    public void testRelativeNonChildPrefix() {
        // /home/user 不应把 /home/userdata/x 当成其子路径而误删前缀
        assertEquals("/home/userdata/x", FilenameUtil.relative("/home/user", "/home/userdata/x"));
    }

    @Test
    public void testRelativeRootChild() {
        assertEquals("a", FilenameUtil.relative("/", "/a"));
    }

    @Test
    public void testRelativeRelativePaths() {
        assertEquals("c.txt", FilenameUtil.relative("a/b", "a/b/c.txt"));
    }

    @Test
    public void testRelativeWindowsSeparators() {
        assertEquals("b", FilenameUtil.relative("C:\\a", "C:\\a\\b"));
    }

    @Test
    public void testRelativeTrailingSeparator() {
        assertEquals("x", FilenameUtil.relative("/home/user/", "/home/user/x"));
    }

    @Test
    public void testGetName() {
        assertEquals("c.txt", FilenameUtil.getName("a/b/c.txt"));
        assertEquals("c.txt", FilenameUtil.getName("a\\b\\c.txt"));
    }

    @Test
    public void testGetExtension() {
        assertEquals("txt", FilenameUtil.getExtension("a/b/c.txt"));
        assertEquals("", FilenameUtil.getExtension("a/b/c"));
    }

    @Test
    public void testGetBaseName() {
        assertEquals("c", FilenameUtil.getBaseName("a/b/c.txt"));
    }
}
