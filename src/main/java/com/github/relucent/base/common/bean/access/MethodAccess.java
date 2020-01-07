package com.github.relucent.base.common.bean.access;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MethodAccess {

    // ==============================Fields===========================================
    private final String[] methodNames;
    private final Class<?>[][] parameterTypes;
    private final Class<?>[] returnTypes;
    private final Method[] methods;

    // ==============================Constructor======================================
    protected MethodAccess(Class<?> type) {

        boolean isInterface = type.isInterface();
        if (!isInterface && type.getSuperclass() == null && type != Object.class) {
            throw new IllegalArgumentException("The type must not be a primitive type or void.");
        }

        List<Method> methods = new ArrayList<Method>();
        if (isInterface) {
            recursiveAddInterfaceMethodsToList(type, methods);
        } else {
            Class<?> nextClass = type;
            while (nextClass != Object.class) {
                addDeclaredMethodsToList(nextClass, methods);
                nextClass = nextClass.getSuperclass();
            }
        }

        int methodCount = methods.size();
        String[] methodNames = new String[methodCount];
        Class<?>[][] parameterTypes = new Class[methodCount][];
        Class<?>[] returnTypes = new Class[methodCount];
        for (int i = 0; i < methodCount; i++) {
            Method method = methods.get(i);
            methodNames[i] = method.getName();
            parameterTypes[i] = method.getParameterTypes();
            returnTypes[i] = method.getReturnType();
        }

        this.methodNames = methodNames;
        this.parameterTypes = parameterTypes;
        this.returnTypes = returnTypes;
        this.methods = methods.toArray(new Method[methodCount]);
    }

    public static MethodAccess create(Class<?> type) {
        return new MethodAccess(type);
    }

    // ==============================Methods==========================================
    public String[] getMethodNames() {
        return methodNames;
    }

    public Class<?>[][] getParameterTypes() {
        return parameterTypes;
    }

    public Class<?>[] getReturnTypes() {
        return returnTypes;
    }

    public Method[] getMethods() {
        return methods;
    }

    public int getIndex(String methodName, Class<?>... paramTypes) {
        for (int i = 0, n = methodNames.length; i < n; i++) {
            if (methodNames[i].equals(methodName) && Arrays.equals(paramTypes, parameterTypes[i])) {
                return i;
            }
        }
        throw new IllegalArgumentException("Unable to find non-private method: " + methodName + " " + Arrays.toString(paramTypes));
    }

    public Object invoke(Object instance, String methodName, Class<?>[] paramTypes, Object... args) {
        int index = getIndex(methodName, paramTypes);
        try {
            return methods[index].invoke(instance, args);
        } catch (IllegalArgumentException | ReflectiveOperationException e) {
            throw new RuntimeException("method invoke error : " + methodName, e);
        }
    }

    // ==============================StaticMethods====================================
    private static void addDeclaredMethodsToList(Class<?> type, List<Method> methods) {
        Method[] declaredMethods = type.getDeclaredMethods();
        for (int i = 0, n = declaredMethods.length; i < n; i++) {
            Method method = declaredMethods[i];
            int modifiers = method.getModifiers();
            if (Modifier.isPrivate(modifiers)) {
                continue;
            }
            methods.add(method);
        }
    }

    private static void recursiveAddInterfaceMethodsToList(Class<?> interfaceType, List<Method> methods) {
        addDeclaredMethodsToList(interfaceType, methods);
        for (Class<?> nextInterface : interfaceType.getInterfaces()) {
            recursiveAddInterfaceMethodsToList(nextInterface, methods);
        }
    }
}
