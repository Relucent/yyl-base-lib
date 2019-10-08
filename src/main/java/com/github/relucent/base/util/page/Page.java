package com.github.relucent.base.util.page;

import java.io.Serializable;
import java.util.List;

/**
 * 分页查询的结果数据 <br>
 * @author YYL
 * @version 2010-10-10
 */
public interface Page<T> extends Serializable {
    /**
     * 获取从第几条数据开始查询
     * @return 开始查询索引
     */
    long getOffset();

    /**
     * 获取每页查询记录数
     * @return 每页查询记录数
     */
    long getLimit();

    /**
     * 获取总记录数
     * @return 总记录数
     */
    long getTotal();

    /**
     * 获取当前页数据
     * @return 当前页数据
     */
    List<T> getRecords();
}
