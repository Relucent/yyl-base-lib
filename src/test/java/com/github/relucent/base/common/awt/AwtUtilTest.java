package com.github.relucent.base.common.awt;

import java.awt.Component;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * AwtUtil 测试（仅校验空安全分支，不触碰真实显示资源）
 */
public class AwtUtilTest {

    @Test(expected = IllegalArgumentException.class)
    public void testTraverseNullParentThrows() {
        AwtUtil.traverseComponents(null, c -> true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTraverseNullFunctionThrows() {
        // 匿名 Component 子类构造不需要显示资源，且守卫在解引用前即抛异常
        AwtUtil.traverseComponents(new Component() {}, null);
    }

    @Test
    public void testTraverseComponentNoChildren() {
        // 非 Container 组件：遍历自身一次，无子节点，不抛异常
        final boolean[] invoked = {false};
        AwtUtil.traverseComponents(new Component() {}, c -> {
            invoked[0] = true;
            return true;
        });
        assertTrue("function 应至少对父组件执行一次", invoked[0]);
    }

    @Test
    public void testTraverseStopWhenFunctionReturnsFalse() {
        // 返回 false 停止继续遍历子组件；此处父组件非 Container，主要验证不抛异常
        final boolean[] invoked = {false};
        AwtUtil.traverseComponents(new Component() {}, c -> {
            invoked[0] = true;
            return false;
        });
        assertTrue(invoked[0]);
    }
}
