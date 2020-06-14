package com.t4m.extractor.scanner;

import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.ModuleInfo;
import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.entity.ProjectInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Construct all of the entities Created by Yuxiang Liao on 2020-06-11 09:46.
 */
public class EntityScanner {

	public String[] exclusions = {"build"};

	private ProjectInfo projectInfo;

	public EntityScanner(ProjectInfo projectInfo) {
		this.projectInfo = projectInfo;
	}

	public ProjectInfo scan() {
		File root = new File(projectInfo.getRootPath());
		List<File> javaFileList = new ArrayList<>();
		getAllJavaFiles(root, javaFileList);
		// 初始化所有Class
		List<ClassInfo> classInfoList = extractClassFromFileList(javaFileList);
		System.out.printf("类总数: %d%n", classInfoList.size());
		// 初始化所有Package
		List<PackageInfo> packageInfoList = extractPackageFromClassList(classInfoList);
		System.out.printf("包总数: %d%n", packageInfoList.size());
		// packageInfoList.forEach(i -> System.out
		// 		.printf("(%d)%s   %s %n", i.getPackagePathChain().length, i.getFullPackageName(), i.getAbsolutePath()));
		List<ModuleInfo> moduleInfoList = extractModuleFromPackageList(packageInfoList);
		System.out.printf("模块总数: %d%n", moduleInfoList.size());
		moduleInfoList.forEach(m -> {
			System.out.println(m.getMainPackageSet().size());
			// m.getPackageSet().forEach(p -> {
			// 	System.out.print(p + " | ");
			// });
			System.out.println(m);
		});
		projectInfo.setModuleList(moduleInfoList);
		projectInfo.setPackageList(packageInfoList);
		projectInfo.setClassList(classInfoList);
		return projectInfo;
	}

	/**
	 * 对于列表中的每个File对象，从中读取信息，并转化为{@code ClassInfo}对象. <br> 包括{@code absolutePath}, {@code packagePath}
	 */
	private List<ClassInfo> extractClassFromFileList(List<File> javaFileList) {
		List<ClassInfo> classInfoList = new ArrayList<>();
		javaFileList.forEach(javaFile -> {
			ClassInfo classInfo = new ClassInfo();
			classInfo.setAbsolutePath(javaFile.getAbsolutePath().strip());
			try {
				String line;
				BufferedReader reader = new BufferedReader(new FileReader(javaFile));
				while ((line = reader.readLine()) != null) {
					// 去读java文件的包路径
					if (line.startsWith("package")) {
						classInfo.setPackageFullName(line.replaceFirst("package", "").replace(";", "").strip());
						break;
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			classInfoList.add(classInfo);
		});
		return classInfoList;
	}

	/**
	 * 对于列表中的每个{@code ClassInfo}对象，从中读取信息，并转化为{@code PackageInfo}对象. <br> 包括{@code fullPackageName}, {@code
	 * packagePathChain}
	 */
	private List<PackageInfo> extractPackageFromClassList(List<ClassInfo> classInfoList) {
		List<PackageInfo> packageInfoList = new ArrayList<>();
		classInfoList.forEach(classInfo -> {
			PackageInfo packageInfo = new PackageInfo();
			String pkgFullName = classInfo.getPackageFullName();
			// 添加包全限定名
			packageInfo.setFullPackageName(pkgFullName);
			// 添加包全限定名，数组方式
			packageInfo.setPackagePathChain(pkgFullName.split("\\."));
			// 添加包的绝对路径
			packageInfo.setAbsolutePath(classInfo.getAbsolutePath().replaceFirst("/{1}?[^/]*?\\.java", "").strip());
			// 避免包重复
			int index;
			if ((index = packageInfoList.indexOf(packageInfo)) == -1) {
				packageInfoList.add(packageInfo);
			} else {
				packageInfo = packageInfoList.get(index);
			}
			// 添加直接子类信息
			packageInfo.addClassSet(classInfo);
		});
		return packageInfoList;
	}

	private List<ModuleInfo> extractModuleFromPackageList(List<PackageInfo> packageInfoList) {
		List<ModuleInfo> moduleInfoList = new ArrayList<>();
		packageInfoList.forEach(packageInfo -> {
			ModuleInfo moduleInfo = new ModuleInfo();
			String pkgFullName = packageInfo.getFullPackageName();
			String pkgPath = packageInfo.getAbsolutePath();
			// 去除包路径，并为模块添加路径
			String regx = "";
			if (!PackageInfo.EMPTY_IDENTIFIER.equals(packageInfo.getFullPackageName())) {
				regx = File.separator + pkgFullName.replaceAll("\\.", File.separator) + "$";
			}
			String absolutePath;
			if (!"".equals(regx)) {
				absolutePath = pkgPath.replaceAll(regx, "").strip();
			} else {
				absolutePath = pkgPath.strip();
			}
			moduleInfo.setAbsolutePath(absolutePath.replaceAll("(" + File.separator + "(main|test|java))+", ""));
			// 避免模块重复
			int index;
			if ((index = moduleInfoList.indexOf(moduleInfo)) == -1) {
				moduleInfoList.add(moduleInfo);
			} else {
				moduleInfo = moduleInfoList.get(index);
			}
			// 为模块添加子包
			// 去除包路径后，最大公共路径相同的包，将属于同一个模块
			if (absolutePath.contains(File.separator + "main")) {
				moduleInfo.addMainPackageSet(packageInfo);
				moduleInfo.setMainScopePath(absolutePath);
			} else if (absolutePath.contains(File.separator + "test")) {
				moduleInfo.addTestPackageSet(packageInfo);
				moduleInfo.setTestScopePath(absolutePath);
			} else {
				moduleInfo.addOtherPackageSet(packageInfo);
				moduleInfo.setOtherScopePath(absolutePath);
			}
		});
		return moduleInfoList;
	}

	/**
	 * 从项目跟路径开始查找所有.java文件，指定的{@code exclusions}目录将被排除
	 *
	 * @param file 项目根目录
	 * @param javaList 储存所有.java文件
	 */
	private void getAllJavaFiles(File file, List<File> javaList) {
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

	public ProjectInfo getProjectInfo() {
		return projectInfo;
	}

	public void setProjectInfo(ProjectInfo projectInfo) {
		this.projectInfo = projectInfo;
	}

	public static void main(String[] args) {

		ProjectInfo projectInfo = new ProjectInfo();
		projectInfo.setRootPath("/Users/liao/myProjects/IdeaProjects/comp5911m/refactor");
		// projectInfo.setRootPath("/Users/liao/myProjects/IdeaProjects/sonarqube");
		projectInfo.setProjectName("CustomizedName");

		EntityScanner entityScanner = new EntityScanner(projectInfo);
		entityScanner.scan();
	}
}
