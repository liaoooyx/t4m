package com.t4m.extractor.entity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Created by Yuxiang Liao on 2020-06-11 09:29.
 */
public class ModuleInfo {

	private String shortName;
	private String pathName;
	private String absolutePath;

	private Set<ModuleInfo> subModuleSet = new HashSet<>();
	private ModuleInfo previousModuleInfo;

	private Set<PackageInfo> mainPackageSet = new HashSet<>();
	private Set<PackageInfo> testPackageSet = new HashSet<>();
	private Set<PackageInfo> otherPackageSet = new HashSet<>();

	private String mainScopePath;
	private String testScopePath;
	private String otherScopePath;

	private boolean isRootModule = false;

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

	public ModuleInfo getPreviousModuleInfo() {
		return previousModuleInfo;
	}

	public void setPreviousModuleInfo(ModuleInfo previousModuleInfo) {
		this.previousModuleInfo = previousModuleInfo;
	}

	public String getPathName() {
		return pathName;
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
	}

	public Set<ModuleInfo> getSubModuleSet() {
		return subModuleSet;
	}

	public void setSubModuleSet(Set<ModuleInfo> subModuleSet) {
		this.subModuleSet = subModuleSet;
	}

	public void addSubModuleSet(ModuleInfo moduleInfo){
		this.subModuleSet.add(moduleInfo);
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
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

	public Set<PackageInfo> getMainPackageSet() {
		return mainPackageSet;
	}

	public void setMainPackageSet(Set<PackageInfo> mainPackageSet) {
		this.mainPackageSet = mainPackageSet;
	}

	public void addMainPackageSet(PackageInfo packageInfo) {
		this.mainPackageSet.add(packageInfo);
	}

	public boolean isRootModule() {
		return isRootModule;
	}

	public void setRootModule(boolean rootModule) {
		isRootModule = rootModule;
	}

	public Set<PackageInfo> getTestPackageSet() {
		return testPackageSet;
	}

	public void setTestPackageSet(Set<PackageInfo> testPackageSet) {
		this.testPackageSet = testPackageSet;
	}

	public Set<PackageInfo> getOtherPackageSet() {
		return otherPackageSet;
	}

	public void setOtherPackageSet(Set<PackageInfo> otherPackageSet) {
		this.otherPackageSet = otherPackageSet;
	}

	public void addTestPackageSet(PackageInfo packageInfo) {
		this.testPackageSet.add(packageInfo);
	}

	public void addOtherPackageSet(PackageInfo packageInfo) {
		this.otherPackageSet.add(packageInfo);
	}

}
