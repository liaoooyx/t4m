package com.t4m.extractor.entity;

import java.util.*;

/**
 * 根据{@code absolutePath}来判断对象是否一致
 */
public class PackageInfo {

	public static String EMPTY_IDENTIFIER = "(null)";

	private String fullyQualifiedName; // = (null), if doesn't have package
	private String absolutePath;

	private ModuleInfo moduleInfo;

	private PackageInfo previousPackage;
	private List<PackageInfo> subPackageList = new ArrayList<>();
	private List<ClassInfo> classList = new ArrayList<>();

	// private Map<PackageInfo, Integer> dependsOn;
	// private Map<PackageInfo, Integer> dependedBy;

	public PackageInfo(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		PackageInfo that = (PackageInfo) o;
		return Objects.equals(absolutePath, that.absolutePath);
	}

	@Override
	public int hashCode() {
		return Objects.hash(absolutePath);
	}

	public String getFullyQualifiedName() {
		return fullyQualifiedName;
	}

	public void setFullyQualifiedName(String fullyQualifiedName) {
		this.fullyQualifiedName = fullyQualifiedName;
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

	public PackageInfo getPreviousPackage() {
		return previousPackage;
	}

	public void setPreviousPackage(PackageInfo previousPackage) {
		this.previousPackage = previousPackage;
	}

	public List<PackageInfo> getSubPackageList() {
		return subPackageList;
	}

	public void setSubPackageList(List<PackageInfo> subPackageList) {
		this.subPackageList = subPackageList;
	}

	/**
	 * 避免添加重复元素，参数类需要重写{@code equals()}和{@code hashCode()}方法。 如果对象不存在列表中，则添加并返回该对象；如果对象已存在，则从列表中获取并返回该对象。
	 */
	public PackageInfo safeAddSubPackageList(PackageInfo packageInfo) {
		int index;
		if ((index = subPackageList.indexOf(packageInfo)) == -1) {
			this.subPackageList.add(packageInfo);
			return packageInfo;
		} else {
			return this.subPackageList.get(index);
		}
	}

	public List<ClassInfo> getClassList() {
		return classList;
	}

	public void setClassList(List<ClassInfo> classList) {
		this.classList = classList;
	}

	/**
	 * 避免添加重复元素，参数类需要重写{@code equals()}和{@code hashCode()}方法。 如果对象不存在列表中，则添加并返回该对象；如果对象已存在，则从列表中获取并返回该对象。
	 */
	public ClassInfo safeAddClassList(ClassInfo classInfo) {
		int index;
		if ((index = classList.indexOf(classInfo)) == -1) {
			this.classList.add(classInfo);
			return classInfo;
		} else {
			return this.classList.get(index);
		}
	}

}
