package com.github.relucent.base.common.bean.mapping;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class InterfaceProxyFactory {

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> proxyInterface) {
        ClassLoader classLoader = proxyInterface.getClassLoader();
        Class<?>[] interfaces = new Class[] {proxyInterface};
        InvocationHandler handler = new InterfaceInvocationHandler();
        return (T) Proxy.newProxyInstance(classLoader, interfaces, handler);
    }
}
