package com.t4m.extractor.entity;

import java.util.*;

/**
 * 用于调整模块的依赖关系， Created by Yuxiang Liao on 2020-06-12 15:36.
 */
public class DirHierarchyNode {

	private String name; // 当前文件夹名字
	private String absolutePath;
	private DirHierarchyNode previousNode; // 上一层文件夹名
	private List<DirHierarchyNode> nextNodeList = new LinkedList<>(); // 下一层文件夹名

	private ModuleInfo moduleInfo;

	public DirHierarchyNode(String name) {
		this.name = name;
	}

	public DirHierarchyNode(String name, String absolutePath) {
		this.name = name;
		this.absolutePath = absolutePath;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		DirHierarchyNode dirHierarchyNode = (DirHierarchyNode) o;
		return Objects.equals(absolutePath, dirHierarchyNode.absolutePath);
	}

	@Override
	public int hashCode() {
		return Objects.hash(absolutePath);
	}

	public DirHierarchyNode getPreviousNode() {
		return previousNode;
	}

	public void setPreviousNode(DirHierarchyNode previousNode) {
		this.previousNode = previousNode;
	}

	public List<DirHierarchyNode> getNextNodeList() {
		return nextNodeList;
	}

	public void setNextNodeList(List<DirHierarchyNode> nextNodeList) {
		this.nextNodeList = nextNodeList;
	}

	/**
	 * 避免添加重复元素，参数类需要重写{@code equals()}和{@code hashCode()}方法。 如果对象不存在列表中，则添加并返回该对象；如果对象已存在，则从列表中获取并返回该对象。
	 */
	public DirHierarchyNode safeAddNodeList(DirHierarchyNode dirHierarchyNode) {
		int index;
		if ((index = nextNodeList.indexOf(dirHierarchyNode)) == -1) {
			this.nextNodeList.add(dirHierarchyNode);
			return dirHierarchyNode;
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

	public boolean hasModuleInfo() {
		return moduleInfo != null;
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

}
