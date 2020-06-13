package com.t4m.extractor.entity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yuxiang Liao on 2020-06-09 14:02.
 */
public class ProjectInfo {

	private String rootPath;
	private String projectName;

	private ModuleInfo rootModule;

	private List<ModuleInfo> moduleList;
	private List<PackageInfo> packageList;
	private List<ClassInfo> classList;

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public ModuleInfo getRootModule() {
		return rootModule;
	}

	public void setRootModule(ModuleInfo rootModule) {
		this.rootModule = rootModule;
	}

	public List<ModuleInfo> getModuleList() {
		return moduleList;
	}

	public void setModuleList(List<ModuleInfo> moduleList) {
		this.moduleList = moduleList;
	}

	public void addModuleList(ModuleInfo moduleInfo) {
		moduleList.add(moduleInfo);
	}

	public List<ClassInfo> getClassList() {
		return classList;
	}

	public void setClassList(List<ClassInfo> classList) {
		this.classList = classList;
	}

	public List<PackageInfo> getPackageList() {
		return packageList;
	}

	public void setPackageList(List<PackageInfo> packageList) {
		this.packageList = packageList;
	}
}