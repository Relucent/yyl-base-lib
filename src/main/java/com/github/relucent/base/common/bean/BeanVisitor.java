package com.github.relucent.base.common.bean;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;

import com.github.relucent.base.common.bean.introspector.BeanDescCache;
import com.github.relucent.base.common.bean.introspector.PropDesc;

/**
 * Bean 属性访问器，可以从 Bean中提取属性名称，然后逐个访问。<br>
 */
public class BeanVisitor {

    // ==============================Fields===========================================
    /**
     * 原始 Bean
     */
    private final Object source;
    /**
     * 定义是否应忽略null值
     */
    private boolean ignoreNull;

    // ----------------------------------------------------------------------------------------------------
    public BeanVisitor(final Object source) {
        this.source = source;
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * 定义是否应忽略 {@code null}值。
     * @param ignoreNull 是否应忽略 {@code null}值
     * @return Bean属性访问器
     */
    public BeanVisitor setIgnoreNull(final boolean ignoreNull) {
        this.ignoreNull = ignoreNull;
        return this;
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * 开始访问属性
     * @param consumer 属性处理动作
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void visit(final BiConsumer<String, Object> consumer) {
        if (source instanceof Map) {
            for (Map.Entry entry : ((Map<Object, Object>) source).entrySet()) {
                Object key = entry.getKey();
                Object value = entry.getValue();
                if (value == null && ignoreNull) {
                    continue;
                }
                consumer.accept(String.valueOf(key), value);
            }
        } else {
            Collection<PropDesc> pds = BeanDescCache.INSTANCE.getBeanDesc(source.getClass()).getProps();
            for (final PropDesc pd : pds) {
                String propertyName = pd.getFieldName();
                Object propertyValue = pd.getValue(source);
                if (propertyValue == null && ignoreNull) {
                    continue;
                }
                consumer.accept(propertyName, propertyValue);
            }
        }
    }
}
