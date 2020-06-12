package com.t4m.extractor.entity;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Created by Yuxiang Liao on 2020-06-09 22:55.
 */
public class PackageInfo {

	private PackageInfo parentPackage;
	private Set<PackageInfo> childrenPackage;
	private Set<ClassInfo> classSet; // direct classes

	private String fullPackageName;
	private String absolutePath;

	private String[] packagePathChain;


	private Map<PackageInfo, Integer> dependsOn;
	private Map<PackageInfo, Integer> dependedBy;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		PackageInfo that = (PackageInfo) o;
		return fullPackageName.equals(that.fullPackageName) && absolutePath.equals(that.absolutePath);
	}

	@Override
	public int hashCode() {
		return Objects.hash(fullPackageName, absolutePath);
	}

	public PackageInfo getParentPackage() {
		return parentPackage;
	}

	public void setParentPackage(PackageInfo parentPackage) {
		this.parentPackage = parentPackage;
	}

	public Set<PackageInfo> getChildrenPackage() {
		return childrenPackage;
	}

	public void setChildrenPackage(Set<PackageInfo> childrenPackage) {
		this.childrenPackage = childrenPackage;
	}

	public Set<ClassInfo> getClassSet() {
		return classSet;
	}

	public void setClassSet(Set<ClassInfo> classSet) {
		this.classSet = classSet;
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
}
