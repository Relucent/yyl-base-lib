
package com.github.relucent.base.common.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;

/**
 * 序列化工具类， 依赖于JDK的序列化机制
 */
public class SerializeUtil {

    /**
     * 序列化对象，依赖于JDK的序列化机制，被序列化的对象必须实现{@code java.io.Serializable}
     * @param object 要被序列化的对象
     * @return 序列化后的字节码
     */
    public static byte[] serialize(Object object) {
        if (!(object instanceof Serializable)) {
            return null;
        }
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {
            try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
                output.writeObject(object);
                output.flush();
            }
            return bytes.toByteArray();
        } catch (final IOException e) {
            throw IoRuntimeException.wrap(e);
        }
    }

    /**
     * 反序列化对象， 依赖于JDK的序列化机制， 被反序列化的对象必须实现{@code java.io.Serializable}。<br>
     * 此方法不会检查反序列化安全，可能存在反序列化漏洞风险。<br>
     * @param <T> 对象类型
     * @param bytes 反序列化的字节码
     * @return 反序列化后的对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T deserialize(final byte[] bytes) {
        try {
            try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
                return (T) input.readObject();
            }
        } catch (Exception e) {
            throw IoRuntimeException.wrap(e);
        }
    }

    /**
     * 反序列化对象， 依赖于JDK的序列化机制， 被反序列化的对象必须实现{@code java.io.Serializable}。<br>
     * 可通过配置项{@code options}设置反序列化的黑名单和白名单，从而修复反序列化漏洞风险。<br>
     * @param <T> 对象类型
     * @param bytes 反序列化的字节码
     * @param options 配置项
     * @return 反序列化后的对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T deserialize(final byte[] bytes, SerializeOptions options) {
        try {
            try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes)) {
                @Override
                protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                    options.checkClassName((desc.getName()));
                    return super.resolveClass(desc);
                };
            }) {
                return (T) input.readObject();
            }
        } catch (Exception e) {
            throw IoRuntimeException.wrap(e);
        }
    }
}
