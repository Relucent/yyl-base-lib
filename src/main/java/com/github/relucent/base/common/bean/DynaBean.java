package com.github.relucent.base.common.bean;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

import com.github.relucent.base.common.bean.info.BeanPropDesc;
import com.github.relucent.base.common.exception.ExceptionHelper;
import com.github.relucent.base.common.lang.AssertUtil;
import com.github.relucent.base.common.lang.ClassUtil;

/**
 * 动态Bean (Dynamic Bean)<br>
 */
@SuppressWarnings("serial")
public class DynaBean implements Cloneable, Serializable {

    // ==============================Fields===========================================
    private final Class<?> beanClass;
    private final Object bean;

    // ==============================Constructors=====================================
    /**
     * 构造函数
     * @param bean Bean对象
     */
    public DynaBean(Object bean) {
        AssertUtil.notNull(bean);
        if (bean instanceof DynaBean) {
            bean = ((DynaBean) bean).getBean();
        }
        this.bean = bean;
        this.beanClass = ClassUtil.getClass(bean);
    }

    // ==============================Methods==========================================
    /**
     * 获得属性对应的值
     * @param <T> 属性值类型
     * @param property 属性名
     * @return 属性值
     * @throws RuntimeException 获取属性值失败导致的异常
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String property) {
        // 这是一个MAP对象
        if (Map.class.isAssignableFrom(beanClass)) {
            return (T) ((Map<?, ?>) bean).get(property);
        }
        // 获得Bean的属性
        BeanPropDesc prop = BeanUtil.getBeanDesc(beanClass).getProp(property);
        if (prop == null) {
            throw ExceptionHelper.error("No public property for " + bean.getClass() + "[" + property + "]");
        }
        return (T) prop.getValue(bean);
    }

    /**
     * 设置属性对应的值
     * @param property 属性名
     * @param value 属性值
     */
    @SuppressWarnings("unchecked")
    public void set(String property, Object value) {
        if (Map.class.isAssignableFrom(beanClass)) {
            ((Map<String, Object>) bean).put(property, value);
        } else {
            BeanPropDesc prop = BeanUtil.getBeanDesc(beanClass).getProp(property);
            if (prop == null) {
                throw ExceptionHelper.error("No public property for " + bean.getClass() + "[" + property + "]");
            }
            prop.setValue(bean, value);
        }
    }

    /**
     * 检查是否有指定名称的属性
     * @param property 属性名
     * @return 如果Bean对象有该属性，则返回{@code true}
     */
    public boolean hasProp(String property) {
        if (Map.class.isAssignableFrom(beanClass)) {
            return ((Map<?, ?>) bean).containsKey(property);
        }
        return BeanUtil.getBeanDesc(beanClass).getProp(property) != null;
    }

    /**
     * 获得原始Bean
     * @param <T> Bean类型
     * @return bean
     */
    @SuppressWarnings("unchecked")
    public <T> T getBean() {
        return (T) this.bean;
    }

    /**
     * 获得Bean的类型
     * @param <T> Bean类型
     * @return Bean类型
     */
    @SuppressWarnings("unchecked")
    public <T> Class<T> getBeanClass() {
        return (Class<T>) this.beanClass;
    }

    // ==============================OverrideMethods==================================
    @Override
    public DynaBean clone() {
        try {
            return (DynaBean) super.clone();
        } catch (final CloneNotSupportedException e) {
            throw ExceptionHelper.error("No public property for " + bean.getClass());
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(bean);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof DynaBean)) {
            return false;
        }
        return Objects.equals(bean, ((DynaBean) obj).bean);
    }

    @Override
    public String toString() {
        return "DynaBean(" + this.bean.toString() + ")";
    }
}
