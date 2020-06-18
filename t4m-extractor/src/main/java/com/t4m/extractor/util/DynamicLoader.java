package com.t4m.extractor.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Yuxiang Liao on 2020-06-17 05:22.
 */
@Deprecated
public class DynamicLoader {

	public static final Logger LOGGER = LoggerFactory.getLogger(DynamicLoader.class);

	/**
	 * auto fill in the java-name with code, return null if cannot find the public class
	 *
	 * @param javaSrc source code string
	 * @return return the Map, the KEY means ClassName, the VALUE means bytecode.
	 */
	public static Map<String, byte[]> compile(String javaSrc) {
		Pattern pattern = Pattern.compile("public\\s+class\\s+(\\w+)");
		Matcher matcher = pattern.matcher(javaSrc);
		if (matcher.find())
			return compile(matcher.group(1) + ".java", javaSrc);
		return null;
	}

	/**
	 * @param javaName the name of your public class,eg: <code>TestClass.java</code>
	 * @param javaSrc source code string
	 * @return return the Map, the KEY means ClassName, the VALUE means bytecode.
	 */
	public static Map<String, byte[]> compile(String javaName, String javaSrc) {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager stdManager = compiler.getStandardFileManager(null, null, null);
		JavaFileObject javaFileObject = MemoryJavaFileManager.initStringSource(javaName, javaSrc);
		try (MemoryJavaFileManager manager = new MemoryJavaFileManager(stdManager)) {
			JavaCompiler.CompilationTask task = compiler.getTask(null, manager, null, null, null,
			                                                     Collections.singletonList(javaFileObject));
			if (task.call())
				return manager.getClassBytesMap();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 同时编译所有Java文件，避免找不到依赖的类
	 */
	public static Map<String, byte[]> compile(List<JavaFileObject> javaFileObjectList) {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager stdManager = compiler.getStandardFileManager(null, null, null);
		try (MemoryJavaFileManager manager = new MemoryJavaFileManager(stdManager)) {
			JavaCompiler.CompilationTask task = compiler.getTask(null, manager, null, null, null, javaFileObjectList);
			if (task.call())
				return manager.getClassBytesMap();
		} catch (IOException e) {
			LOGGER.error("",e);
		}
		return null;
	}

	public static class MemoryClassLoader extends URLClassLoader {
		Map<String, byte[]> classBytesMap = new HashMap<>();

		public MemoryClassLoader(Map<String, byte[]> classBytesMap) {
			super(new URL[0], MemoryClassLoader.class.getClassLoader());
			this.classBytesMap.putAll(classBytesMap);
		}

		@Override
		public Class<?> findClass(String name) throws ClassNotFoundException {
			byte[] buf = classBytesMap.get(name);
			if (buf == null) {
				return super.findClass(name);
			}
			classBytesMap.remove(name);
			return defineClass(name, buf, 0, buf.length);
		}

	}

}

