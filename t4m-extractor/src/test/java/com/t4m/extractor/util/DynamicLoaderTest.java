package com.t4m.extractor.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DynamicLoaderTest {
	private String javaSrc = "public class TestClass{" + "public void sayHello(String msg) {" +
			"System.out.printf(\"Hello %s! This message from a Java String.%n\",msg);" + "}" +
			"public int add(int a,int b){" + "return a+b;" + "}" + "}";

	@Test
	public void testCompile() {
		Map<String, byte[]> bytecode = DynamicLoader.compile("TestClass.java", javaSrc);
		for (Iterator<String> iterator = bytecode.keySet().iterator(); iterator.hasNext(); ) {
			String key = iterator.next();
			byte[] code = bytecode.get(key);
			System.out.printf("Class: %s, Length: %d%n", key, code.length);
		}
		// Since the compiler and compiler options are different, the size of the bytes may be inconsistent.
		Assertions.assertEquals(558, bytecode.get("TestClass").length);
	}

	@Test
	public void testInvoke() throws ClassNotFoundException, IllegalAccessException, InstantiationException,
	                                NoSuchMethodException, InvocationTargetException {
		Random random = new Random();
		int a = random.nextInt(1024);
		int b = random.nextInt(1024);
		Map<String, byte[]> bytecode = DynamicLoader.compile("TestClass.java", javaSrc);
		DynamicLoader.MemoryClassLoader classLoader = new DynamicLoader.MemoryClassLoader(bytecode);
		Class clazz = classLoader.loadClass("TestClass");
		Object object = clazz.getDeclaredConstructor().newInstance();
		Method method = clazz.getMethod("add", int.class, int.class);
		Object returnValue = method.invoke(object, a, b);
		Assertions.assertEquals(a + b, returnValue);
	}
}