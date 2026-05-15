package com.github.relucent.base.common.matcher;

import java.util.Map;

/**
 * 模板匹配工具类，用于按模板匹配字符串。<br>
 * 支持占位符形式 ${name} 或 {name}，匹配成功返回 true。 <br>
 * 适用于简单模板匹配，不解析复杂逻辑。<br>
 */
public class TemplateMatcher {

    private final String template;

    /**
     * 构造方法
     * @param template 模板字符串，例如 "Hello ${name}, your id is ${id}"
     */
    public TemplateMatcher(String template) {
        this.template = template;
    }

    /**
     * 判断输入字符串是否符合模板 占位符用 values 替换，如果替换后与原模板相同，则匹配成功
     * @param input  待匹配字符串
     * @param values 占位符对应的值，例如 Map.of("name","Tom","id","123")
     * @return true 如果匹配成功，否则 false
     */
    public boolean matches(String input, Map<String, String> values) {
        String replaced = template;
        for (Map.Entry<String, String> entry : values.entrySet()) {
            replaced = replaced.replace("${" + entry.getKey() + "}", entry.getValue());
            replaced = replaced.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return replaced.equals(input);
    }

}
