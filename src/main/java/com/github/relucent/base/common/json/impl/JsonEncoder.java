package com.github.relucent.base.common.json.impl;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.github.relucent.base.common.time.DateUtil;

/**
 * JSON编码工具类，将JAVA对象编码为JSON字符串。
 * @author _yyl
 * @version 1.0
 */
public class JsonEncoder {

    private static Set<String> excludeFields = new HashSet<String>();
    static {
        excludeFields.add("class");
        excludeFields.add("declaringClass");
        excludeFields.add("metaClass");
    }

    /**
     * 将Java对象转化为JSON字符串
     * @param object java对象
     * @return JSON字符串
     */
    public String encode(Object object) {
        return encodeBasic(object);
    }

    /**
     * 将Java对象转化为JSON字符串
     * @param object java对象
     * @return JSON字符串
     */
    private String encodeBasic(Object object) {
        if (object == null) {
            return encodeNULL();
        }
        Class<?> clazz = object.getClass();
        if (clazz.isInterface()) {
            return encodeEmpty();
        } else if (object instanceof CharSequence) {
            return encodeString((CharSequence) object);
        } else if (object instanceof Boolean) {
            return encodeBoolean((Boolean) object);
        } else if (object instanceof Number) {
            return encodeNumber((Number) object);
        } else if (object instanceof Map) {
            return encodeMap((Map<?, ?>) object);
        } else if (object instanceof Iterable) {
            return encodeIterable((Iterable<?>) object);
        } else if (object instanceof Object[]) {// object.getClass().isArray()
            return encodeArray((Object[]) object);
        } else if (object instanceof java.util.Date) {
            return encodeDate((java.util.Date) object);
        } else if (object instanceof java.lang.Enum) {// object.getClass().isEnum()
            return encodeEnum((java.lang.Enum<?>) object);
        } else {
            return encodeBean(object);
        }
    }

    /**
     * 返回一个NULL对象
     * @return JSON字符串
     */
    private String encodeNULL() {
        return "null";
    }

    /**
     * 将Java-String对象转化为JSON字符串
     * @param string 字符串对象
     * @return JSON字符串
     */
    private String encodeString(CharSequence charSequence) {
        String string = charSequence.toString();
        StringBuilder sbr = new StringBuilder(string.length() * 4);
        sbr.append('\"');
        for (int i = 0, sz = string.length(); i < sz; i++) {
            char ch = string.charAt(i);
            // handle_unicode
            if (ch > 0xfff) {
                sbr.append("\\u");
                sbr.append(hex(ch));
            } else if (ch > 0xff) {
                sbr.append("\\u0");
                sbr.append(hex(ch));
            } else if (ch > 0x7f) {
                sbr.append("\\u00");
                sbr.append(hex(ch));
            } else if (ch < 32) {
                switch (ch) {
                case '\b':
                    sbr.append('\\');
                    sbr.append('b');
                    break;
                case '\n':
                    sbr.append('\\');
                    sbr.append('n');
                    break;
                case '\t':
                    sbr.append('\\');
                    sbr.append('t');
                    break;
                case '\f':
                    sbr.append('\\');
                    sbr.append('f');
                    break;
                case '\r':
                    sbr.append('\\');
                    sbr.append('r');
                    break;
                default:
                    if (ch > 0xf) {
                        sbr.append("\\u00");
                        sbr.append(hex(ch));
                    } else {
                        sbr.append("\\u000");
                        sbr.append(hex(ch));
                    }
                    break;
                }
            } else {
                // line.
                switch (ch) {
                case '\'':
                    sbr.append("\\u0027");
                    break;
                case '"':
                case '\\':
                    sbr.append("\\");
                    sbr.append(ch);
                    break;
                default:
                    sbr.append(ch);
                    break;
                }
            }
        }
        sbr.append('\"');
        return sbr.toString();
    }

    /**
     * 转换char的16进制表示形式
     * @param ch 字符
     * @return 16进制字符串
     */
    private String hex(char ch) {
        return Integer.toHexString(ch).toUpperCase(Locale.ENGLISH);
    }

