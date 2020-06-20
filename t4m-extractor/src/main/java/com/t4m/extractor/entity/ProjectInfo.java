package com.t4m.extractor.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Yuxiang Liao on 2020-06-09 14:02.
 */
public class ProjectInfo {

	public static final Logger LOGGER = LoggerFactory.getLogger(ProjectInfo.class);

	private String absolutePath;
	private String projectDirName;

	private DirectoryNode rootNode;

	private List<ModuleInfo> moduleList = new ArrayList<>();
	private List<PackageInfo> packageList = new ArrayList<>();
	private List<ClassInfo> classList = new ArrayList<>();

	public ProjectInfo(String absolutePath) {
		this.absolutePath = absolutePath;
		this.projectDirName = new File(absolutePath).getName();
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	public String getProjectDirName() {
		return projectDirName;
	}

	public void setProjectDirName(String projectDirName) {
		this.projectDirName = projectDirName;
	}

	public DirectoryNode getRootNode() {
		return rootNode;
	}

	public void setRootNode(DirectoryNode rootNode) {
		this.rootNode = rootNode;
	}

	public List<ModuleInfo> getModuleList() {
		return moduleList;
	}

	public void setModuleList(List<ModuleInfo> moduleList) {
		this.moduleList = moduleList;
	}

	/**
	 * 避免添加重复元素，参数类需要重写{@code equals()}和{@code hashCode()}方法。 如果对象不存在列表中，则添加并返回该对象；如果对象已存在，则从列表中获取并返回该对象。
	 */
	public ModuleInfo safeAddModuleList(ModuleInfo moduleInfo) {
		int index;
		if ((index = moduleList.indexOf(moduleInfo)) == -1) {
			this.moduleList.add(moduleInfo);
			return moduleInfo;
		} else {
			return this.moduleList.get(index);
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
			LOGGER.debug("{} exists in {}", classInfo.getFullyQualifiedName(), classInfo.getAbsolutePath());
			return this.classList.get(index);
		}
	}

	public List<PackageInfo> getPackageList() {
		return packageList;
	}

	public void setPackageList(List<PackageInfo> packageList) {
		this.packageList = packageList;
	}

	/**
	 * 避免添加重复元素，参数类需要重写{@code equals()}和{@code hashCode()}方法。 如果对象不存在列表中，则添加并返回该对象；如果对象已存在，则从列表中获取并返回该对象。
	 */
	public PackageInfo safeAddPackageList(PackageInfo packageInfo) {
		int index;
		if ((index = packageList.indexOf(packageInfo)) == -1) {
			this.packageList.add(packageInfo);
			return packageInfo;
		} else {
			return this.packageList.get(index);
		}
	}

	/**
	 * 根据全限定包名获取对象，返回获得的第一个对象，如果不存在则返回{@code null}.
	 */
	public PackageInfo getPackageInfoByFullyQualifiedName(String fullyQualifiedName) {
		Optional<PackageInfo> optProjectInfo = packageList.stream().filter(
				pkg -> fullyQualifiedName.equals(pkg.getFullyQualifiedName())).findFirst();
		return optProjectInfo.orElse(null);
	}

	/**
	 * 根据包绝对路径获取对象，返回获得的第一个对象，如果不存在则返回{@code null}.
	 */
	public PackageInfo getPackageInfoByAbsolutePath(String absolutePath) {
		int i = this.packageList.indexOf(new PackageInfo(absolutePath));
		if (i != -1)
			return this.packageList.get(i);
		else
			return null;
	}
}