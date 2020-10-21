package com.github.relucent.base.plugin.mybatis;

import java.io.Serializable;

@SuppressWarnings("serial")
public class MybatisPageContext implements Serializable {

    private long offset = 0L;
    private long limit = 1L;
    private long total = -1L;

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getLimit() {
        return limit;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
