/**
 * 动态类编译器程序工具类。<br>
 * 在非常多Java应用中需要在程式中调用Java编译器来编译和运行。<br>
 * 在早期的版本中(Java SE5及以前版本)中只能通过tools.jar中的com.sun.tools.javac包来调用Java编译器，但由于tools.jar不是标准的Java库，在使用时必须要设置这个jar的路径。<br>
 * 在Java SE6中为我们提供了标准的包来操作Java编译器，这就是javax.tools包。<br>
 * @author YYL
 */
package com.github.relucent.base.common.compiler;
