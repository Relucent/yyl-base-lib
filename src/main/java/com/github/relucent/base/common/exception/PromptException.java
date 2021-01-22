package com.github.relucent.base.common.exception;

/**
 * 提示异常类
 * @see RuntimeException
 * @author YYL
 */
@SuppressWarnings("serial")
public class PromptException extends GeneralException {

    /**
     * 构造函数
     * @param message 异常的详细信息， 可以使用{@link #getMessage()} 方法获取
     */
    public PromptException(String message) {
        super(message);
    }

    /**
     * 构造函数
     * @param code 异常的编码， 可以使用{@link #getCode()} 方法获取
     * @param message 异常的详细信息， 可以使用{@link #getMessage()} 方法获取
     */
    public PromptException(Integer code, String message) {
        super(code, message);
    }
}
