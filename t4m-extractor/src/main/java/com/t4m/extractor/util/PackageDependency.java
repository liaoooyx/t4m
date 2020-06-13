package com.t4m.extractor.util;

import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.scanner.EntityScanner;

import java.io.File;
import java.util.Arrays;


/**
 * Created by Yuxiang Liao on 2020-06-13 14:17.
 */
public class PackageDependency {

	ProjectInfo projectInfo;

	public PackageDependency(ProjectInfo projectInfo) {
		this.projectInfo = projectInfo;
	}

	public void createPkgDependency(File dir, PackageInfo previousPkg) {
		if (dir.isDirectory()) {
			int index = projectInfo.getPackageList().indexOf(new PackageInfo(dir.getAbsolutePath()));
			//判断当前路径是否有对应的包
			if (index != -1) {
				PackageInfo currentPkg = projectInfo.getPackageList().get(index);
				if (previousPkg != null) {
					previousPkg.addChildrenPackage(currentPkg);
					currentPkg.setParentPackage(previousPkg);
				}
				// 更新父节点，搜索子路径下的其他文件夹
				Arrays.stream(dir.listFiles()).forEach(f -> {
					createPkgDependency(f, currentPkg);
				});
			} else {
				// 没有。使用上层的父节点，继续搜索子路径下的其他文件夹
				Arrays.stream(dir.listFiles()).forEach(f -> {
					createPkgDependency(f, previousPkg);
				});
			}
		}
		//TODO 处理：如果root路径下没有文件夹
	}


	public static void main(String[] args) {
		String rootPath = "/Users/liao/myProjects/IdeaProjects/sonarqube";

		ProjectInfo projectInfo = new ProjectInfo();
		projectInfo.setRootPath(rootPath);
		projectInfo.setProjectName("CustomizedName");

		EntityScanner entityScanner = new EntityScanner(projectInfo);
		entityScanner.scan();
		projectInfo = entityScanner.getProjectInfo();

		PackageDependency packageDependency = new PackageDependency(projectInfo);
		packageDependency.createPkgDependency(new File(rootPath), null);

		PathNode rootNode = new PathNode(new File(projectInfo.getRootPath()).getName(), projectInfo.getRootPath(),
		                                 true);

		// 建立模块层级关系
		projectInfo.getModuleList().forEach(moduleInfo -> {
			String suffixPath = moduleInfo.getModulePath().replace(rootPath, "").replaceFirst(File.separator, "")
			                              .strip();
			// 递归生成路径节点
			if (!"".equals(suffixPath)) {
				String[] fileNames = suffixPath.split(File.separator);
				PathNode.createPathNode(fileNames, rootNode, moduleInfo);
			}
		});

		System.out.println(rootNode);
	}
}
