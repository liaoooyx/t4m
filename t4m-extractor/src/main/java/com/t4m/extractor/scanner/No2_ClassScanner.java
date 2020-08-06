package com.t4m.extractor.scanner;

import com.t4m.extractor.T4MExtractor;
import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.metric.SLOCMetric;
import com.t4m.extractor.util.EntityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

/**
 * 通过反射来扫描类信息 Created by Yuxiang Liao on 2020-06-16 13:42.
 */
public class No2_ClassScanner {

	public static final Logger LOGGER = LoggerFactory.getLogger(No2_ClassScanner.class);

	private ProjectInfo projectInfo;

	public No2_ClassScanner(ProjectInfo projectInfo) {
		this.projectInfo = projectInfo;
	}

	/**
	 * 对于列表中的每个File对象，从中读取信息，并转化为{@code ClassInfo}对象. <br> 包括{@code absolutePath}, {@code packageFullyQualifiedName}.
	 */
	public void scan(List<File> rawJavaFileList) {
		LOGGER.info("Extracting the basic information from .java files. Extracting the basic information of class level.");
		rawJavaFileList.forEach(javaFile -> {
			try {
				String line;
				String pkgFullyQualifiedName = PackageInfo.EMPTY_IDENTIFIER;
				BufferedReader reader = new BufferedReader(new FileReader(javaFile));
				// 保证类的唯一性
				String classShortName = javaFile.getName().split("\\.")[0];
				// init ClassInfo
				ClassInfo classInfo = EntityUtil.safeAddEntityToList(
						new ClassInfo(classShortName, javaFile.getAbsolutePath().strip()), projectInfo.getClassList());
				// SLOC from source file
				SLOCMetric slocMetric = new SLOCMetric();
				while ((line = reader.readLine()) != null) {
					String currentLine = line.strip();
					// 读java文件的包路径
					if (currentLine.startsWith("package ")) {
						pkgFullyQualifiedName = line.replaceFirst("package", "").replace(";", "").strip();
					}
					// sloc计数
					slocMetric.countSLOCByLine(currentLine);
				}
				slocMetric.setSourceFileSLOCToCounterMap(classInfo.getSlocCounterMap());
				classInfo.setMainPublicClass(classInfo);
				classInfo.setClassDeclaration(ClassInfo.ClassDeclaration.PUBLIC_OUTER_CLASS);
				classInfo.setFullyQualifiedName(pkgFullyQualifiedName + "." + classShortName);
				classInfo.setPackageFullyQualifiedName(pkgFullyQualifiedName);
				if ("package-info".equals(classShortName)){
					classInfo.setClassModifier(ClassInfo.ClassModifier.NONE);
				}

			} catch (FileNotFoundException e) {
				LOGGER.error("No such file to be converted to ClassInfo object.%n[{}]", e.toString(), e);
			} catch (IOException e) {
				LOGGER.error("Error happened when finding package path. [{}]", e.toString(), e);
			}
		});
	}
}
