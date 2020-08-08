package com.github.relucent.base.common.script;

import javax.script.Compilable;
import javax.script.Invocable;
import javax.script.ScriptEngine;

/**
 * 全功能引擎接类，支持 ScriptEngine, Compilable 和 Invocable.
 * @author YYL
 */
public interface ScriptEnginex extends ScriptEngine, Compilable, Invocable {

}
