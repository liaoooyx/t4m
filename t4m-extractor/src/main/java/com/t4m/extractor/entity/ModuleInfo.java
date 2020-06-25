package com.t4m.extractor.entity;

import java.io.Serializable;
import java.util.*;

/**
 * Created by Yuxiang Liao on 2020-06-11 09:29.
 */
public class ModuleInfo implements Serializable {

	private static final long serialVersionUID = 4453849096224682890L;
	//shortName和fullyQualifiedName都
	private String shortName;
	private String relativePath; // 从项目路径开始的相对路径
	private String absolutePath;

	private List<ModuleInfo> subModuleList = new ArrayList<>();
	private ModuleInfo previousModuleInfo;

	private List<PackageInfo> mainPackageList = new ArrayList<>();
	private List<PackageInfo> testPackageList = new ArrayList<>();
	private List<PackageInfo> otherPackageList = new ArrayList<>();

	private String mainScopePath;
	private String testScopePath;
	private String otherScopePath;

	public ModuleInfo(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ModuleInfo that = (ModuleInfo) o;
		return Objects.equals(absolutePath, that.absolutePath);
	}

	@Override
	public int hashCode() {
		return Objects.hash(absolutePath);
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	public List<ModuleInfo> getSubModuleList() {
		return subModuleList;
	}

	public void setSubModuleList(List<ModuleInfo> subModuleList) {
		this.subModuleList = subModuleList;
	}

	/**
	 * 避免添加重复元素，参数类需要重写{@code equals()}和{@code hashCode()}方法。 如果对象不存在列表中，则添加并返回该对象；如果对象已存在，则从列表中获取并返回该对象。
	 */
	public ModuleInfo safeAddSubModuleList(ModuleInfo moduleInfo) {
		int index;
		if ((index = subModuleList.indexOf(moduleInfo)) == -1) {
			this.subModuleList.add(moduleInfo);
			return moduleInfo;
		} else {
			return this.subModuleList.get(index);
		}
	}

	public ModuleInfo getPreviousModuleInfo() {
		return previousModuleInfo;
	}

	public void setPreviousModuleInfo(ModuleInfo previousModuleInfo) {
		this.previousModuleInfo = previousModuleInfo;
	}

	public List<PackageInfo> getMainPackageList() {
		return mainPackageList;
	}

	public void setMainPackageList(List<PackageInfo> mainPackageList) {
		this.mainPackageList = mainPackageList;
	}

	/**
	 * 避免添加重复元素，参数类需要重写{@code equals()}和{@code hashCode()}方法。 如果对象不存在列表中，则添加并返回该对象；如果对象已存在，则从列表中获取并返回该对象。
	 */
	public PackageInfo safeAddMainPackageList(PackageInfo packageInfo) {
		int index;
		if ((index = mainPackageList.indexOf(packageInfo)) == -1) {
			this.mainPackageList.add(packageInfo);
			return packageInfo;
		} else {
			return this.mainPackageList.get(index);
		}
	}

	public boolean hasMainPackageList() {
		return mainPackageList.size() > 0;
	}

	public List<PackageInfo> getTestPackageList() {
		return testPackageList;
	}

	public void setTestPackageList(List<PackageInfo> testPackageList) {
		this.testPackageList = testPackageList;
	}

	/**
	 * 避免添加重复元素，参数类需要重写{@code equals()}和{@code hashCode()}方法。 如果对象不存在列表中，则添加并返回该对象；如果对象已存在，则从列表中获取并返回该对象。
	 */
	public PackageInfo safeAddTestPackageList(PackageInfo packageInfo) {
		int index;
		if ((index = testPackageList.indexOf(packageInfo)) == -1) {
			this.testPackageList.add(packageInfo);
			return packageInfo;
		} else {
			return this.testPackageList.get(index);
		}
	}

	public boolean hasTestPackageList() {
		return testPackageList.size() > 0;
	}

	public List<PackageInfo> getOtherPackageList() {
		return otherPackageList;
	}

	public void setOtherPackageList(List<PackageInfo> otherPackageList) {
		this.otherPackageList = otherPackageList;
	}

	/**
	 * 避免添加重复元素，参数类需要重写{@code equals()}和{@code hashCode()}方法。 如果对象不存在列表中，则添加并返回该对象；如果对象已存在，则从列表中获取并返回该对象。
	 */
	public PackageInfo safeAddOtherPackageList(PackageInfo packageInfo) {
		int index;
		if ((index = otherPackageList.indexOf(packageInfo)) == -1) {
			this.otherPackageList.add(packageInfo);
			return packageInfo;
		} else {
			return this.otherPackageList.get(index);
		}
	}

	public boolean hasOtherPackageList() {
		return otherPackageList.size() > 0;
	}

	public String getMainScopePath() {
		return mainScopePath;
	}

	public void setMainScopePath(String mainScopePath) {
		this.mainScopePath = mainScopePath;
	}

	public String getTestScopePath() {
		return testScopePath;
	}

	public void setTestScopePath(String testScopePath) {
		this.testScopePath = testScopePath;
	}

	public String getOtherScopePath() {
		return otherScopePath;
	}

	public void setOtherScopePath(String otherScopePath) {
		this.otherScopePath = otherScopePath;
	}
}
