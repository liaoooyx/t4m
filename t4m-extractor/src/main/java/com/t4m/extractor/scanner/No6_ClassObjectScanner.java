package com.t4m.extractor.scanner;

import com.t4m.extractor.T4MExtractor;
import com.t4m.extractor.entity.DirectoryNode;
import com.t4m.extractor.entity.ModuleInfo;
import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Yuxiang Liao on 2020-06-18 01:12.
 */
public class No6_ClassObjectScanner {

	public static final Logger LOGGER = LoggerFactory.getLogger(No6_ClassObjectScanner.class);

	private static final String TEMP_COMPILE_OUTPUT_PATH = PropertyUtil.getProperty("TEMP_COMPILE_OUTPUT_PATH");

	private ProjectInfo projectInfo;

	public No6_ClassObjectScanner(ProjectInfo projectInfo) {
		this.projectInfo = projectInfo;
	}

	public void scan() {
		projectInfo.getModuleList().forEach(moduleInfo -> {
			String moduleTempCompileOutputPath = moduleInfo.getTempCompileOutputPath();
			File outputDir = checkOutputDir(moduleTempCompileOutputPath);
			// 虽然生成了模块的临时输出文件夹，但是否生成.class文件另说
			List<String> javaSourceFilePathList = getJavaSourceFilePathFromModule(moduleInfo);
			try {
				if (javaSourceFilePathList != null) {
					boolean success = compile(outputDir, javaSourceFilePathList);
					if (!success) {
						LOGGER.info("Failed to compile module {}", moduleInfo.getRelativePath());
					}
				} else {
					LOGGER.info("Module {} does not contain java file in non-test scope. \r\nPath of this module: {}",
					            moduleInfo.getRelativePath(), moduleInfo.getAbsolutePath());
				}
			} catch (IOException e) {
				LOGGER.error("Error happened when compiling module {}. [{}]", moduleInfo.getRelativePath(),
				             e.toString(), e);
			}
		});
	}


	private File checkOutputDir(String tempOutputPath) {
		File outputDir = new File(tempOutputPath);
		if (!outputDir.exists()) {
			try {
				if (!outputDir.mkdirs()) {
					LOGGER.error("Cannot create temporary output directory {}", tempOutputPath);
				}
			} catch (Exception e) {
				LOGGER.error("Error happen when creating temporary output directory for target project java file. [{}]",
				             e.toString(), e);
			}
		}
		return outputDir;
	}

	/**
	 * 返回模块中所有非测试类的绝对路径。模块下如果模块下只有测试类，则返回null。
	 */
	private List<String> getJavaSourceFilePathFromModule(ModuleInfo moduleInfo) {
		if (moduleInfo.hasMainPackageList()) {
			return getJavaSourceFilePathFromPackageList(moduleInfo.getMainPackageList());
		} else if (moduleInfo.hasOtherPackageList()) {
			return getJavaSourceFilePathFromPackageList(moduleInfo.getOtherPackageList());
		} else {
			return null;
		}
	}

	private List<String> getJavaSourceFilePathFromPackageList(List<PackageInfo> packageInfoList) {
		List<String> javaSourceFileList = new ArrayList<>();
		packageInfoList.forEach(packageInfo -> {
			packageInfo.getClassList().forEach(classInfo -> {
				javaSourceFileList.add(classInfo.getAbsolutePath());
			});
		});
		return javaSourceFileList;
	}

	private boolean compile(File outputDir, List<String> javaFilePathList) throws IOException {
		StandardJavaFileManager fileManager = null;
		try {
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
			// 建立用于保存被编译文件名的对象
			// 每个文件被保存在一个从JavaFileObject继承的类中
			fileManager = compiler.getStandardFileManager(diagnostics, null, null);
			JavaFileManager.Location oLocation = StandardLocation.CLASS_OUTPUT;
			fileManager.setLocation(oLocation, Arrays.asList(outputDir));
			Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromStrings(
					javaFilePathList);
			JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null,
			                                                     compilationUnits);
			boolean result = task.call();
			if (!result) {
				for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
					// read error dertails from the diagnostic object
					LOGGER.warn(diagnostic.toString());
				}
			}
			return result;
		} finally {
			if (fileManager != null) {
				fileManager.close();
			}
		}
	}

	public static void main(String[] args) {
		String rootPath = "/Users/liao/myProjects/IdeaProjects/sonarqube";
		ProjectInfo projectInfo = new ProjectInfo(rootPath);
		T4MExtractor t4MExtractor = new T4MExtractor(projectInfo);
		t4MExtractor.scanDependency();
		No6_ClassObjectScanner classObjectScanner = new No6_ClassObjectScanner(projectInfo);
		classObjectScanner.scan();
	}
}
