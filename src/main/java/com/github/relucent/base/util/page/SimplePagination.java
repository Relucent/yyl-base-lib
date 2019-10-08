package com.github.relucent.base.util.page;

/**
 * 分页查询条件参数<br>
 * @author YYL
 * @version 2010-10-11
 */
@SuppressWarnings("serial")
public class SimplePagination implements Pagination {

    // =================================Fields=================================================
    /** 开始查询 的数据索引号 (从0开始) */
    private long offset = 0;

    /** 每页条数 */
    private long limit = DEFAULT_LIMIT;

    // =================================Constructors===========================================
    /**
     * 构造函数
     */
    public SimplePagination() {
        this(0, DEFAULT_LIMIT);
    }

    /**
     * 构造函数
     * @param offset 查询数据开始索引
     * @param limit 查询记录数
     */
    public SimplePagination(long offset, long limit) {
        this.offset = offset;
        this.limit = limit;
    }

    // =================================Methods================================================
    /**
     * 获取从第几条数据开始查询
     * @return 查询的偏移量
     */
    public long getOffset() {
        return offset;
    }

    /**
     * 设置从第几条数据开始查询
     * @param offset 查询的偏移量
     */
    public void setOffset(long offset) {
        this.offset = offset;
    }

    /**
     * 获取每页显示记录数
     * @return 每页显示记录数
     */
    public long getLimit() {
        return limit;
    }

    /**
     * 设置每页显示记录数
     * @param limit 每页显示记录数
     */
    public void setLimit(long limit) {
        this.limit = limit;
    }

    // =================================HashCode_Equals========================================
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) limit;
        result = prime * result + (int) offset;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SimplePagination other = (SimplePagination) o;
        return limit == other.getLimit() && offset == other.getOffset();
    }

    @Override
    public String toString() {
        return "Pagination [offset=" + offset + ", limit=" + limit + "]";
    }
}
