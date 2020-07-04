package com.t4m.extractor.entity;

import com.t4m.extractor.util.EntityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by Yuxiang Liao on 2020-06-09 14:02.
 */
public class ProjectInfo implements Serializable {

	private static final long serialVersionUID = 3553544572450478178L;

	public static final Logger LOGGER = LoggerFactory.getLogger(ProjectInfo.class);

	private Date createDate;

	private String absolutePath;
	private String projectDirName;

	private DirHierarchyNode rootDirHierarchyNode;

	private List<ModuleInfo> moduleList = new ArrayList<>();
	private List<PackageInfo> packageList = new ArrayList<>();

	private List<ClassInfo> classList = new ArrayList<>();
	private List<ClassInfo> innerClassList = new ArrayList<>();
	private List<ClassInfo> extraClassList = new ArrayList<>();

	public ProjectInfo(String absolutePath) {
		this(new Date(), absolutePath);
		String[] paths = absolutePath.split(File.separator);
		this.projectDirName = paths[paths.length - 1];

	}

	public ProjectInfo(Date createDate, String absolutePath) {
		this.createDate = createDate;
		this.absolutePath = absolutePath;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ProjectInfo that = (ProjectInfo) o;
		return Objects.equals(createDate, that.createDate) && Objects.equals(absolutePath, that.absolutePath) &&
				Objects.equals(projectDirName, that.projectDirName) && Objects.equals(rootDirHierarchyNode,
				                                                                      that.rootDirHierarchyNode) &&
				Objects.equals(moduleList, that.moduleList) && Objects.equals(packageList, that.packageList) &&
				Objects.equals(classList, that.classList) && Objects.equals(innerClassList, that.innerClassList);
	}

	@Override
	public int hashCode() {
		return Objects.hash(createDate, absolutePath, projectDirName, rootDirHierarchyNode, moduleList, packageList,
		                    classList, innerClassList);
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
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

	public DirHierarchyNode getRootDirHierarchyNode() {
		return rootDirHierarchyNode;
	}

	public void setRootDirHierarchyNode(DirHierarchyNode rootDirHierarchyNode) {
		this.rootDirHierarchyNode = rootDirHierarchyNode;
	}

	public List<ModuleInfo> getModuleList() {
		return moduleList;
	}

	public void setModuleList(List<ModuleInfo> moduleList) {
		this.moduleList = moduleList;
	}

	public List<ClassInfo> getClassList() {
		return classList;
	}

	public void setClassList(List<ClassInfo> classList) {
		this.classList = classList;
	}

	public List<ClassInfo> getInnerClassList() {
		return innerClassList;
	}

	public void setInnerClassList(List<ClassInfo> innerClassList) {
		this.innerClassList = innerClassList;
	}

	public List<ClassInfo> getExtraClassList() {
		return extraClassList;
	}

	public void setExtraClassList(List<ClassInfo> extraClassList) {
		this.extraClassList = extraClassList;
	}

	public List<ClassInfo> getAllClassList(){
		List<ClassInfo> all = new ArrayList<>();
		all.addAll(classList);
		all.addAll(innerClassList);
		all.addAll(extraClassList);
		return all;
	}

	/**
	 * 优先从当前模块中查找类，如果当前模块中没有，则从整个项目中查找 一般情况下，全限定类名是不重复的，因此只需要从整个项目中发现即可；但不排除包名和类目都重复的情况（比如多个版本）这种情况默认先从当前的包中查询。
	 */
	public ClassInfo getClassInfoByFullyQualifiedName(String fullyQualifiedClassName, ModuleInfo moduleInfo) {
		List<ClassInfo> tempClassList = new ArrayList<>();
		if (moduleInfo.hasMainPackageList()) {
			moduleInfo.getMainPackageList().forEach(tempModule -> {
				tempClassList.addAll(tempModule.getClassList());
			});
		} else if (moduleInfo.hasOtherPackageList()) {
			moduleInfo.getOtherPackageList().forEach(tempModule -> {
				tempClassList.addAll(tempModule.getClassList());
			});
		}
		return EntityUtil.getClassByQualifiedName(tempClassList, fullyQualifiedClassName);
	}


	public List<PackageInfo> getPackageList() {
		return packageList;
	}

	public void setPackageList(List<PackageInfo> packageList) {
		this.packageList = packageList;
	}

	/**
	 * 优先从当前模块中查找包，如果当前模块中没有，则从整个项目中查找 一般情况下，全限定包名是不重复的，因此只需要从整个项目中发现即可；但不排除包名重复的情况，这种情况默认先从当前的包中查询。
	 */
	public PackageInfo getPackageInfoByFullyQualifiedName(String fullyQualifiedPackageName, ModuleInfo moduleInfo) {
		PackageInfo packageInfo = null;
		if (moduleInfo.hasMainPackageList()) {
			packageInfo = findPackageInfoFromList(moduleInfo.getMainPackageList(), fullyQualifiedPackageName);
		} else if (moduleInfo.hasOtherPackageList()) {
			packageInfo = findPackageInfoFromList(moduleInfo.getOtherPackageList(), fullyQualifiedPackageName);
		}
		if (packageInfo != null) {
			return packageInfo;
		} else {
			return findPackageInfoFromList(packageList, fullyQualifiedPackageName);
		}
	}

	private PackageInfo findPackageInfoFromList(List<PackageInfo> pkgInfoList, String fullyQualifiedPackageName) {
		Optional<PackageInfo> optProjectInfo = pkgInfoList.stream().filter(
				pkg -> fullyQualifiedPackageName.equals(pkg.getFullyQualifiedName())).findFirst();
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