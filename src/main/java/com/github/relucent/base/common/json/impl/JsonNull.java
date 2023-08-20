package com.github.relucent.base.common.json.impl;

import com.github.relucent.base.common.constant.StringConstant;

/**
 * 用于定义{@code null}
 */
public enum JsonNull {

    NULL;

    /**
     * 获得“null”字符串
     * @return The string "null".
     */
    @Override
    public String toString() {
        return StringConstant.NULL;
    }

    /**
     * 判断是否是NULL
     * @param value 对象
     * @return 如果对象是{@code null}或者是 @{code JsonNull} 则返回{@code true}
     */
    public static boolean isNull(Object value) {
        return value == null || NULL.equals(value);
    }
}
