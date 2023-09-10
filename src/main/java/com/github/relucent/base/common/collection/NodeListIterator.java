package com.github.relucent.base.common.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 迭代器{@link NodeList}数组迭代器{@link Iterator}适配类
 */
public class NodeListIterator implements Iterator<Node> {

    // =================================Fields=================================================
    /** 节点列表 */
    private final NodeList nodeList;
    /** 当前位置 */
    private int cursor = 0;

    // ==============================Constructors=====================================
    /**
     * 构造函数
     * @param nodeList 节点列表
     */
    public NodeListIterator(final NodeList nodeList) {
        this.nodeList = nodeList;
    }

    // ==============================Methods==========================================
    @Override
    public boolean hasNext() {
        return (cursor < nodeList.getLength());
    }

    @Override
    public Node next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return nodeList.item(cursor++);
    }
}
