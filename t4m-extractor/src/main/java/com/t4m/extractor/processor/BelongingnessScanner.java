package com.t4m.extractor.processor;

import com.t4m.extractor.ProcessChain;
import com.t4m.extractor.ProcessNode;
import com.t4m.extractor.entity.DirHierarchyNode;
import com.t4m.extractor.entity.ModuleInfo;
import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.util.EntityUtil;
import com.t4m.extractor.util.RegularExprUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

/**
 * Created by Yuxiang Liao on 2020-06-16 01:18.
 */
public class BelongingnessScanner implements ProcessNode {

	private static final Logger LOGGER = LoggerFactory.getLogger(BelongingnessScanner.class);

	private ProjectInfo projectInfo;

	@Override
	public void scan(ProjectInfo projectInfo, ProcessChain processChain) {
		LOGGER.info("Resolving the dependencies between modules and packages.");
		this.projectInfo = projectInfo;
		DirHierarchyNode rootNode = new DirHierarchyNode(new File(projectInfo.getAbsolutePath()).getName(),
		                                                 projectInfo.getAbsolutePath());
		createModuleBelongingness(rootNode);
		createPackageBelongingness();
		projectInfo.setRootDirHierarchyNode(rootNode);
		processChain.scan(projectInfo);
	}

	private void createModuleBelongingness(DirHierarchyNode rootNode) {
		projectInfo.getModuleList().forEach(moduleInfo -> {
			String moduleSuffixPath = moduleInfo.getAbsolutePath().replace(projectInfo.getAbsolutePath(), "")
			                                    .replaceFirst(RegularExprUtil.compatibleWithWindows("/"), "").strip();
			String moduleRelativePath = projectInfo.getProjectDirName() + File.separator + moduleSuffixPath;
			String[] temp = moduleRelativePath.split(RegularExprUtil.compatibleWithWindows("/"));
			String moduleShortName = temp[temp.length - 1];
			if (Objects.equals(rootNode.getName(), moduleShortName)) {
				// 根模块 (可以不存在根模块)
				rootNode.setModuleInfo(moduleInfo);
				moduleInfo.setRelativePath(moduleShortName);
			} else {
				//子模块
				moduleInfo.setRelativePath(moduleRelativePath);
			}
			moduleInfo.setShortName(moduleShortName);
			// 根据模块的相对路径，递归生成节点
			if (!"".equals(moduleSuffixPath)) {
				String[] fileNames = moduleRelativePath.split(RegularExprUtil.compatibleWithWindows("/"));
				//排除根模块
				String[] excludeRootDir = Arrays.copyOfRange(fileNames, 1, fileNames.length);
				initDirectoryNodeLink(excludeRootDir, rootNode, moduleInfo);
			}
		});
		recursiveModuleBelongingness(rootNode, null);
	}

	/**
	 * According to the module path, recursively create a linked list of nodes
	 */
	private void initDirectoryNodeLink(String[] names, DirHierarchyNode previousNode, ModuleInfo moduleInfo) {
		if (names.length > 0) {
			String name = names[0];
			DirHierarchyNode currentNode = EntityUtil.safeAddEntityToList(
					new DirHierarchyNode(name, previousNode.getAbsolutePath() + File.separator + name),
					previousNode.getNextNodeList());
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
	 * Depth-first, recursively traverse nodes
	 *
	 * @param currentNode Node pointer
	 * @param previousModuleInfo Previous module
	 */
	private void recursiveModuleBelongingness(DirHierarchyNode currentNode, ModuleInfo previousModuleInfo) {
		if (currentNode.hasModuleInfo()) {
			if (previousModuleInfo != null) { // Not the root node
				currentNode.getModuleInfo().setPreviousModuleInfo(previousModuleInfo);
				EntityUtil.safeAddEntityToList(currentNode.getModuleInfo(), previousModuleInfo.getSubModuleList());
			}
			if (currentNode.hasNextNode()) { // The root node
				currentNode.getNextNodeList().forEach(
						node -> recursiveModuleBelongingness(node, currentNode.getModuleInfo()));
			}
		} else {
			if (currentNode.hasNextNode()) {
				currentNode.getNextNodeList().forEach(node -> recursiveModuleBelongingness(node, previousModuleInfo));
			}
		}
	}

	private void createPackageBelongingness() {
		recursivePackageBelongingness(new File(projectInfo.getAbsolutePath()), null, projectInfo);
	}

	/**
	 * Start traversing from the project root path and establish package belonging in a depth-first manner
	 *
	 * @param dir Current path
	 * @param previousPkg The pointer
	 * @param projectInfo The target project
	 */

	private void recursivePackageBelongingness(File dir, PackageInfo previousPkg, ProjectInfo projectInfo) {
		if (dir.isDirectory()) {
			// Get the package under the current path
			PackageInfo currentPkg = EntityUtil.getPackageInfoByAbsolutePath(projectInfo.getPackageList(), dir.getAbsolutePath());
			// Determine whether the current path has a corresponding package
			if (currentPkg != null) {
				// Set package belongingness
				if (previousPkg != null) {
					EntityUtil.safeAddEntityToList(currentPkg, previousPkg.getSubPackageList());
					currentPkg.setPreviousPackage(previousPkg);
				}
				// Update the pointer, search for other folders under the subpath
				Arrays.stream(dir.listFiles()).forEach(f -> recursivePackageBelongingness(f, currentPkg, projectInfo));
			} else {
				// Keep the pointer, and continue to search for other folders under the subpath
				Arrays.stream(dir.listFiles()).forEach(f -> recursivePackageBelongingness(f, previousPkg, projectInfo));
			}
		}
	}

}
