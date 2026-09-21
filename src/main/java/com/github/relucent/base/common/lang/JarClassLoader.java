package com.github.relucent.base.common.lang;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import com.github.relucent.base.common.io.FileUtil;
import com.github.relucent.base.common.net.UrlUtil;

/**
 * 外部 Jar 文件类加载器。<br>
 * 用于在运行时动态加载指定目录、Jar 文件以及 Class 文件目录中的类。<br>
 * 该类基于 {@link URLClassLoader} 实现，支持 JDK 8 至 JDK 21。<br>
 * 与直接修改系统 ClassLoader 不同，本类创建独立的类加载器， 并以指定的 ClassLoader 作为父加载器。<br>
 * JarClassLoader → (parent) → System ClassLoader <br>
 */
public class JarClassLoader extends URLClassLoader {

	// ============================== StaticCreate ==================================
	/**
	 * 加载指定目录中的 Jar 文件和 Class 文件。<br>
	 * 如果参数是 Jar 文件，则直接加载该 Jar 文件； 如果参数是目录，则递归加载目录中的所有 Jar 文件， 同时将该目录作为 Class 搜索目录。<br>
	 * @param dir Jar 文件或 Jar 所在目录
	 * @return JarClassLoader
	 */
	public static JarClassLoader load(File dir) {
		final JarClassLoader loader = new JarClassLoader();
		loader.addJar(dir);
		// 目录本身作为 Class 搜索路径
		if (dir != null && dir.isDirectory()) {
			loader.addURL(dir);
		}
		return loader;
	}

	/**
	 * 已过时，请使用 {@link #load(File)}。<br>
	 * 加载指定目录中的 Jar 文件和 Class 文件，功能与 {@link #load(File)} 完全一致，仅为向后兼容保留。<br>
	 * @param dir Jar 文件或 Jar 所在目录
	 * @return JarClassLoader
	 * @deprecated 直接使用 {@link #load(File)} 即可；本方法保留过渡期，将在未来版本移除
	 */
	@Deprecated
	public static JarClassLoader loadJar(File dir) {
		return load(dir);
	}

	/**
	 * 创建一个以系统 ClassLoader 为父加载器的 JarClassLoader， 并加载指定的 Jar 文件。<br>
	 * JDK 9 开始，系统 ClassLoader 不再保证是 {@link URLClassLoader}，因此不能通过强制类型转换后调用 {@code URLClassLoader.addURL()} 的方式向系统
	 * ClassLoader 动态添加 Jar。<br>
	 * 本方法保留原有 {@code loadJarToSystemClassLoader} 方法的 API， 但实际实现为创建一个以系统 ClassLoader 为父加载器的 {@link JarClassLoader}。<br>
	 * @param jarFile Jar 文件或 Jar 所在目录
	 * @return 以系统 ClassLoader 为父加载器的 JarClassLoader
	 */
	public static JarClassLoader loadJarToSystemClassLoader(File jarFile) {
		return loadJarToClassLoader(jarFile, ClassLoader.getSystemClassLoader());
	}

	/**
	 * 创建一个以指定 ClassLoader 为父加载器的 JarClassLoader， 并加载指定的 Jar 文件。
	 * @param jarFile Jar 文件或 Jar 所在目录
	 * @param parent  父 ClassLoader
	 * @return JarClassLoader
	 */
	public static JarClassLoader loadJarToClassLoader(File jarFile, ClassLoader parent) {
		final JarClassLoader loader = new JarClassLoader(new URL[0], parent);
		loader.addJar(jarFile);
		return loader;
	}

	// ============================== Constructors ==================================
	/**
	 * 创建一个空的 JarClassLoader，默认使用当前类的 ClassLoader 作为父加载器。
	 */
	public JarClassLoader() {
		this(new URL[0]);
	}

	/**
	 * 根据指定 URL 创建 JarClassLoader，默认使用当前类的 ClassLoader 作为父加载器。
	 * @param urls 初始加载 URL
	 */
	public JarClassLoader(URL[] urls) {
		this(urls, JarClassLoader.class.getClassLoader());
	}

	/**
	 * 根据指定 URL 和父 ClassLoader 创建 JarClassLoader。
	 * @param urls   初始加载 URL
	 * @param parent 父 ClassLoader
	 */
	public JarClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}

	// ============================== Methods ========================================

	/**
	 * 加载指定的 Jar 文件或目录。<br>
	 * 如果参数是 Jar 文件，则直接加载该 Jar 文件； 如果参数是目录，则递归查找并加载目录中的所有 Jar 文件。<br>
	 * @param jarFileOrDir Jar 文件或包含 Jar 文件的目录
	 */
	public void addJar(File jarFileOrDir) {

		if (jarFileOrDir == null) {
			return;
		}

		if (isJarFile(jarFileOrDir)) {
			addURL(jarFileOrDir);
			return;
		}

		final List<File> jars = loopJar(jarFileOrDir);

		for (File jar : jars) {
			addURL(jar);
		}
	}

	/**
	 * 添加一个 URL 到当前类加载器的搜索路径。<br>
	 * @param url 要添加的 URL
	 */
	@Override
	public void addURL(URL url) {
		super.addURL(url);
	}

	/**
	 * 添加一个文件或目录到当前类加载器的搜索路径。<br>
	 * 如果是目录，则该目录用于搜索 Class 文件； 如果是 Jar 文件，则加载该 Jar 文件。<br>
	 * @param file 文件或目录
	 */
	public void addURL(File file) {
		if (file == null) {
			return;
		}
		addURL(UrlUtil.getURL(file));
	}

	// ============================== PrivateMethods ================================

	/**
	 * 递归查找指定文件或目录中的 Jar 文件。 <br>
	 * @param file Jar 文件或包含 Jar 文件的目录
	 * @return 找到的 Jar 文件列表
	 */
	private static List<File> loopJar(File file) {
		return FileUtil.loopFiles(file, JarClassLoader::isJarFile);
	}

	/**
	 * 判断指定文件是否为 Jar 文件。<br>
	 * @param file 文件
	 * @return 如果是 Jar 文件返回 {@code true}，否则返回 {@code false}
	 */
	private static boolean isJarFile(File file) {
		if (!FileUtil.isFile(file)) {
			return false;
		}
		return file.getPath().toLowerCase().endsWith(".jar");
	}
}