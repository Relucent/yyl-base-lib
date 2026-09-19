package com.github.relucent.base.common.io;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;

import org.junit.Test;

public class PathUtilTest {

    @Test
    public void testIsSubFalseWhenOnlyPrefixOverlap() {
        // Path.startsWith 为元素级比较，/a/bc 不应误判为 /a/b 的子目录
        assertFalse(PathUtil.isSub(Paths.get("/a/b"), Paths.get("/a/bc")));
    }

    @Test
    public void testIsSubTrueForChild() {
        assertTrue(PathUtil.isSub(Paths.get("/a/b"), Paths.get("/a/b/c")));
    }

    @Test
    public void testIsSubRoot() {
        assertTrue(PathUtil.isSub(Paths.get("/"), Paths.get("/a")));
    }
}
