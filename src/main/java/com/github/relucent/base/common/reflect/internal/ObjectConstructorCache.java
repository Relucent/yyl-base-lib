package com.github.relucent.base.common.reflect.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

import com.github.relucent.base.common.collection.WeakConcurrentMap;
import com.github.relucent.base.common.exception.ExceptionUtil;
import com.github.relucent.base.common.reflect.TypeReference;

/**
 * 对象构造器构建类<br>
 * 返回一个对象构造器，该构造器可以构造所请求类型的实例。<br>
 */
public class ObjectConstructorCache {

    /** Bean属性缓实例 */
    public static final ObjectConstructorCache INSTANCE = new ObjectConstructorCache();

    /** 构建器缓存 */
    private final WeakConcurrentMap<TypeReference<?>, ObjectConstructor<?>> cache = new WeakConcurrentMap<>();

    /** 单例模式 */
    private ObjectConstructorCache() {
    }

    /**
     * 根据类型，获得一个对象构造器
     * @param <T> 对象的类型泛型
     * @param typeToken 对象的类型引用
     * @return 对象构造器
     */
    @SuppressWarnings("unchecked")
    public <T> ObjectConstructor<T> get(TypeReference<T> typeToken) {
        return (ObjectConstructor<T>) cache.computeIfAbsent(typeToken, (key) -> create(typeToken));
    }

    /**
     * 根据类型，获得一个对象构造器
     * @param <T> 对象的类型泛型
     * @param typeToken 对象的类型引用
     * @return 对象构造器
     */
    protected <T> ObjectConstructor<T> create(TypeReference<T> typeToken) {

        final Type type = typeToken.getType();
        final Class<? super T> rawType = typeToken.getRawType();

        // 使用默认的构造函数创建构建器
        ObjectConstructor<T> defaultConstructor = newDefaultConstructor(rawType);
        if (defaultConstructor != null) {
            return defaultConstructor;
        }

        // 常见接口类型（如Map和List）及其子类型的构造函数。
        ObjectConstructor<T> defaultImplementation = newDefaultImplementationConstructor(type, rawType);
        if (defaultImplementation != null) {
            return defaultImplementation;
        }

        // 最后尝试不安全 UNSAFE
        return newUnsafeAllocator(type, rawType);
    }

    /**
     * 使用默认的构造函数创建构建器
     * @param <T> 对象泛型
     * @param rawType 对象原始类
     * @return 对象原始类构造器
     */
    private <T> ObjectConstructor<T> newDefaultConstructor(Class<? super T> rawType) {
        try {
            final Constructor<? super T> constructor = rawType.getDeclaredConstructor();
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }
            return new ObjectConstructor<T>() {
                @SuppressWarnings("unchecked") // T is the same raw type as is requested
                @Override
                public T construct() {
                    try {
                        Object[] args = null;
                        return (T) constructor.newInstance(args);
                    } catch (InstantiationException e) {
                        throw new RuntimeException("Failed to invoke " + constructor + " with no args", e);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException("Failed to invoke " + constructor + " with no args", e.getTargetException());
                    } catch (IllegalAccessException e) {
                        throw new AssertionError(e);
                    }
                }
            };
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * 常见接口类型（如Map和List）及其子类型的构造函数。
     * @param <T> 对象泛型
     * @param type 对象类型
     * @param rawType 对象原始类
     * @return 对象的构造器
     */
    @SuppressWarnings("unchecked")
    private <T> ObjectConstructor<T> newDefaultImplementationConstructor(final Type type, Class<? super T> rawType) {
        if (Collection.class.isAssignableFrom(rawType)) {
            if (SortedSet.class.isAssignableFrom(rawType)) {
                return new ObjectConstructor<T>() {
                    @Override
                    public T construct() {
                        return (T) new TreeSet<Object>();
                    }
                };
            }
            if (EnumSet.class.isAssignableFrom(rawType)) {
                return new ObjectConstructor<T>() {
                    @SuppressWarnings("rawtypes")
                    @Override
                    public T construct() {
                        if (type instanceof ParameterizedType) {
                            Type elementType = ((ParameterizedType) type).getActualTypeArguments()[0];
                            if (elementType instanceof Class) {
                                return (T) EnumSet.noneOf((Class) elementType);
                            } else {
                                throw ExceptionUtil.error("Invalid EnumSet type: " + type.toString());
                            }
                        } else {
                            throw ExceptionUtil.error("Invalid EnumSet type: " + type.toString());
                        }
                    }
                };
            }
            if (Set.class.isAssignableFrom(rawType)) {
                return new ObjectConstructor<T>() {
                    @Override
                    public T construct() {
                        return (T) new LinkedHashSet<Object>();
                    }
                };
            }
            if (Queue.class.isAssignableFrom(rawType)) {
                return new ObjectConstructor<T>() {
                    @Override
                    public T construct() {
                        return (T) new ArrayDeque<Object>();
                    }
                };
            }
            return new ObjectConstructor<T>() {
                @Override
                public T construct() {
                    return (T) new ArrayList<Object>();
                }
            };
        }

        if (Map.class.isAssignableFrom(rawType)) {
            if (ConcurrentNavigableMap.class.isAssignableFrom(rawType)) {
                return new ObjectConstructor<T>() {
                    @Override
                    public T construct() {
                        return (T) new ConcurrentSkipListMap<Object, Object>();
                    }
                };
            }
            if (ConcurrentMap.class.isAssignableFrom(rawType)) {
                return new ObjectConstructor<T>() {
                    @Override
                    public T construct() {
                        return (T) new ConcurrentHashMap<Object, Object>();
                    }
                };
            }
            if (SortedMap.class.isAssignableFrom(rawType)) {
                return new ObjectConstructor<T>() {
                    @Override
                    public T construct() {
                        return (T) new TreeMap<Object, Object>();
                    }
                };

            }
            if (type instanceof ParameterizedType
                    && !(String.class.isAssignableFrom(TypeReference.of(((ParameterizedType) type).getActualTypeArguments()[0]).getRawType()))) {
                return new ObjectConstructor<T>() {
                    @Override
                    public T construct() {
                        return (T) new LinkedHashMap<Object, Object>();
                    }
                };
            }
            return new ObjectConstructor<T>() {
                @Override
                public T construct() {
                    return (T) new LinkedHashMap<String, Object>();
                }
            };
        }

        return null;
    }

    /**
     * 基于 不安全的对象分配器的构造函数。
     * @param <T> 对象泛型
     * @param type 对象类型
     * @param rawType 对象原始类
     * @return 对象的构造器
     */
    private <T> ObjectConstructor<T> newUnsafeAllocator(final Type type, final Class<? super T> rawType) {
        return new ObjectConstructor<T>() {

            private final UnsafeAllocator unsafeAllocator = UnsafeAllocator.create();

            @SuppressWarnings("unchecked")
            @Override
            public T construct() {
                try {
                    return (T) unsafeAllocator.newInstance(rawType);
                } catch (Exception e) {
                    throw new RuntimeException(("Unable to invoke no-args constructor for " + type + ". "
                            + "Registering an InstanceCreator with Gson for this type may fix this problem."), e);
                }
            }
        };
    }
}
