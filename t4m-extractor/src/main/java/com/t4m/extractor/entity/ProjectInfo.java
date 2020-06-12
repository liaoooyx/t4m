package com.t4m.extractor.entity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Yuxiang Liao on 2020-06-09 14:02.
 */
public class ProjectInfo {

	private String rootPath;
	private String projectName;

	private ModuleInfo rootModule;
	private List<ModuleInfo> moduleList;

	public ProjectInfo() {
		this.moduleList = new ArrayList<>();
	}

	public ProjectInfo(List<ModuleInfo> moduleList) {
		this.moduleList = moduleList;
	}

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

	public void addModuleList(ModuleInfo moduleInfo){
		moduleList.add(moduleInfo);
	}
}