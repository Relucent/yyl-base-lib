package com.github.relucent.base.common.io;

import java.io.InvalidClassException;
import java.util.Set;

import com.github.relucent.base.common.collection.CollectionUtil;
import com.github.relucent.base.common.collection.ConcurrentHashSet;

public class SerializeOptions {

    private Set<String> whiteClassSet = new ConcurrentHashSet<>();
    private Set<String> blackClassSet = new ConcurrentHashSet<>();

    /**
     * 接受反序列化的类，用于反序列化验证
     * @param acceptClasses 接受反序列化的类
     */
    public void accept(final Class<?>... acceptClasses) {
        for (final Class<?> acceptClass : acceptClasses) {
            this.whiteClassSet.add(acceptClass.getName());
        }
    }

    /**
     * 禁止反序列化的类，用于反序列化验证
     * @param refuseClasses 禁止反序列化的类
     */
    public void refuse(final Class<?>... refuseClasses) {
        for (final Class<?> acceptClass : refuseClasses) {
            this.blackClassSet.add(acceptClass.getName());
        }
    }

    /**
     * 验证反序列化的类是否合规
     * @param className 类名
     * @throws InvalidClassException 如果不合规则抛出该异常
     */
    void checkClassName(final String className) throws InvalidClassException {

        // 黑名单
        if (CollectionUtil.isNotEmpty(blackClassSet)) {
            if (blackClassSet.contains(className)) {
                throw new InvalidClassException("Unauthorized deserialization attempt by black list", className);
            }
        }
        // 白名单
        if (CollectionUtil.isEmpty(whiteClassSet)) {
            return;
        }
        // JAVA中的类
        if (className.startsWith("java.") && !className.startsWith("java.awt.geom.")) {
            return;
        }

        if (whiteClassSet.contains(className)) {
            return;
        }

        throw new InvalidClassException("Unauthorized deserialization attempt", className);
    }
}
