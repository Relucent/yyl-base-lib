package com.github.relucent.base.common.bean.copier;

/**
 * 属性拷贝配置项<br>
 */
public class CopyOptions {

    // ----------------------------------------------------------------------------------------------------
    /**
     * 定义是否应忽略null值，如果为{@code true}，当源对象的值为null时，忽略而不拷贝此值
     */
    private boolean ignoreNull = false;

    /**
     * 定义是否检查transient关键字修饰和@Transient注解，如果检查，被修饰的字段或方法对应的字段将被忽略。
     */
    protected boolean checkTransient = true;

    /**
     * 定义是否覆盖目标值，如果不覆盖{@code false}，会先读取目标对象的值，为{@code null}则写，否则忽略。
     */
    protected boolean override = true;

    // ----------------------------------------------------------------------------------------------------
    /**
     * 设置是否应忽略null值，如果为{@code true}，当源对象的值为null时，忽略而不拷贝此值
     * @param ignoreNull 是否应忽略null值
     * @return {@code CopyOptions}
     */
    public CopyOptions setIgnoreNull(boolean ignoreNull) {
        this.ignoreNull = ignoreNull;
        return this;
    }

    /**
     * 是否应忽略null值，如果为{@code true}，当源对象的值为null时，忽略而不拷贝此值
     * @return 是否应忽略null值
     */
    public boolean isIgnoreNull() {
        return ignoreNull;
    }

    /**
     * 设置是否检查transient关键字修饰和@Transient注解。<br>
     * 如果为{@code true}，被修饰的字段或方法对应的字段将被忽略。
     * @param checkTransient 是否检查transient关键字修饰和@Transient注解
     * @return {@code CopyOptions}
     */
    public CopyOptions setCheckTransient(boolean checkTransient) {
        this.checkTransient = checkTransient;
        return this;
    }

    /**
     * 是否检查transient关键字修饰和@Transient注解。<br>
     * 如果为{@code true}，被修饰的字段或方法对应的字段将被忽略。
     * @return 是否检查transient关键字修饰和@Transient注解
     */
    public boolean isCheckTransient() {
        return checkTransient;
    }

    /**
     * 设置是否覆盖目标值，如果不覆盖，会先读取目标对象的值，为{@code null}则写，否则忽略。如果覆盖，则不判断直接写
     * @param override 是否覆盖目标值
     * @return {@code CopyOptions}
     */
    public CopyOptions setOverride(final boolean override) {
        this.override = override;
        return this;
    }

    /**
     * 是否覆盖目标值，如果不覆盖，会先读取目标对象的值，为{@code null}则写，否则忽略。如果覆盖，则不判断直接写
     * @return 是否覆盖目标值
     */
    public boolean isOverride() {
        return override;
    }
}
