package com.t4m.extractor;

import org.eclipse.jdt.core.compiler.CompilationProgress;
import org.eclipse.jdt.core.compiler.batch.BatchCompiler;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Yuxiang Liao on 2020-06-17 20:17.
 */
public class Test {

	public void compileAllClass(List<String> javaFilePathList) throws IOException, ClassNotFoundException {

		String tempOutputPath = System.getProperty("user.dir") + "/out/temp/";
		File outputDir = new File(tempOutputPath);
		if (!outputDir.exists()) {
			try {
				if (!outputDir.mkdirs()) {
					// LOGGER.error("Cannot create temporary output directory {}", tempOutputPath);
				}
			} catch (Exception e) {
				// LOGGER.error("Error happen when creating temporary output directory for target project java file. [{}]",
				//              e.toString(), e);
			}
		}
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
		// 建立用于保存被编译文件名的对象
		// 每个文件被保存在一个从JavaFileObject继承的类中
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
		JavaFileManager.Location oLocation = StandardLocation.CLASS_OUTPUT;
		fileManager.setLocation(oLocation, Arrays.asList(outputDir));
		Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromStrings(
				javaFilePathList);
		JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null,
		                                                     compilationUnits);
		boolean result = task.call();
		fileManager.close();
		if (result) {
			//如果编译成功，用类加载器加载该类
			URLClassLoader classLoader = new URLClassLoader(new URL[]{new URL("file:" + tempOutputPath)});
			Class clazz = classLoader.loadClass("org.sonar.application.App");
			System.out.println(clazz.getName());
		} else {
			//如果想得到具体的编译错误，可以对Diagnostics进行扫描
			// String error = "";
			// for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
			// 	System.out.println(diagnostic);
			// }
			for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
				// read error dertails from the diagnostic object
				System.out.println(diagnostic.getMessage(null));
			}
		}

	}

	public static void javac(String[] args) throws IOException, ClassNotFoundException {
		String sOutputPath = "/Users/liao/desktop/out/temp";
		List<String> paths = new ArrayList<String>();
		paths.add(
				"/Users/liao/myProjects/IdeaProjects/comp5911m/refactor/refactor-module2/src/main/java/com/refactor/refactor3/Customer.java");
		paths.add(
				"/Users/liao/myProjects/IdeaProjects/comp5911m/refactor/refactor-module2/src/main/java/com/refactor/refactor3/Price.java");
		paths.add(
				"/Users/liao/myProjects/IdeaProjects/comp5911m/refactor/refactor-module2/src/main/java/com/refactor/refactor3/price/LuxuryPrice.java");
		paths.add(
				"/Users/liao/myProjects/IdeaProjects/comp5911m/refactor/refactor-module2/src/main/java/com/refactor/refactor3/price/NewModelPrice.java");
		paths.add(
				"/Users/liao/myProjects/IdeaProjects/comp5911m/refactor/refactor-module2/src/main/java/com/refactor/refactor3/price/StandardPrice.java");
		paths.add(
				"/Users/liao/myProjects/IdeaProjects/comp5911m/refactor/refactor-module2/src/main/java/com/refactor/refactor3/Rental.java");
		paths.add(
				"/Users/liao/myProjects/IdeaProjects/comp5911m/refactor/refactor-module2/src/main/java/com/refactor/refactor3/Car.java");

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
		// 建立用于保存被编译文件名的对象
		// 每个文件被保存在一个从JavaFileObject继承的类中
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
		JavaFileManager.Location oLocation = StandardLocation.CLASS_OUTPUT;
		fileManager.setLocation(oLocation, Arrays.asList(new File(sOutputPath)));
		Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromStrings(paths);
		JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null,
		                                                     compilationUnits);
		boolean result = task.call();
		fileManager.close();
		if (result) {
			//如果编译成功，用类加载器加载该类
			URLClassLoader classLoader = new URLClassLoader(new URL[]{new URL("file:/Users/liao/desktop/out/")});
			Class clazz = classLoader.loadClass("com.refactor.refactor3.Car");
			System.out.println(clazz.getName());
		} else {
			//如果想得到具体的编译错误，可以对Diagnostics进行扫描
			String error = "";
			for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
				System.out.println(diagnostic);
			}
		}

	}

	public static void main(String[] args) {
		List<String> paths = new ArrayList<String>();
		paths.add(
				"/Users/liao/myProjects/IdeaProjects/comp5911m/refactor/refactor-module2/src/main/java/com/refactor/refactor3/Customer.java");
		paths.add(
				"/Users/liao/myProjects/IdeaProjects/comp5911m/refactor/refactor-module2/src/main/java/com/refactor/refactor3/Price.java");
		paths.add(
				"/Users/liao/myProjects/IdeaProjects/comp5911m/refactor/refactor-module2/src/main/java/com/refactor/refactor3/price/LuxuryPrice.java");
		paths.add(
				"/Users/liao/myProjects/IdeaProjects/comp5911m/refactor/refactor-module2/src/main/java/com/refactor/refactor3/price/NewModelPrice.java");
		paths.add(
				"/Users/liao/myProjects/IdeaProjects/comp5911m/refactor/refactor-module2/src/main/java/com/refactor/refactor3/price/StandardPrice.java");
		paths.add(
				"/Users/liao/myProjects/IdeaProjects/comp5911m/refactor/refactor-module2/src/main/java/com/refactor/refactor3/Rental.java");
		paths.add(
				"/Users/liao/myProjects/IdeaProjects/comp5911m/refactor/refactor-module2/src/main/java/com/refactor/refactor3/Car.java");

		StandardJavaFileManager fileManager = null;
		CompilationProgress progress = null; // instantiate your subclass
		BatchCompiler.compile("/Users/liao/myProjects/IdeaProjects/sonarqube -source 1.8 -classpath /Users/liao/myProjects/IdeaProjects/sonarque;rt.jar -d /Users/liao/desktop/out/ -proceedOnError[]", new PrintWriter(System.out),
		                      new PrintWriter(System.err), progress);
	}
}
