package com.t4m.extractor.scanner;

import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.ModuleInfo;
import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.entity.ProjectInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Construct all of the entities Created by Yuxiang Liao on 2020-06-11 09:46.
 */
public class EntityScanner {

	public static final Logger LOGGER = LoggerFactory.getLogger(EntityScanner.class);

	public static String[] exclusions = {"build"};

	/**
	 * 扫描项目，自底向上构建所有实体对象：Classes->Packages->Modules。实体将以List的形式存放在Project对象中。
	 */
	public static ProjectInfo scan(ProjectInfo projectInfo) {
		File root = new File(projectInfo.getAbsolutePath());
		List<File> rawJavaFileList = new ArrayList<>();
		getAllJavaFiles(root, rawJavaFileList);
		// 初始化所有Class
		List<ClassInfo> classInfoList = extractClassFromFileList(projectInfo, rawJavaFileList);
		LOGGER.debug("类总数: {}", classInfoList.size());
		// 初始化所有Package
		List<PackageInfo> packageInfoList = extractPackageFromClassList(projectInfo, classInfoList);
		LOGGER.debug("包总数: {}", packageInfoList.size());
		// 初始化所有Module
		List<ModuleInfo> moduleInfoList = extractModuleFromPackageList(projectInfo, packageInfoList);
		LOGGER.debug("模块总数: {}", moduleInfoList.size());
		return projectInfo;
	}

	/**
	 * 对于列表中的每个File对象，从中读取信息，并转化为{@code ClassInfo}对象. <br> 包括{@code absolutePath}, {@code packageFullyQualifiedName}.
	 */
	private static List<ClassInfo> extractClassFromFileList(ProjectInfo projectInfo, List<File> rawJavaFileList) {
		rawJavaFileList.forEach(javaFile -> {
			try {
				// throw new FileNotFoundException();
				String line;
				String pkgFullyQualifiedName = PackageInfo.EMPTY_IDENTIFIER;
				BufferedReader reader = new BufferedReader(new FileReader(javaFile));
				while ((line = reader.readLine()) != null) {
					// 读java文件的包路径
					if (line.startsWith("package")) {
						pkgFullyQualifiedName = line.replaceFirst("package", "").replace(";", "").strip();
						break;
					}
				}
				// 保证类的唯一性
				ClassInfo classInfo = projectInfo.safeAddClassList(new ClassInfo(javaFile.getAbsolutePath().strip()));
				String classShortName = javaFile.getName().split("\\.")[0];
				classInfo.setShortName(classShortName);
				classInfo.setFullyQualifiedName(pkgFullyQualifiedName + "." + classShortName);
				classInfo.setPackageFullyQualifiedName(pkgFullyQualifiedName);

			} catch (FileNotFoundException e) {
				LOGGER.warn("No such file to be converted to ClassInfo object.%n[{}]", e.toString(), e);
			} catch (IOException e) {
				LOGGER.warn("Error happened when finding package path. [{}]", e.toString(), e);
			}
		});
		return projectInfo.getClassList();
	}

	/**
	 * 对于列表中的每个{@code ClassInfo}对象，从中读取信息，并转化为{@code PackageInfo}对象. <br> 包括{@code fullyQualifiedName},{@code
	 * absolutePath}, 并添加{@code classInfo}到{@code classList}中
	 */
	private static List<PackageInfo> extractPackageFromClassList(
			ProjectInfo projectInfo, List<ClassInfo> classInfoList) {
		classInfoList.forEach(classInfo -> {
			String pkgAbsolutePath = classInfo.getAbsolutePath().replaceFirst("/{1}?[^/]*?\\.java", "").strip();
			// 保证包的唯一性
			PackageInfo packageInfo = projectInfo.safeAddPackageList(new PackageInfo(pkgAbsolutePath));
			packageInfo.setFullyQualifiedName(classInfo.getPackageFullyQualifiedName());
			packageInfo.safeAddClassList(classInfo);
			classInfo.setPackageInfo(packageInfo);
		});
		return projectInfo.getPackageList();
	}

	/**
	 * 根据列表中的{@code PackageInfo}创建模块信息
	 */
	private static List<ModuleInfo> extractModuleFromPackageList(
			ProjectInfo projectInfo, List<PackageInfo> packageInfoList) {
		packageInfoList.forEach(packageInfo -> {
			String pkgFullName = packageInfo.getFullyQualifiedName();
			String pkgPath = packageInfo.getAbsolutePath();
			// 去除包名路径，得到模块添加路径
			String regx = "";
			if (!PackageInfo.EMPTY_IDENTIFIER.equals(packageInfo.getFullyQualifiedName())) {
				regx = File.separator + pkgFullName.replaceAll("\\.", File.separator) + "$";
			}
			String moduleAbsolutePathWithSuffix;
			if (!"".equals(regx)) {
				moduleAbsolutePathWithSuffix = pkgPath.replaceAll(regx, "").strip();
			} else {
				moduleAbsolutePathWithSuffix = pkgPath.strip();
			}
			String regex =
					File.separator + "src(" + File.separator + "main|" + File.separator + "test)" + File.separator +
							"java$"; // "/src(/main|/test)/java"
			String moduleAbsolutePath = moduleAbsolutePathWithSuffix.replaceAll(regex, "");
			// 保证模块的唯一性
			ModuleInfo moduleInfo = projectInfo.safeAddModuleList(new ModuleInfo(moduleAbsolutePath));
			// 为模块添加子包，分为3个域：main，test，other。包括域路径和域下的包
			// 包在加入列表中时，已去重
			if (moduleAbsolutePathWithSuffix.contains(File.separator + "main")) {
				moduleInfo.safeAddMainPackageList(packageInfo);
				moduleInfo.setMainScopePath(moduleAbsolutePathWithSuffix);
			} else if (moduleAbsolutePathWithSuffix.contains(File.separator + "test")) {
				moduleInfo.safeAddTestPackageList(packageInfo);
				moduleInfo.setTestScopePath(moduleAbsolutePathWithSuffix);
			} else {
				moduleInfo.safeAddOtherPackageList(packageInfo);
				moduleInfo.setOtherScopePath(moduleAbsolutePathWithSuffix);
			}
		});
		return projectInfo.getModuleList();
	}

	/**
	 * 从项目跟路径开始，递归查找所有.java文件，指定的{@code exclusions}目录将被排除
	 *
	 * @param file 文件
	 * @param javaList 储存所有.java文件
	 */
	private static void getAllJavaFiles(File file, List<File> javaList) {
		if (file.isDirectory()) {
			File[] fileArray = file.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					for (String exclusion : exclusions) {
						if (exclusion.equals(pathname.getName()))
							return false;
					}
					return true;
				}
			});
			Arrays.stream(fileArray).forEach(f -> {
				getAllJavaFiles(f, javaList);
			});
		} else {
			if (file.getName().endsWith(".java")) {
				javaList.add(file);
			}
		}
	}

	public static void main(String[] args) {

		// ProjectInfo projectInfo = new ProjectInfo("/Users/liao/myProjects/IdeaProjects/comp5911m/refactor");
		ProjectInfo projectInfo = new ProjectInfo("/Users/liao/myProjects/IdeaProjects/sonarqube");

		EntityScanner.scan(projectInfo);
	}
}
