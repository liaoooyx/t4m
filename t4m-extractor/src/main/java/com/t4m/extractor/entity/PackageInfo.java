package com.t4m.extractor.entity;

import java.util.*;

/**
 * Created by Yuxiang Liao on 2020-06-09 22:55.
 */
public class PackageInfo {

	public static String EMPTY_IDENTIFIER = "(null)";

	private PackageInfo parentPackage;
	private Set<PackageInfo> childrenPackageSet = new HashSet<>();
	private Set<ClassInfo> classSet = new HashSet<>();
	; // direct classes

	private String fullPackageName; // = (null), if doesn't have package
	private String absolutePath;

	private String[] packagePathChain;


	private Map<PackageInfo, Integer> dependsOn;
	private Map<PackageInfo, Integer> dependedBy;

	public PackageInfo() {
	}

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

	public PackageInfo getParentPackage() {
		return parentPackage;
	}

	public void setParentPackage(PackageInfo parentPackage) {
		this.parentPackage = parentPackage;
	}

	public Set<PackageInfo> getChildrenPackageSet() {
		return childrenPackageSet;
	}

	public void setChildrenPackageSet(Set<PackageInfo> childrenPackageSet) {
		this.childrenPackageSet = childrenPackageSet;
	}

	public void addChildrenPackage(PackageInfo packageInfo) {
		this.childrenPackageSet.add(packageInfo);
	}

	public Set<ClassInfo> getClassSet() {
		return classSet;
	}

	public void setClassSet(Set<ClassInfo> classSet) {
		this.classSet = classSet;
	}

	public void addClassSet(ClassInfo classInfo) {
		this.classSet.add(classInfo);
	}

	public String getFullPackageName() {
		return fullPackageName;
	}

	public void setFullPackageName(String fullPackageName) {
		this.fullPackageName = fullPackageName;
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	public String[] getPackagePathChain() {
		return packagePathChain;
	}

	public void setPackagePathChain(String[] packagePathChain) {
		this.packagePathChain = packagePathChain;
	}

	public Map<PackageInfo, Integer> getDependsOn() {
		return dependsOn;
	}

	public void setDependsOn(Map<PackageInfo, Integer> dependsOn) {
		this.dependsOn = dependsOn;
	}

	public Map<PackageInfo, Integer> getDependedBy() {
		return dependedBy;
	}

	public void setDependedBy(Map<PackageInfo, Integer> dependedBy) {
		this.dependedBy = dependedBy;
	}

	@Override
	public String toString() {
		return "PackageInfo{" + "parentPackage=" + parentPackage + ", childrenPackage=" + childrenPackageSet +
				", classSet=" + classSet + ", fullPackageName='" + fullPackageName + '\'' + ", absolutePath='" +
				absolutePath + '\'' + ", packagePathChain=" + Arrays.toString(packagePathChain) + ", dependsOn=" +
				dependsOn + ", dependedBy=" + dependedBy + '}';
	}
}
