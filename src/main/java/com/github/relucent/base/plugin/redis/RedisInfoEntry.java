package com.github.relucent.base.plugin.redis;

/**
 * _Redis 服务器的各种信息和统计数值
 */
public class RedisInfoEntry {

    // =================================Fields================================================
    private String name;
    private String value;
    private String description;

    // =================================Methods================================================
    /**
     * 获得信息名称
     * @return 信息名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置信息名称
     * @param name 信息名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获得信息值
     * @return 信息值
     */
    public String getValue() {
        return value;
    }

    /**
     * 设置信息值
     * @param value 信息值
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * 获得信息描述
     * @return 信息描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置信息描述
     * @param description 信息描述
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
