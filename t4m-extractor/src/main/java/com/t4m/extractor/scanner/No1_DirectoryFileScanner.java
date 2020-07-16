package com.t4m.extractor.scanner;

import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.util.PropertyUtil;
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
public class No1_DirectoryFileScanner {

	public static final Logger LOGGER = LoggerFactory.getLogger(No1_DirectoryFileScanner.class);

	public static String[] exclusions = PropertyUtil.getProperty("EXCLUDED_PATH").split(";");

	private ProjectInfo projectInfo;

	private List<File> rawJavaFileList = new ArrayList<>();

	public No1_DirectoryFileScanner(ProjectInfo projectInfo) {
		this.projectInfo = projectInfo;
	}

	/**
	 * 扫描项目中所有的java文件，并返回{@code rawJavaFileList}
	 */
	public List<File> scan() {
		File root = new File(projectInfo.getAbsolutePath());
		getAllJavaFiles(root, rawJavaFileList);
		return rawJavaFileList;
	}

	/**
	 * 从项目跟路径开始，递归查找所有.java文件，指定的{@code exclusions}目录将被排除
	 *
	 * @param file 文件
	 * @param javaList 储存所有.java文件
	 */
	private void getAllJavaFiles(File file, List<File> javaList) {
		if (file.isDirectory()) {
			File[] fileArray = file.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					for (String exclusion : exclusions) {
						if (pathname.getAbsolutePath().contains(exclusion))
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
		ProjectInfo projectInfo = new ProjectInfo("/Users/liao/myProjects/IdeaProjects/sonarqube");
		No1_DirectoryFileScanner directoryFileScanner = new No1_DirectoryFileScanner(projectInfo);
		directoryFileScanner.scan();
	}

}
