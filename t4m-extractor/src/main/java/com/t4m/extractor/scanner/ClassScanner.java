package com.t4m.extractor.scanner;

import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.DirectoryNode;
import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.entity.ProjectInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 通过反射来扫描类信息 Created by Yuxiang Liao on 2020-06-16 13:42.
 */
public class ClassScanner {

	public static final Logger LOGGER = LoggerFactory.getLogger(ClassScanner.class);

	/**
	 * 对于列表中的每个File对象，从中读取信息，并转化为{@code ClassInfo}对象. <br> 包括{@code absolutePath}, {@code packageFullyQualifiedName}.
	 */
	public static List<ClassInfo> scan(ProjectInfo projectInfo, List<File> rawJavaFileList) {
		rawJavaFileList.forEach(javaFile -> {
			try {
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
				String classShortName = javaFile.getName().split("\\.")[0];
				String classFullyQualifiedName = pkgFullyQualifiedName + "." + classShortName;
				ClassInfo classInfo = projectInfo.safeAddClassList(
						new ClassInfo(classFullyQualifiedName, javaFile.getAbsolutePath().strip()));
				classInfo.setShortName(classShortName);
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
	 * 补全classInfo的信息
	 */
	public static void revealClassInfo(ClassInfo classInfo) {
		try {
			Class currentClazz = Class.forName(classInfo.getFullyQualifiedName());
			findAndAddInnerClass(currentClazz, classInfo);

		} catch (ClassNotFoundException e) {
			LOGGER.warn("Cannot reflect Class object by {}. [{}]", classInfo.getFullyQualifiedName(), e.toString(), e);
		}
	}

	/**
	 * 创建内部类对应的ClassInfo,并加入到当前classInfo对象的{@code innerClassList}中
	 */
	public static void findAndAddInnerClass(Class currentClazz, ClassInfo classInfo) {
		Arrays.stream(currentClazz.getClasses()).forEach(clazz -> {
			ClassInfo innerClassInfo = classInfo.safeAddInnerClassList(
					new ClassInfo(clazz.getName(), classInfo.getAbsolutePath()));
			innerClassInfo.setShortName(clazz.getSimpleName());
			innerClassInfo.setPackageFullyQualifiedName(classInfo.getFullyQualifiedName());
			classInfo.getPackageInfo().safeAddClassList(innerClassInfo);
		});
	}

	public static void main(String[] args) {
		String rootPath = "/Users/liao/myProjects/IdeaProjects/sonarqube";
		ProjectInfo projectInfo = new ProjectInfo(rootPath);
		List<File> rawJavaFileList = new ArrayList<>();
		DirectoryScanner.scan(projectInfo, rawJavaFileList);

	}
}
