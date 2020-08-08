package com.t4m.extractor.entity;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * 用于调整模块的依赖关系， Created by Yuxiang Liao on 2020-06-12 15:36.
 */
public class DirHierarchyNode implements Serializable {

	private static final long serialVersionUID = -3065831265679039732L;

	private String name; // name of current dir
	private String absolutePath;
	private DirHierarchyNode previousNode; // previous dir
	private List<DirHierarchyNode> nextNodeList = new LinkedList<>(); // a list of next dirs

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
