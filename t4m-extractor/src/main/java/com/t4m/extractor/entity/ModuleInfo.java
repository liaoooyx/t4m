package com.t4m.extractor.entity;

import java.util.List;

/**
 * Created by Yuxiang Liao on 2020-06-11 09:29.
 */
public class ModuleInfo {

	private String moduleName;
	private String modulePath;

	private ModuleScope moduleScope;

	private List<PackageInfo> packageList;

	private boolean isRootModule = false;

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

	public ModuleScope getModuleScope() {
		return moduleScope;
	}

	public void setModuleScope(ModuleScope moduleScope) {
		this.moduleScope = moduleScope;
	}

	public List<PackageInfo> getPackageList() {
		return packageList;
	}

	public void setPackageList(List<PackageInfo> packageList) {
		this.packageList = packageList;
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
				", moduleScope=" + moduleScope + ", packageList=" + packageList + ", isRootModule=" + isRootModule +
				'}';
	}
}
