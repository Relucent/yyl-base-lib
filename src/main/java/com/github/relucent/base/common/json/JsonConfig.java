package com.github.relucent.base.common.json;

import java.io.Serializable;

/**
 * JSON配置
 */
@SuppressWarnings("serial")
public class JsonConfig implements Serializable {

    // ==============================Fields===========================================
    /**
     * 是否忽略转换过程中的异常
     */
    private final boolean ignoreError;
    /**
     * 日期是否会被转换为时间戳
     */
    private final boolean writeDateAsTimestamps;
    /**
     * 是否忽略null值
     */
    private final boolean ignoreNullValue;
    /**
     * 是否支持transient关键字修饰和@Transient注解，如果支持，被修饰的字段或方法对应的字段将被忽略。
     */
    private final boolean transientSupport;
    /**
     * 是否去除末尾多余0，例如如果为true,5.0返回5
     */
    private final boolean stripTrailingZeros;

    /**
     * 缩进因子，定义每一级别增加的缩进量
     */
    private final int indentFactor;

    // ==============================Constructors=====================================
    protected JsonConfig(Builder builder) {
        this.ignoreError = builder.ignoreError;
        this.writeDateAsTimestamps = builder.writeDateAsTimestamps;
        this.ignoreNullValue = builder.ignoreNullValue;
        this.transientSupport = builder.transientSupport;
        this.stripTrailingZeros = builder.stripTrailingZeros;
        this.indentFactor = builder.indentFactor;
    }

    // ==============================Methods===========================================
    /**
     * 是否忽略转换过程中的异常
     * @return 是否忽略转换过程中的异常
     */
    public boolean isIgnoreError() {
        return ignoreError;
    }

    /**
     * 日期是否会被转换为时间戳
     * @return 日期是否会被转换为时间戳
     */
    public boolean isWriteDateAsTimestamps() {
        return writeDateAsTimestamps;
    }

    /**
     * 是否忽略null值
     * @return 是否忽略null值
     */
    public boolean isIgnoreNullValue() {
        return this.ignoreNullValue;
    }

    /**
     * 是否支持transient关键字修饰和@Transient注解，如果支持，被修饰的字段或方法对应的字段将被忽略。
     * @return 是否支持
     */
    public boolean isTransientSupport() {
        return this.transientSupport;
    }

    /**
     * 是否去除数字类型末尾多余0
     * @return 是否去除末尾多余0
     */
    public boolean isStripTrailingZeros() {
        return stripTrailingZeros;
    }

    /**
     * 设置缩进因子，定义每一级别增加的缩进量(用于JSON美化)
     * @return 缩进因子
     */
    public int getindentFactor() {
        return indentFactor;
    }

    /**
     * 获得构建器
     * @return 构建器
     */
    public Builder builder() {
        return new Builder()//
                .setIgnoreError(ignoreError)//
                .setWriteDateAsTimestamps(writeDateAsTimestamps)//
                .setIgnoreNullValue(ignoreNullValue)//
                .setTransientSupport(transientSupport)//
                .setStripTrailingZeros(stripTrailingZeros)//
                .setIndentFactor(indentFactor);//
    }

    // ==============================Builders==========================================
    /**
     * 构建器
     */
    public static class Builder {
        /** 是否忽略转换过程中的异常 */
        private boolean ignoreError;
        /** 日期是否会被转换为时间戳 */
        private boolean writeDateAsTimestamps;
        /** 是否忽略null值 */
        private boolean ignoreNullValue = true;
        /** 是否支持transient关键字修饰和@Transient注解，如果支持，被修饰的字段或方法对应的字段将被忽略。 */
        private boolean transientSupport = true;
        /** 是否去除末尾多余0，例如如果为true,5.0返回5 */
        private boolean stripTrailingZeros = true;
        /** 缩进因子，定义每一级别增加的缩进量 */
        private int indentFactor = 0;

        /**
         * 设置是否忽略转换过程中的异常
         * @param ignoreError 是否忽略转换过程中的异常
         * @return {@code Builder}
         */
        public Builder setIgnoreError(boolean ignoreError) {
            this.ignoreError = ignoreError;
            return this;
        }

        /**
         * 设置日期是否会被转换为时间戳
         * @param writeDateAsTimestamps 日期是否会被转换为时间戳
         * @return {@code Builder}
         */
        public Builder setWriteDateAsTimestamps(boolean writeDateAsTimestamps) {
            this.writeDateAsTimestamps = writeDateAsTimestamps;
            return this;
        }

        /**
         * 设置是否忽略null值
         * @param ignoreNullValue 是否忽略null值
         * @return {@code Builder}
         */
        public Builder setIgnoreNullValue(boolean ignoreNullValue) {
            this.ignoreNullValue = ignoreNullValue;
            return this;
        }

        /**
         * 设置是否支持transient关键字修饰和@Transient注解，如果支持，被修饰的字段或方法对应的字段将被忽略。
         * @param transientSupport 是否支持
         * @return {@code Builder}
         */
        public Builder setTransientSupport(boolean transientSupport) {
            this.transientSupport = transientSupport;
            return this;
        }

        /**
         * 设置是否去除数字类型末尾多余0
         * @param stripTrailingZeros 是否去除末尾多余0
         * @return {@code Builder}
         */
        public Builder setStripTrailingZeros(boolean stripTrailingZeros) {
            this.stripTrailingZeros = stripTrailingZeros;
            return this;
        }

        /**
         * 设置缩进因子，定义每一级别增加的缩进量(JSON美化)
         * @param indentFactor 缩进因子
         * @return {@code Builder}
         */
        public Builder setIndentFactor(int indentFactor) {
            this.indentFactor = indentFactor;
            return this;
        }

        /**
         * 构建 JSON配置
         * @return JSON配置
         */
        public JsonConfig build() {
            return new JsonConfig(this);
        }
    }

}
