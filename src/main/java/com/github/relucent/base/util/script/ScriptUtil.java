package com.github.relucent.base.util.script;

/**
 * 脚本工具类
 * @author YYL
 */
public class ScriptUtil {

    /**
     * 获得脚本引擎实例
     * @param name 脚本名称
     * @return 脚本引擎实例
     */
    public static ScriptEnginex getScriptEngine(String name) {
        return new ScriptEnginex(name);
    }

    /**
     * 获得 JavaScript 脚本引擎实例
     * @return JavaScript 脚本引擎实例
     */
    public static JavaScriptEngine getJavaScriptEngine() {
        return new JavaScriptEngine();
    }
}
