package com.github.relucent.base.common.reflect.access;

import java.lang.reflect.Constructor;

public class ConstructorAccess<T> {

    // ==============================Fields===========================================
    private final Constructor<T> constructor;

    // ==============================Constructor======================================
    protected ConstructorAccess(Class<T> type) {
        try {
            this.constructor = type.getDeclaredConstructor();
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException("Class cannot be created (missing no-arg constructor): " + type.getName(), e);
        }
    }

    public static <T> ConstructorAccess<T> create(Class<T> type) {
        return new ConstructorAccess<T>(type);
    }

    // ==============================Methods==========================================
    public T newInstance() {
        try {
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Class constructor new instance error: " + constructor.getName(), e);
        }
    }
}
