package com.github.relucent.base.common.exception;

/**
 * 异常工具类
 * @author YYL
 */
public class ExceptionHelper {

    /**
     * 创建通用异常
     * @param cause 源异常
     * @return 通用异常
     */
    public static GeneralException propagate(Throwable cause) {
        if (cause instanceof GeneralException) {
            return (GeneralException) cause;
        }
        return new GeneralException(ErrorType.DEFAULT, "#", cause);
    }

    /**
     * 创建未捕获异常
     * @param message 异常信息
     * @param cause 源异常
     * @return 未捕获异常
     */
    public static GeneralException propagate(String message, Throwable cause) {
        return new GeneralException(ErrorType.DEFAULT, message, cause);
    }

    /**
     * 创建提示异常
     * @param message 提示消息
     * @return 提示异常
     */
    public static GeneralException prompt(String message) {
        return new GeneralException(ErrorType.PROMPT, message);
    }

    /**
     * 创建通用异常
     * @param type 异常类型
     * @param message 异常信息
     * @return 通用异常
     */
    public static GeneralException error(ErrorType type, String message) {
        return new GeneralException(type, message);
    }
}
