package com.github.relucent.base.common.awt;

import java.awt.Component;
import java.awt.Container;
import java.util.function.Function;

/**
 * AWT 工具类
 */
public class AwtUtil {
    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected AwtUtil() {
    }

    /**
     * 遍历父组件及其所有子组件，并执行自定义的操作
     * @param parent 父组件
     * @param function 执行的操作，如果该方法返回false，则不在继续遍历该组件的子组件
     */
    public static void traverseComponents(Component parent, Function<Component, Boolean> function) {

        if (Boolean.FALSE.equals(function.apply(parent))) {
            return;
        }

        if (parent instanceof Container) {
            Container container = (Container) parent;
            for (Component child : container.getComponents()) {
                traverseComponents(child, function);
            }
        }
    }
}
