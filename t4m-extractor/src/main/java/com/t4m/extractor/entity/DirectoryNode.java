package com.t4m.extractor.entity;

import java.io.File;
import java.util.*;

/**
 * 用于调整模块的依赖关系， Created by Yuxiang Liao on 2020-06-12 15:36.
 */
public class DirectoryNode {

	private String name; // 当前文件夹名字
	private String absolutePath;
	private DirectoryNode previousNode; // 上一层文件夹名
	private List<DirectoryNode> nextNodeList = new LinkedList<>(); // 下一层文件夹名

	private ModuleInfo moduleInfo;

	public DirectoryNode(String name) {
		this.name = name;
	}

	public DirectoryNode(String name, String absolutePath) {
		this.name = name;
		this.absolutePath = absolutePath;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		DirectoryNode directoryNode = (DirectoryNode) o;
		return Objects.equals(absolutePath, directoryNode.absolutePath);
	}

	@Override
	public int hashCode() {
		return Objects.hash(absolutePath);
	}

	public DirectoryNode getPreviousNode() {
		return previousNode;
	}

	public void setPreviousNode(DirectoryNode previousNode) {
		this.previousNode = previousNode;
	}

	public List<DirectoryNode> getNextNodeList() {
		return nextNodeList;
	}

	public void setNextNodeList(List<DirectoryNode> nextNodeList) {
		this.nextNodeList = nextNodeList;
	}

	/**
	 * 避免添加重复元素，参数类需要重写{@code equals()}和{@code hashCode()}方法。 如果对象不存在列表中，则添加并返回该对象；如果对象已存在，则从列表中获取并返回该对象。
	 */
	public DirectoryNode safeAddNodeList(DirectoryNode directoryNode) {
		int index;
		if ((index = nextNodeList.indexOf(directoryNode)) == -1) {
			this.nextNodeList.add(directoryNode);
			return directoryNode;
		} else {
			return this.nextNodeList.get(index);
		}
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

	public boolean hasNextNode() {
		return nextNodeList.size() > 0;
	}

	public boolean hasModuleInfo() {
		return moduleInfo != null;
	}

	/**
	 * 根据路径，递归创建节点链表
	 */
	public static void initDirectoryNodeLink(String[] names, DirectoryNode previousNode, ModuleInfo moduleInfo) {
		if (names.length > 0) {
			String name = names[0];
			DirectoryNode currentNode = previousNode.safeAddNodeList(
					new DirectoryNode(name, previousNode.getAbsolutePath() + File.separator + name));
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

}
