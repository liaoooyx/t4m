package com.t4m.extractor.scanner;

import com.t4m.extractor.entity.ProjectInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Yuxiang Liao on 2020-06-17 02:43.
 */
public class No1_DirectoryFileScanner implements T4MScanner {

	public static final Logger LOGGER = LoggerFactory.getLogger(No1_DirectoryFileScanner.class);

	// private final ProjectInfo projectInfo;
	//
	// private final List<File> rawJavaFileList = new ArrayList<>();
	//
	// public No1_DirectoryFileScanner(ProjectInfo projectInfo) {
	// 	this.projectInfo = projectInfo;
	// }

	@Override
	public void scan(ProjectInfo projectInfo, ScannerChain scannerChain) {
		LOGGER.info("Scanning all .java file from [{}]", projectInfo.getAbsolutePath());
		List<File> rawJavaFileList = new ArrayList<>();
		File root = new File(projectInfo.getAbsolutePath());
		getAllJavaFiles(root, rawJavaFileList, projectInfo);
		scannerChain.setRawJavaFileList(rawJavaFileList);
		scannerChain.scan(projectInfo);
	}

	// /**
	//  * 扫描项目中所有的java文件，并返回{@code rawJavaFileList}
	//  */
	// public List<File> scan() {
	// 	LOGGER.info(
	// 			"************************************* Start scanning the project **************************************");
	// 	LOGGER.info("Scanning all .java file from [{}]", projectInfo.getAbsolutePath());
	// 	File root = new File(projectInfo.getAbsolutePath());
	// 	getAllJavaFiles(root, rawJavaFileList);
	// 	return rawJavaFileList;
	// }

	/**
	 * 从项目跟路径开始，递归查找所有.java文件，指定的{@code exclusions}目录将被排除
	 */
	private void getAllJavaFiles(File file, List<File> javaList, ProjectInfo projectInfo) {
		if (file.isDirectory()) {
			File[] fileArray = file.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					String[] exclusions = projectInfo.getExcludedPath().split(";");
					for (String exclusion : exclusions) {
						if (!"".equals(exclusion) && pathname.getAbsolutePath().contains(exclusion))
							return false;
					}
					return true;
				}
			});
			Arrays.stream(fileArray).forEach(f -> {
				getAllJavaFiles(f, javaList, projectInfo);
			});
		} else {
			if (file.getName().endsWith(".java")) {
				javaList.add(file);
			}
		}
	}


}
