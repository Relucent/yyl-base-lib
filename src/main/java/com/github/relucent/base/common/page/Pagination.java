package com.github.relucent.base.common.page;

import java.io.Serializable;

/**
 * 分页查询条件参数<br>
 * @author YYL
 * @version 2010-10-10
 */
public interface Pagination extends Serializable {

    /**
     * 获取从第几条数据开始查询
     * @return 查询的偏移量
     */
    long getOffset();

    /**
     * 获取每页显示记录数
     * @return 每页显示记录数
     */
    long getLimit();
}
