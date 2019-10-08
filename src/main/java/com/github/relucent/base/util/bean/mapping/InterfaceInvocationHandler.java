package com.github.relucent.base.util.bean.mapping;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class InterfaceInvocationHandler implements InvocationHandler {
    private final Map<String, Object> properties = new HashMap<String, Object>();

    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
        String methodName = method.getName();
        if (BeanMapper.isSetter(method)) {
            properties.put(BeanMapper.m2f(methodName), args[0]);
        } else if (BeanMapper.isGetter(method)) {
            return properties.get(BeanMapper.m2f(methodName));
        }
        return null;
    }
}
