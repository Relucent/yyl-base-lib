package com.github.relucent.base.common.script;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

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
        ScriptEngine engine = getEngine(name);
        return new ScriptEngineWrapper(engine);
    }

    /**
     * 获得 JavaScript 脚本引擎实例
     * @return JavaScript 脚本引擎实例
     */
    public static ScriptEnginex getJavaScriptEngine() {
        return getScriptEngine("javascript");
    }

    /**
     * 根据名称获得脚本引擎
     * @param name 脚本引擎名称
     * @return 脚本引擎
     */
    private static ScriptEngine getEngine(String name) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName(name);
        if (engine == null) {
            engine = manager.getEngineByExtension(name);
        }
        if (engine == null) {
            engine = manager.getEngineByMimeType(name);
        }
        if (engine == null) {
            throw new NullPointerException("Script for [" + name + "] not support !");
        }
        return engine;
    }
}
