package com.t4m.extractor.entity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Created by Yuxiang Liao on 2020-06-11 09:29.
 */
public class ModuleInfo {

	private String moduleName;
	private String modulePath;

	private boolean isMainScope; // 只有为scr->main->java的情况下，才视为正在开发的模块；

	private Set<PackageInfo> packageSet = new HashSet<>();

	private boolean isRootModule = false;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ModuleInfo that = (ModuleInfo) o;
		return Objects.equals(modulePath, that.modulePath);
	}

	@Override
	public int hashCode() {
		return Objects.hash(modulePath);
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getModulePath() {
		return modulePath;
	}

	public void setModulePath(String modulePath) {
		this.modulePath = modulePath;
	}

	public boolean isMainScope() {
		return isMainScope;
	}

	public void setMainScope(boolean mainScope) {
		isMainScope = mainScope;
	}

	public Set<PackageInfo> getPackageSet() {
		return packageSet;
	}

	public void setPackageSet(Set<PackageInfo> packageSet) {
		this.packageSet = packageSet;
	}

	public void addPackageSet(PackageInfo packageInfo) {
		this.packageSet.add(packageInfo);
	}

	public boolean isRootModule() {
		return isRootModule;
	}

	public void setRootModule(boolean rootModule) {
		isRootModule = rootModule;
	}

	@Override
	public String toString() {
		return "ModuleInfo{" + "moduleName='" + moduleName + '\'' + ", modulePath='" + modulePath + '\'' +
				", isMainScope=" + isMainScope + ", packageSet=" + packageSet + ", isRootModule=" + isRootModule + '}';
	}

}
