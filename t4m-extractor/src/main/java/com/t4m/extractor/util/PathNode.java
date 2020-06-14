package com.t4m.extractor.util;

import com.t4m.extractor.entity.ModuleInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.scanner.EntityScanner;

import java.io.File;
import java.util.*;

/**
 * 用于调整模块的依赖关系， Created by Yuxiang Liao on 2020-06-12 15:36.
 */
public class PathNode {

	private String name; // 当前文件夹名字
	private String absolutePath;
	private PathNode previousNode; // 上一层文件夹名
	private List<PathNode> nextNodeList = new LinkedList<>(); // 下一层文件夹名

	private boolean isRootNode = false;

	private ModuleInfo moduleInfo;

	public PathNode() {

	}

	public PathNode(String name) {
		this.name = name;
	}

	public PathNode(String name, String absolutePath) {
		this.name = name;
		this.absolutePath = absolutePath;
	}

	public PathNode(String name, String absolutePath, boolean isRootNode) {
		this.name = name;
		this.isRootNode = isRootNode;
		this.absolutePath = absolutePath;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		PathNode pathNode = (PathNode) o;
		return Objects.equals(name, pathNode.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	public PathNode getPreviousNode() {
		return previousNode;
	}

	public void setPreviousNode(PathNode previousNode) {
		this.previousNode = previousNode;
	}

	public List<PathNode> getNextNodeList() {
		return nextNodeList;
	}

	public void setNextNodeList(List<PathNode> nextNodeList) {
		this.nextNodeList = nextNodeList;
	}

	public boolean isRootNode() {
		return isRootNode;
	}

	public void setRootNode(boolean rootNode) {
		isRootNode = rootNode;
	}

	public void addNextNodeList(PathNode nextNode) {
		nextNodeList.add(nextNode);
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	public ModuleInfo getModuleInfo() {
		return moduleInfo;
	}

	public void setModuleInfo(ModuleInfo moduleInfo) {
		this.moduleInfo = moduleInfo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static void createPathNode(String[] names, PathNode previousNode, ModuleInfo moduleInfo) {
		if (names.length > 0) {
			String name = names[0];
			PathNode currentNode = new PathNode(name);
			// 判断节点是否已经存在，如果不存在则创建节点
			int index;
			if ((index = previousNode.getNextNodeList().indexOf(currentNode)) != -1) {
				currentNode = previousNode.getNextNodeList().get(index);
			} else if (previousNode.equals(currentNode)) {
				currentNode = previousNode;
			} else {
				currentNode.setPreviousNode(previousNode);
				currentNode.setAbsolutePath(previousNode.getAbsolutePath() + File.separator + name);
				previousNode.addNextNodeList(currentNode);
			}
			String[] nextNames = Arrays.copyOfRange(names, 1, names.length);
			createPathNode(nextNames, currentNode, moduleInfo);
		} else {
			// 已经遍历到底部，则添加模块信息
			previousNode.setModuleInfo(moduleInfo);
		}
	}

	public static PathNode getLastNode(String[] names, PathNode previousNode, PathNode targetNode) {
		if (names.length > 0) {
			String name = names[0];
			PathNode currentNode = new PathNode(name);
			// 查找路径链的最后一个节点
			int index;
			if ((index = previousNode.getNextNodeList().indexOf(currentNode)) != -1) {
				currentNode = previousNode.getNextNodeList().get(index);
			}
			String[] nextNames = Arrays.copyOfRange(names, 1, names.length);
			targetNode = getLastNode(nextNames, currentNode, targetNode);
		} else {
			targetNode = previousNode;
		}
		return targetNode;
	}

	public boolean hasNextNode() {
		return nextNodeList.size() > 0;
	}

	public boolean hasModuleInfo() {
		return moduleInfo != null;
	}

	public static void createModuleDependency(PathNode currentNode, ModuleInfo previousModuleInfo) {
		if (currentNode.hasModuleInfo()) {
			if (previousModuleInfo != null) {
				currentNode.getModuleInfo().setPreviousModuleInfo(previousModuleInfo);
				previousModuleInfo.addSubModuleSet(currentNode.getModuleInfo());
			}
			if (currentNode.hasNextNode()) {
				currentNode.getNextNodeList().forEach(node -> {
					createModuleDependency(node, currentNode.getModuleInfo());
				});
			}
		} else {
			if (currentNode.hasNextNode()) {
				currentNode.getNextNodeList().forEach(node -> {
					createModuleDependency(node, previousModuleInfo);
				});
			}
		}
	}

	public static void main(String[] args) {

		// String rootPath = "/Users/liao/myProjects/IdeaProjects/sonarqube";
		String rootPath = "/Users/liao/myProjects/IdeaProjects/comp5911m/refactor";

		ProjectInfo projectInfo = new ProjectInfo();
		projectInfo.setRootPath(rootPath);
		projectInfo.setProjectName("CustomizedName");

		EntityScanner entityScanner = new EntityScanner(projectInfo);
		entityScanner.scan();
		projectInfo = entityScanner.getProjectInfo();

		String projectDirName = new File(projectInfo.getRootPath()).getName();

		PathNode rootNode = new PathNode(new File(projectInfo.getRootPath()).getName(), projectInfo.getRootPath(),
		                                 true);

		// 建立模块层级关系
		projectInfo.getModuleList().forEach(moduleInfo -> {
			// 补充module信息
			String suffixPath = moduleInfo.getAbsolutePath().replace(rootPath, "").replaceFirst(File.separator, "")
			                              .strip();
			String shortPath = projectDirName + File.separator + suffixPath;
			moduleInfo.setPathName(shortPath);
			moduleInfo.setShortName(
					shortPath.replaceAll(File.separator, ".").replaceAll("(\\.(src|main|test|java))+", ""));

			// 递归生成路径节点
			if (!"".equals(moduleInfo.getShortName())) {
				String[] fileNames = moduleInfo.getShortName().split("\\.");
				createPathNode(fileNames, rootNode, moduleInfo);
			}
		});

		createModuleDependency(rootNode, null);

		System.out.println(rootNode);

	}

}