package com.github.relucent.base.plugin.model;

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
    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static Result<?> ok() {
        return ok(null);
    }

    public static <T> Result<T> ok(T data) {
        return ok("OK", data);
    }

    public static <T> Result<T> okMessage(String message) {
        return ok(message, null);
    }

    public static <T> Result<T> ok(String msg, T data) {
        return new Result<>(200, msg, data);
    }

    public static <T> Result<T> error() {
        return error(null);
    }

    public static <T> Result<T> error(T data) {
        return error("ERROR", data);
    }

    public static <T> Result<T> errorMessage(String message) {
        return error(message, null);
    }

    public static <T> Result<T> error(String msg, T data) {
        return new Result<>(500, msg, data);
    }

    public static <T> Result<T> ofMessage(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> of(Integer code, String message, T data) {
        return new Result<>(code, message, data);
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
