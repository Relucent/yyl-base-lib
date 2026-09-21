package com.github.relucent.base.common.bean.mapping;

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
            return null;
        } else if (BeanMapper.isGetter(method)) {
            return properties.get(BeanMapper.m2f(methodName));
        }
        // 处理 Object 方法，避免 hashCode/toString/equals 返回 null 触发拆箱 NPE
        if (method.getParameterCount() == 0) {
            if ("toString".equals(methodName)) {
                return "InterfaceInvocationHandler" + properties;
            }
            if ("hashCode".equals(methodName)) {
                return System.identityHashCode(proxy);
            }
        } else if (method.getParameterCount() == 1 && "equals".equals(methodName)) {
            return Boolean.valueOf(proxy == args[0]);
        }
        return null;
    }
}
