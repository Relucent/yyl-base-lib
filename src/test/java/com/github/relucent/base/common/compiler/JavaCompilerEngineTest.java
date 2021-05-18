package com.github.relucent.base.common.compiler;

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;

public class JavaCompilerEngineTest {
    @Test
    public void compileTest() throws Exception {
        JavaCompilerEngine compileEngine = new JavaCompilerEngine();
        String name = "yyl.example.basic.jdk6.Hello";
        String source = ""//
                + "package yyl.example.basic.jdk6;\n"//
                + "public class Hello{\n"//
                + "    public static String hello(String value) {\n"//
                + "        return \"Hello, \" + value;\n"//
                + "    }\n"//
                + "}\n";
        Class<?> clazz = compileEngine.compile(name, source);
        Method method = clazz.getMethod("hello", String.class);
        String actual = (String) method.invoke(null, "JavaCompiler");
        String expected = "Hello, JavaCompiler";
        Assert.assertEquals(expected, actual);
    }
}
