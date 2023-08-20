package com.github.relucent.base.common.json.impl;

/**
 * 缩进量
 */
class Indenter {

    /** 缩进因子，定义每一级别增加的缩进量 */
    private final int indentFactor;

    /** 当前缩进量 */
    private int indent;

    public Indenter(int indentFactor) {
        this.indentFactor = indentFactor;
    }

    /**
     * 增加缩进
     */
    public void increment() {
        indent += indentFactor;
    }

    /**
     * 减少缩进
     */
    public void decrement() {
        indent -= indentFactor;
    }

    /**
     * 获得缩进量
     * @return 缩进量
     */
    public int getIndent() {
        return indent > 0 ? indent : 0;
    }

    /**
     * 是否启用缩进
     */
    public boolean isPretty() {
        return indentFactor != 0;
    }
}