package com.github.relucent.base.common.bean.copier;

import java.lang.reflect.Type;
import java.util.Map;

import com.github.relucent.base.common.bean.BeanUtil;
import com.github.relucent.base.common.bean.BeanVisitor;
import com.github.relucent.base.common.bean.introspector.PropDesc;
import com.github.relucent.base.common.convert.ConvertUtil;
import com.github.relucent.base.common.reflect.TypeUtil;

/**
 * Bean 拷贝器
 */
public class BeanCopier {

    // ==============================Constants========================================
    private static final CopyOptions DEFAULT_OPTIONS = new CopyOptions();
    // ==============================Fields===========================================
    /** 源对象 */
    protected final Object source;
    /** 目标对象 */
    protected final Object target;
    /** 源对象类型 */
    protected final Type sourceType;
    /** 目标对象类型(用于标注有泛型参数的Bean对象) */
    protected final Type targetType;

    // ==============================Constructors=====================================
    /**
     * 构造函数
     * @param source 源对象
     * @param target 目标对象
     */
    public BeanCopier(Object source, Object target) {
        this(source, target, target.getClass());
    }

    /**
     * 构造函数
     * @param source 源对象
     * @param target 目标对象
     * @param targetType 目标对象类型
     */
    public BeanCopier(Object source, Object target, Type targetType) {
        this.source = source;
        this.target = target;
        this.sourceType = source.getClass();
        this.targetType = targetType;
    }

    // ==============================Methods==========================================
    /**
     * 执行属性拷贝
     */
    public void copy() {
        copy(DEFAULT_OPTIONS);
    }

    /**
     * 执行属性拷贝
     * @param options 配置项
     */
    @SuppressWarnings("unchecked")
    public void copy(final CopyOptions options) {

        if (source instanceof Map) {

            // Map To Map
            if (target instanceof Map) {

                @SuppressWarnings("rawtypes")
                final Map mapTarget = (Map) target;

                // 获取Map值的泛型
                final Type keyType = TypeUtil.getTypeArgument(targetType, 0);
                final Type valueType = TypeUtil.getTypeArgument(targetType, 1);

                ((Map<?, ?>) source).forEach((key, value) -> {

                    key = TypeUtil.isUnknown(keyType) ? key : ConvertUtil.convert(key, keyType, null);
                    value = TypeUtil.isUnknown(valueType) ? value : ConvertUtil.convert(value, valueType, null);

                    // 忽略 null
                    if (value == null && options.isIgnoreNull()) {
                        return;
                    }

                    // 不是覆盖模式，并且目标对象有值
                    if (!options.isOverride() && mapTarget.get(key) != null) {
                        return;
                    }

                    mapTarget.put(key, value);
                });
            }
            // Map To Bean
            else {
                final Map<String, PropDesc> targetPdMap = BeanUtil.getBeanDesc(target.getClass()).getPropMap();

                ((Map<?, ?>) source).forEach((key, value) -> {

                    String name = key.toString();

                    PropDesc pd = targetPdMap.get(name);

                    // 字段不可写
                    if (pd == null || !pd.isWritable(options.isCheckTransient())) {
                        return;
                    }

                    // 目标字段类型
                    Type toType = TypeUtil.getActualType(targetType, pd.getFieldType());

                    value = ConvertUtil.convert(value, toType, null);

                    // 忽略 null
                    if (value == null && options.isIgnoreNull()) {
                        return;
                    }

                    // 不是覆盖模式，并且目标对象有值
                    if (!options.isOverride() && pd.getValue(target) != null) {
                        return;
                    }

                    // 目标赋值
                    pd.setValue(this.target, value);
                });
            }
        } else {

            BeanVisitor beanVisitor = new BeanVisitor(source).setIgnoreNull(options.isIgnoreNull());

            // Bean To Map
            if (target instanceof Map) {

                @SuppressWarnings("rawtypes")
                final Map mapTarget = (Map) target;

                final Type keyType = TypeUtil.getTypeArgument(targetType, 0);
                final Type valueType = TypeUtil.getTypeArgument(targetType, 1);

                beanVisitor.visit((name, value) -> {

                    name = TypeUtil.isUnknown(keyType) ? name : ConvertUtil.convert(name, keyType, null);
                    value = TypeUtil.isUnknown(valueType) ? value : ConvertUtil.convert(value, valueType, null);

                    // 忽略 null
                    if (value == null && options.isIgnoreNull()) {
                        return;
                    }

                    // 不是覆盖模式，并且目标对象有值
                    if (!options.isOverride() && mapTarget.get(name) != null) {
                        return;
                    }

                    mapTarget.put(name, value);
                });

            }
            // Bean To Bean
            else {

                final Map<String, PropDesc> targetPdMap = BeanUtil.getBeanDesc(target.getClass()).getPropMap();

                beanVisitor.visit((name, value) -> {

                    PropDesc pd = targetPdMap.get(name);

                    // 字段不可写
                    if (pd == null || !pd.isWritable(options.isCheckTransient())) {
                        return;
                    }

                    // 目标字段类型
                    Type toType = TypeUtil.getActualType(targetType, pd.getFieldType());

                    value = ConvertUtil.convert(value, toType, null);

                    // 忽略 null
                    if (value == null && options.isIgnoreNull()) {
                        return;
                    }

                    // 不是覆盖模式，并且目标对象有值
                    if (!options.isOverride() && pd.getValue(target) != null) {
                        return;
                    }

                    // 目标赋值
                    pd.setValue(this.target, value);
                });
            }
        }
    }
}