package com.github.relucent.base.common.cache;

import java.time.Duration;
import java.util.Objects;

import com.github.relucent.base.common.reflect.TypeReference;

/**
 * 缓存定义
 * @param <T> 缓存元素的类型
 */
public class CacheDefinition<T> {

    /** 缓存名称 */
    private final String name;
    /** 缓存元素的类型引用 */
    private final TypeReference<T> elementType;
    /** 缓存对象过期时间 */
    private final Duration ttl;
    /** 缓存对象最长空闲时间 */
    private final Duration maxIdleTime;

    /**
     * 缓存定义构造函数
     * @param name 缓存名称
     * @param elementType 缓存元素的类型引用
     * @param ttl 缓存对象过期时间
     * @param maxIdleTime 缓存对象最长空闲时间
     */
    protected CacheDefinition(String name, TypeReference<T> elementType, Duration ttl, Duration maxIdleTime) {
        this.name = name;
        this.elementType = elementType;
        this.ttl = ttl;
        this.maxIdleTime = maxIdleTime;
    }

    /**
     * 获得一个缓存定义
     * @param <T> 缓存元素类型
     * @param name 缓存名称
     * @param elementType 缓存元素的类型引用
     * @return 缓存定义实例
     */
    public static <T> CacheDefinition<T> of(String name, TypeReference<T> elementType) {
        return of(name, elementType, null, null);
    }

    /**
     * 获得一个缓存定义
     * @param <T> 缓存元素类型
     * @param name 缓存名称
     * @param elementType 缓存元素的类型引用
     * @param ttl 缓存对象过期时间
     * @return 缓存定义实例
     */
    public static <T> CacheDefinition<T> of(String name, TypeReference<T> elementType, Duration ttl) {
        return of(name, elementType, ttl, null);
    }

    /**
     * 获得一个缓存定义
     * @param <T> 缓存元素类型
     * @param name 缓存名称
     * @param elementType 缓存元素的类型引用
     * @param ttl 缓存对象过期时间
     * @param maxIdleTime 缓存对象最长空闲时间
     * @return 缓存定义实例
     */
    public static <T> CacheDefinition<T> of(String name, TypeReference<T> elementType, Duration ttl, Duration maxIdleTime) {
        return new CacheDefinition<T>(name, elementType, ttl, maxIdleTime);
    }

    /**
     * 获得一个缓存定义
     * @param <T> 缓存元素类型
     * @param name 缓存名称
     * @param elementType 缓存元素的类型
     * @return 缓存定义实例
     */
    public static <T> CacheDefinition<T> of(String name, Class<T> elementType) {
        return of(name, elementType, null, null);
    }

    /**
     * 获得一个缓存定义
     * @param <T> 缓存元素类型
     * @param name 缓存名称
     * @param elementType 缓存元素的类型
     * @param ttl 缓存对象过期时间
     * @return 缓存定义实例
     */
    public static <T> CacheDefinition<T> of(String name, Class<T> elementType, Duration ttl) {
        return of(name, elementType, ttl, null);
    }

    /**
     * 获得一个缓存定义
     * @param <T> 缓存元素类型
     * @param name 缓存名称
     * @param elementType 缓存元素的类型
     * @param ttl 缓存对象过期时间
     * @param maxIdleTime 缓存对象最长空闲时间
     * @return 缓存定义实例
     */
    public static <T> CacheDefinition<T> of(String name, Class<T> elementType, Duration ttl, Duration maxIdleTime) {
        return of(name, TypeReference.of(elementType), ttl, maxIdleTime);
    }

    /**
     * 获得缓存名称
     * @return 缓存名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获得缓存元素的类型引用
     * @return 缓存元素元素的类型引用
     */
    public TypeReference<T> getElementType() {
        return elementType;
    }

    /**
     * 获得缓存对象过期时间
     * @return 缓存对象过期时间
     */
    public Duration getTtl() {
        return ttl;
    }

    /**
     * 获得缓存对象最长空闲时间
     * @return 缓存对象最长空闲时间
     */
    public Duration getMaxIdleTime() {
        return maxIdleTime;
    }

    /**
     * 获得缓存定义的构建器
     * @return 缓存定义的构建器
     */
    public Builder<T> newBuilder() {
        return new Builder<>(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elementType, maxIdleTime, name, ttl);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CacheDefinition<?> other = (CacheDefinition<?>) obj;
        return Objects.equals(name, other.name) && Objects.equals(elementType, other.elementType) && Objects.equals(maxIdleTime, other.maxIdleTime)
                && Objects.equals(ttl, other.ttl);
    }

    @Override
    public String toString() {
        return "SimpleCacheDefinition [name=" + name + ", elementType=" + elementType + ", ttl=" + ttl + ", maxIdleTime=" + maxIdleTime + "]";
    }

    public static class Builder<T> {
        private String name;
        private TypeReference<T> elementType;
        private Duration ttl;
        private Duration maxIdleTime;

        protected Builder(CacheDefinition<T> definition) {
            this.name = definition.name;
            this.elementType = definition.elementType;
            this.ttl = definition.ttl;
            this.maxIdleTime = definition.maxIdleTime;
        }

        public Builder<T> name(String name) {
            this.name = name;
            return this;
        }

        public Builder<T> elementType(TypeReference<T> elementType) {
            this.elementType = elementType;
            return this;
        }

        public Builder<T> ttl(Duration ttl) {
            this.ttl = ttl;
            return this;
        }

        public Builder<T> maxIdleTime(Duration maxIdleTime) {
            this.maxIdleTime = maxIdleTime;
            return this;
        }

        public CacheDefinition<T> build() {
            return new CacheDefinition<>(name, elementType, ttl, maxIdleTime);
        }
    }
}
