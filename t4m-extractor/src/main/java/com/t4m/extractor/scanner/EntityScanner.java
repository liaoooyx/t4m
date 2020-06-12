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

	public void scan() {
		File root = new File(projectInfo.getRootPath());
		List<File> javaFileList = new ArrayList<>();
		getAllJavaFiles(root, javaFileList);
		// 初始化所有Class
		List<ClassInfo> classInfoList = extractClassFromFileList(javaFileList);
		// 初始化所有Package
		List<PackageInfo> packageInfoList = extractPackageFromClassList(classInfoList);
		packageInfoList.forEach(i -> System.out
				.printf("(%d)%s   %s %n", i.getPackagePathChain().length, i.getFullPackageName(), i.getAbsolutePath()));

	}

	public void getAllModules(File file) {
		if ("src".equals(file.getName())) {
			ModuleInfo moduleInfo = new ModuleInfo();
			moduleInfo.setModuleName(file.getParentFile().getName());
			moduleInfo.setModulePath(file.getParentFile().getPath());
			projectInfo.addModuleList(moduleInfo);
		}
		if (file.isDirectory()) {
			File[] fileArray = file.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return !"build".equals(name);
				}
			});
			Arrays.stream(fileArray).forEach(this::getAllModules);
		}
	}

	public void getAllPackages() {

	}

	/**
	 * 对于列表中的每个File对象，从中读取信息，并转化为{@code ClassInfo}对象. <br> 包括{@code absolutePath}, {@code packagePath}
	 */
	private List<ClassInfo> extractClassFromFileList(List<File> javaFileList) {
		List<ClassInfo> classInfoList = new ArrayList<>();
		javaFileList.forEach(javaFile -> {
			ClassInfo classInfo = new ClassInfo();
			classInfo.setAbsolutePath(javaFile.getAbsolutePath());
			try {
				String line;
				BufferedReader reader = new BufferedReader(new FileReader(javaFile));
				while ((line = reader.readLine()) != null) {
					// 去读java文件的包路径
					if (line.startsWith("package")) {
						classInfo.setPackageFullName(line.replaceFirst("package", "").strip());
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
			packageInfo.setFullPackageName(pkgFullName);
			packageInfo.setPackagePathChain(pkgFullName.split("\\."));
			packageInfo.setAbsolutePath(classInfo.getAbsolutePath().replaceFirst("/{1}?[^/]*?\\.java", ""));
			if (!packageInfoList.contains(packageInfo)) {
				packageInfoList.add(packageInfo);
			}
		});
		return packageInfoList;
	}

	/**
	 * 从项目跟路径开始查找所有.java文件，指定的{@code exclusions}目录将被排除
	 *
	 * @param file 项目根目录
	 * @param javaList 储存所有.java文件
	 */
	public void getAllJavaFiles(File file, List<File> javaList) {
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


	/**
	 * Assumming the current directory name is "src", the module name should be two directory layers before.<br> For
	 * root module, check the name of two directory layers before "src" and the last name of root project path.
	 *
	 * @return {@code true} if current file belongs to root module.
	 */
	private boolean checkRootModule(File currentFile) {
		File projectFile = new File(projectInfo.getRootPath());
		return currentFile.getParentFile().equals(projectFile.getName());
	}


	public static void main(String[] args) {

		ProjectInfo projectInfo = new ProjectInfo();
		// projectInfo.setRootPath("/Users/liao/myProjects/IdeaProjects/comp5911m/refactor");
		projectInfo.setRootPath("/Users/liao/myProjects/IdeaProjects/sonarqube");
		projectInfo.setProjectName("CustomizedName");

		EntityScanner entityScanner = new EntityScanner(projectInfo);
		entityScanner.scan();
	}
}
