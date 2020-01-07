package com.github.relucent.base.common.tree;

import java.util.List;

/**
 * 树结构节点模型
 * @author YYL
 * @param <N> 节点类型泛型
 */
public interface Node<N extends Node<N>> {

    /**
     * 获得子节点
     * @return 子节点
     */
    List<N> getChildren();

    /**
     * 设置子节点 
     * @param children 子节点
     */
    void setChildren(List<N> children);
}
