package com.github.relucent.base.plug.model;

import java.io.Serializable;

/**
 * 结果对象
 * @param <T> 返回数据泛型
 */
@SuppressWarnings("serial")
public class Result<T> implements Serializable {

    // ==============================Fields========================================
    private Integer code;
    private String message;
    private T data;

    // ==============================Constructors==================================
    public Result(String message, T data) {
        this(200, message, data);
    }

    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static Result<?> ok() {
        return ok(null);
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>("OK", data);
    }

    public static <T> Result<T> ok(String message, T data) {
        return new Result<>(200, message, data);
    }

    public static Result<?> error() {
        return new Result<>(500, "ERROR", null);
    }

    public static Result<?> error(String message) {
        return new Result<>(500, message, null);
    }

    public static Result<?> ofMessage(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    // ========================================Methods========================================
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
