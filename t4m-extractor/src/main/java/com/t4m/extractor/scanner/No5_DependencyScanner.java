package com.t4m.extractor.scanner;

import com.t4m.extractor.T4MExtractor;
import com.t4m.extractor.entity.DirHierarchyNode;
import com.t4m.extractor.entity.ModuleInfo;
import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.entity.ProjectInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Created by Yuxiang Liao on 2020-06-16 01:18.
 */
public class No5_DependencyScanner {

	private ProjectInfo projectInfo;

	public No5_DependencyScanner(ProjectInfo projectInfo) {
		this.projectInfo = projectInfo;
	}

	public void scan() {
		DirHierarchyNode rootNode = new DirHierarchyNode(new File(projectInfo.getAbsolutePath()).getName(),
		                                                 projectInfo.getAbsolutePath());
		createModuleDependency(rootNode, projectInfo);
		createPackageDependency(projectInfo);
		projectInfo.setRootDirHierarchyNode(rootNode);
	}

	/**
	 * 建立模块依赖关系
	 */
	public static void createModuleDependency(DirHierarchyNode rootNode, ProjectInfo projectInfo) {

		// 建立模块层级关系
		projectInfo.getModuleList().forEach(moduleInfo -> {

			// 补充module信息
			String moduleSuffixPath = moduleInfo.getAbsolutePath().replace(projectInfo.getAbsolutePath(), "")
			                                    .replaceFirst(File.separator, "").strip();
			String moduleRelativePath = projectInfo.getProjectDirName() + File.separator + moduleSuffixPath;
			String[] temp = moduleRelativePath.split(File.separator);
			String moduleShortName = temp[temp.length - 1];
			if (Objects.equals(rootNode.getName(),moduleShortName)){
				// 根模块 (可以不存在根模块)
				rootNode.setModuleInfo(moduleInfo);
				moduleInfo.setRelativePath(moduleShortName);
			}else {
				//子模块
				moduleInfo.setRelativePath(moduleRelativePath);
			}
			moduleInfo.setShortName(moduleShortName);
			// 根据模块的相对路径，递归生成节点
			if (!"".equals(moduleSuffixPath)) {
				String[] fileNames = moduleRelativePath.split(File.separator);
				//排除根模块
				String[] excludeRootDir = Arrays.copyOfRange(fileNames, 1, fileNames.length);
				initDirectoryNodeLink(excludeRootDir, rootNode, moduleInfo);
			}
		});
		recursiveModuleDependency(rootNode, null);
	}

	/**
	 * 根据路径，递归创建节点链表
	 */
	public static void initDirectoryNodeLink(String[] names, DirHierarchyNode previousNode, ModuleInfo moduleInfo) {
		if (names.length > 0) {
			String name = names[0];
			DirHierarchyNode currentNode = previousNode.safeAddNodeList(
					new DirHierarchyNode(name, previousNode.getAbsolutePath() + File.separator + name));
			// 当currentNode为新节点时，它的previousNode为空，需要赋值
			if (currentNode.getPreviousNode() == null) {
				currentNode.setPreviousNode(previousNode);
			}
			String[] nextNames = Arrays.copyOfRange(names, 1, names.length);
			initDirectoryNodeLink(nextNames, currentNode, moduleInfo);
		} else {
			// 已经遍历到底部，则添加模块信息
			previousNode.setModuleInfo(moduleInfo);
		}
	}

	/**
	 * 深度优先，递归遍历节点
	 */
	private static void recursiveModuleDependency(DirHierarchyNode currentNode, ModuleInfo previousModuleInfo) {
		if (currentNode.hasModuleInfo()) {
			// 非根节点
			if (previousModuleInfo != null) {
				currentNode.getModuleInfo().setPreviousModuleInfo(previousModuleInfo);
				previousModuleInfo.safeAddSubModuleList(currentNode.getModuleInfo());
			}
			// 根节点
			if (currentNode.hasNextNode()) {
				currentNode.getNextNodeList().forEach(
						node -> recursiveModuleDependency(node, currentNode.getModuleInfo()));
			}
		} else {
			// 当前节点无模块
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
		List<File> rawJavaFileList = new ArrayList<>();
		T4MExtractor t4MExtractor = new T4MExtractor(projectInfo);
		t4MExtractor.scanDependency();

	}

}
