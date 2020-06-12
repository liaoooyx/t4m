package com.t4m.extractor.scanner;

import com.t4m.extractor.entity.ProjectInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Yuxiang Liao on 2020-06-10 10:57.
 */
public class Scanner {

	private static String JAVA_FILE_IDENTIFIER = "src";


	private ProjectInfo projectInfo = new ProjectInfo();

	public Scanner(String projectPath) {
		projectInfo.setRootPath(projectPath);
	}

	public static void printFileInfo(File file, String previousPath) {

		if (file.isDirectory()) {
			System.out.printf("(Directory) %s\\%s %n", previousPath, file.getName());
			File[] fileArray = file.listFiles();
			Arrays.stream(fileArray).forEach(f -> {
				printFileInfo(f, previousPath + "\\" + file.getName());
			});
		} else {
			System.out.printf("(File) %s %n", file.getName());
			System.out.printf("(File) %s %n", file.getAbsoluteFile());
			return;
		}
	}

	/**
	 * Add all files absolute path to {@code fileList} based on root {@code file}
	 *
	 * @param file root project path
	 * @param fileList to store all of the files
	 */
	public static void getAllfiles(File file, List<String> fileList) {
		if (file.isDirectory()) {
			File[] fileArray = file.listFiles();
			Arrays.stream(fileArray).forEach(f -> {
				getAllfiles(f, fileList);
			});
		} else {
			fileList.add(file.getAbsolutePath());
		}
	}

	/**
	 * Only remain file with .java suffix.
	 *
	 * @param fileList A list of file path.
	 */
	public static List<String> filterNonJavaFile(List<String> fileList) {
		return fileList.stream().filter(f -> f.endsWith(".java")).collect(Collectors.toList());
	}

	public static List<String> getJavaFileList(File projectPath) {
		List<String> javaList = new ArrayList<>();
		getAllfiles(projectPath, javaList);
		return filterNonJavaFile(javaList);
	}

	public static void main(String[] args) {
		String path = "/Users/liao/myProjects/IdeaProjects/comp5911m/refactor";

		ProjectInfo mainProject = new ProjectInfo();
		ProjectInfo testProject = new ProjectInfo();

		File root = new File(path);
		// printFileInfo(root, ".");
		// List<String> fileList = new ArrayList<>();
		// getAllfiles(root, fileList);
		getJavaFileList(root).forEach(System.out::println);

	}
}
