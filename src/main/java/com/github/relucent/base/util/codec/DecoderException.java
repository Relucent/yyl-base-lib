package com.github.relucent.base.util.codec;

/**
 * 解码过程中出现故障时抛出的异常。 遇到解码特定异常，例如无效数据或超出预期范围的字符。
 */
@SuppressWarnings("serial")
public class DecoderException extends RuntimeException {

    /**
     * 构造函数，将<code>null</code>作为其详细消息。原因未初始化，随后可以通过调用{@link #initCause}来初始化。
     */
    public DecoderException() {
        super();
    }

    /**
     * 构造函数
     * @param message 异常的详细信息， 可以使用{@link #getMessage()} 方法获取
     */
    public DecoderException(final String message) {
        super(message);
    }

    /**
     * 构造函数，指定的原因和的详细消息
     * @param message 异常的详细信息， 可以使用{@link #getMessage()} 方法获取
     * @param cause 原因异常，可以通过{@link #getCause()} 方法获取
     */
    public DecoderException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造函数，指定的原因
     * @param cause 原因异常，可以通过{@link #getCause()} 方法获取
     */
    public DecoderException(final Throwable cause) {
        super(cause);
    }
}
