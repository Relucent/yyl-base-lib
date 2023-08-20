package com.github.relucent.base.common.reflect;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 序列化功能的工具类
 * @author YYL
 */
public class SerializationUtil {

    private static final int BUFFER_SIZE = 1024;

    /**
     * 工具类私有构造
     */
    protected SerializationUtil() {
    }

    /**
     * 将给定对象序列化为字节数组，对象必须实现{@code java.io.Serializable}接口
     * @param object 要序列化的对象
     * @return 对象序列化的字节数组
     */
    public static byte[] serialize(Object object) {
        if (object == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream(BUFFER_SIZE);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            oos.flush();
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to serialize object of type: " + object.getClass(), ex);
        }
        return baos.toByteArray();
    }

    /**
     * 将字节数组反序列化为对象
     * @param <T> 对象类型泛型
     * @param bytes 对象序列化的字节数组
     * @return 反序列化字节的结果对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T deserialize(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            return (T) ois.readObject();
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to deserialize object", e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Failed to deserialize object type", e);
        }
    }

    /**
     * 使用序列化反序列化方式进行对象克隆，对象必须实现{@code java.io.Serializable}接口
     * @param <T> 对象类型
     * @param object 被克隆对象
     * @return 克隆后的对象
     */
    public static <T> T clone(T object) {
        if (!(object instanceof Serializable)) {
            return null;
        }
        return deserialize(serialize(object));
    }
}
