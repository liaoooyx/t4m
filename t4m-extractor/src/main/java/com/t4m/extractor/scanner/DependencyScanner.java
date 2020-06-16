package com.t4m.extractor.scanner;

import com.t4m.extractor.entity.DirectoryNode;
import com.t4m.extractor.entity.ModuleInfo;
import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.entity.ProjectInfo;

import java.io.File;
import java.util.Arrays;

/**
 * Created by Yuxiang Liao on 2020-06-16 01:18.
 */
public class DependencyScanner {

	/**
	 * 建立模块依赖关系
	 */
	public static void createModuleDependency(DirectoryNode rootNode, ProjectInfo projectInfo) {

		// 建立模块层级关系
		projectInfo.getModuleList().forEach(moduleInfo -> {
			// 补充module信息
			String moduleSuffixPath = moduleInfo.getAbsolutePath().replace(projectInfo.getAbsolutePath(), "")
			                                    .replaceFirst(File.separator, "").strip();
			String moduleRelativePath = projectInfo.getProjectDirName() + File.separator + moduleSuffixPath;
			String[] temp = moduleRelativePath.split(File.separator);
			String shortName = temp[temp.length - 1];
			moduleInfo.setRelativePath(moduleRelativePath);
			moduleInfo.setShortName(shortName);
			// 根据模块的相对路径，递归生成节点
			if (!"".equals(moduleSuffixPath)) {
				String[] fileNames = moduleRelativePath.split(File.separator);
				String[] excludeRootDir = Arrays.copyOfRange(fileNames, 1, fileNames.length);
				DirectoryNode.initDirectoryNodeLink(excludeRootDir, rootNode, moduleInfo);
			}
		});
		recursiveModuleDependency(rootNode, null);
	}

	/**
	 * 深度优先，递归遍历节点
	 */
	private static void recursiveModuleDependency(DirectoryNode currentNode, ModuleInfo previousModuleInfo) {
		if (currentNode.hasModuleInfo()) {
			if (previousModuleInfo != null) {
				currentNode.getModuleInfo().setPreviousModuleInfo(previousModuleInfo);
				previousModuleInfo.safeAddSubModuleList(currentNode.getModuleInfo());
			}
			if (currentNode.hasNextNode()) {
				currentNode.getNextNodeList().forEach(
						node -> recursiveModuleDependency(node, currentNode.getModuleInfo()));
			}
		} else {
			if (currentNode.hasNextNode()) {
				currentNode.getNextNodeList().forEach(node -> recursiveModuleDependency(node, previousModuleInfo));
			}
		}
	}

	/**
	 * 建立包依赖关系
	 */
	public static void createPackageDependency(ProjectInfo projectInfo) {
		recursivePackageDependency(new File(projectInfo.getAbsolutePath()), null, projectInfo);
	}

	/**
	 * 从项目根路径开始遍历，以深度优先的方式建立包依赖关系
	 */
	public static void recursivePackageDependency(File dir, PackageInfo previousPkg, ProjectInfo projectInfo) {
		if (dir.isDirectory()) {
			//获取当前路径下的包
			PackageInfo currentPkg = projectInfo.getPackageInfoByAbsolutePath(dir.getAbsolutePath());
			//判断当前路径是否有对应的包
			if (currentPkg != null) {
				if (previousPkg != null) {
					previousPkg.safeAddSubPackageList(currentPkg);
					currentPkg.setPreviousPackage(previousPkg);
				}
				// 更新父节点，搜索子路径下的其他文件夹
				Arrays.stream(dir.listFiles()).forEach(f -> recursivePackageDependency(f, currentPkg, projectInfo));
			} else {
				// 保留父节点，继续搜索子路径下的其他文件夹
				Arrays.stream(dir.listFiles()).forEach(f -> recursivePackageDependency(f, previousPkg, projectInfo));
			}
		}
		//TODO 处理：如果root路径下没有文件夹
	}

	public static void main(String[] args) {
		String rootPath = "/Users/liao/myProjects/IdeaProjects/sonarqube";
		ProjectInfo projectInfo = new ProjectInfo(rootPath);
		EntityScanner.scan(projectInfo);

		DirectoryNode rootNode = new DirectoryNode(new File(projectInfo.getAbsolutePath()).getName(),
		                                           projectInfo.getAbsolutePath());
		createModuleDependency(rootNode, projectInfo);

		createPackageDependency(projectInfo);

		System.out.println(rootNode);
	}

}
