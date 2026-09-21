package com.github.relucent.base.common.lang;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;

import org.junit.Assert;
import org.junit.Test;

public class JarClassLoaderTest {

	@Test
	public void testLoadFromDirectory() throws ClassNotFoundException {
		JarClassLoader loader = JarClassLoader.load(new File("."));
		Assert.assertNotNull(loader);
		Assert.assertTrue(loader instanceof URLClassLoader);
		// 基础类可经父加载器解析
		Assert.assertEquals(String.class, loader.loadClass("java.lang.String"));
	}

	@Test
	public void testLoadNonJarFile() {
		// 传入非 jar 文件不应抛异常，仅返回未加载任何 jar 的加载器
		JarClassLoader loader = JarClassLoader.load(new File("pom.xml"));
		Assert.assertNotNull(loader);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testLoadJarDeprecatedForwarding() {
		// @Deprecated 过渡方法应等价于 load(File)，保留过渡期向后兼容
		JarClassLoader loader = JarClassLoader.loadJar(new File("pom.xml"));
		Assert.assertNotNull(loader);
	}

	@Test
	public void testLoadJarToSystemClassLoader() throws Exception {
		// 回归用例：确保 JDK8 下不会因 (URLClassLoader) getSystemClassLoader() 强转而崩溃，
		// 且始终返回以系统 ClassLoader 为父的可用 URLClassLoader（JDK9+ 走新建子加载器分支）。
		File tmp = Files.createTempDirectory("jarcl-test").toFile();
		URLClassLoader loader = JarClassLoader.loadJarToSystemClassLoader(tmp);
		Assert.assertNotNull(loader);
		Assert.assertTrue(loader instanceof URLClassLoader);
		// 重构后：父加载器为系统 ClassLoader（不再是往系统加载器注入 jar）
		Assert.assertSame(ClassLoader.getSystemClassLoader(), loader.getParent());
	}

	@Test
	public void testLoadJarToClassLoader() throws Exception {
		// 新增 API：父加载器可定制
		ClassLoader parent = new ClassLoader() {
		};
		JarClassLoader loader = JarClassLoader.loadJarToClassLoader(new File("pom.xml"), parent);
		Assert.assertNotNull(loader);
		Assert.assertSame(parent, loader.getParent());
	}

	@Test
	public void testNullSafe() {
		// 构造函数与 add* 对 null 不崩
		JarClassLoader loader = new JarClassLoader();
		loader.addJar(null);
		loader.addURL((File) null);
		loader.addURL((URL) null);
		Assert.assertNotNull(loader);
	}
}