    /**
     * 将Java-Boolean对象转化为JSON字符串
     * @param obj 字符串对象
     * @return JSON字符串
     */
    private String encodeBoolean(Boolean b) {
        return b.toString();
    }

    /**
     * 将Java-Number对象转化为JSON字符串
     * @param n 数字对象
     * @return JSON字符串
     */
    private String encodeNumber(Number n) {
        return n.toString();
    }

    /**
     * 将Java-Map对象转化为JSON字符串
     * @param map Map对象
     * @return JSON字符串
     */
    private String encodeMap(Map<?, ?> map) {
        boolean isFirst = true;
        StringBuilder sbr = new StringBuilder();
        sbr.append("{");
        for (java.util.Iterator<?> it = map.entrySet().iterator(); it.hasNext();) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) it.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (value != null) {
                if (!isFirst) {
                    sbr.append(",");
                } else {
                    isFirst = false;
                }
                sbr.append(encodeBasic(key)).append(":").append(encodeBasic(value));
            }
        }
        sbr.append("}");
        return sbr.toString();
    }

    /**
     * 将Java-Iterable对象转化为JSON字符串
     * @param iterable 可迭代的对象
     * @return JSON字符串
     */
    private String encodeIterable(java.lang.Iterable<?> iterable) {
        StringBuilder sbr = new StringBuilder();
        sbr.append("[");
        int index = 0;
        for (java.util.Iterator<?> it = iterable.iterator(); it.hasNext();) {
            if ((index++) > 0) {
                sbr.append(",");
            }
            sbr.append(encodeBasic(it.next()));
        }
        sbr.append("]");
        return sbr.toString();
    }

    /**
     * 将Java-数组对象转化为JSON字符串
     * @param obj 数组对象
     * @return JSON字符串
     */
    private String encodeArray(Object[] array) {
        StringBuilder sbr = new StringBuilder();
        sbr.append("[");
        for (int index = 0; index < array.length; index++) {
            if (index > 0) {
                sbr.append(",");
            }
            Object o = array[index];
            sbr.append(encodeBasic(o));
        }
        sbr.append("]");
        return sbr.toString();
    }

    /**
     * 将Java-Date对象转化为JSON字符串
     * @param date 日期对象
     * @return JSON字符串
     */
    private String encodeDate(Date date) {
        return this.encode(DateUtil.formatDateTime(date));
    }

    /**
     * 将Java枚举对象转化为JSON字符串
     * @param e 枚举对象
     * @return JSON字符串
     */
    private String encodeEnum(java.lang.Enum<?> e) {
        return "'" + e.name() + "'";
    }

    /**
     * 返回一个JSON简单对象
     * @return JSON字符串
     */
    private String encodeEmpty() {
        return "{}";
    }

    /**
     * 将Java对象转化为JSON字符串
     * @param object Java对象
     * @return JSON字符串
     */
    private String encodeBean(Object object) {
        try {
            Map<String, Object> proxy = new HashMap<String, Object>();
            Class<?> clazz = object.getClass();
            if (clazz == null) {
                throw new IllegalArgumentException("No bean class specified");
            }
            PropertyDescriptor[] descriptors = null;
            try {
                descriptors = Introspector.getBeanInfo(clazz).getPropertyDescriptors();
            } catch (IntrospectionException e) {
                descriptors = new PropertyDescriptor[0];
            }
            for (int i = 0, j = descriptors.length; i < j; i++) {
                PropertyDescriptor descriptor = descriptors[i];
                String key = descriptor.getName();

                // 排除的字段
                if (excludeFields.contains(key)) {
                    continue;
                }

                Method method = descriptor.getReadMethod();
                if (descriptor.getReadMethod() != null) {
                    Class<?> type = descriptor.getPropertyType();
                    if (type.isEnum()) {
                        continue;
                    }
                    // 可以替换为PropertyUtils.getProperty(bean, key)
                    Object value = method.invoke(object);
                    proxy.put(key, value);
                }
            }
            return encodeMap(proxy);
        } catch (Exception ex) {
            return encodeEmpty();
        }
    }
}
