package com.t4m.extractor.entity;

import com.t4m.extractor.util.EntityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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

	private String excludedPath;
	private String dependencyPath;

	private List<ModuleInfo> moduleList = new ArrayList<>();
	private List<PackageInfo> packageList = new ArrayList<>();

	private List<ClassInfo> classList = new ArrayList<>();
	private List<ClassInfo> nestedClassList = new ArrayList<>();
	private List<ClassInfo> extraClassList = new ArrayList<>();
	private List<MethodInfo> methodList = new ArrayList<>();

	public ProjectInfo(String absolutePath, String excludedPath, String dependencyPath) {
		this(absolutePath, excludedPath, dependencyPath, new Date());
	}

	public ProjectInfo(String absolutePath, String excludedPath, String dependencyPath, Date createDate) {
		this.createDate = createDate;
		this.absolutePath = absolutePath;
		String[] paths = absolutePath.split(File.separator);
		this.projectDirName = paths[paths.length - 1];
		this.excludedPath = excludedPath;
		this.dependencyPath = dependencyPath;
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
				Objects.equals(classList, that.classList) && Objects.equals(nestedClassList, that.nestedClassList);
	}

	@Override
	public int hashCode() {
		return Objects.hash(createDate, absolutePath, projectDirName, rootDirHierarchyNode, moduleList, packageList,
		                    classList, nestedClassList);
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

	public String getExcludedPath() {
		return excludedPath;
	}

	public void setExcludedPath(String excludedPath) {
		this.excludedPath = excludedPath;
	}

	public String getDependencyPath() {
		return dependencyPath;
	}

	public void setDependencyPath(String dependencyPath) {
		this.dependencyPath = dependencyPath;
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

	public List<ClassInfo> getNestedClassList() {
		return nestedClassList;
	}

	public void setNestedClassList(List<ClassInfo> nestedClassList) {
		this.nestedClassList = nestedClassList;
	}

	public List<ClassInfo> getExtraClassList() {
		return extraClassList;
	}

	public void setExtraClassList(List<ClassInfo> extraClassList) {
		this.extraClassList = extraClassList;
	}

	public List<PackageInfo> getPackageList() {
		return packageList;
	}

	public void setPackageList(List<PackageInfo> packageList) {
		this.packageList = packageList;
	}

	public List<MethodInfo> getMethodList() {
		return methodList;
	}

	public void setMethodList(List<MethodInfo> methodList) {
		this.methodList = methodList;
	}

	public List<ClassInfo> getAllClassList() {
		List<ClassInfo> all = new ArrayList<>();
		all.addAll(classList);
		all.addAll(nestedClassList);
		all.addAll(extraClassList);
		return all;
	}

	/**
	 * 优先从当前模块中查找类，如果当前模块中没有，则从整个项目中查找（包括其他模块） 一般情况下，全限定类名是不重复的，直接从整个项目中发现即可； 但不排除包名和类名都重复的情况（比如多个版本），所以默认先从当前的包中查询。
	 */
	public ClassInfo getClassInfoByFullyQualifiedName(String fullyQualifiedClassName, ModuleInfo moduleInfo) {
		List<ClassInfo> tempClassList = new ArrayList<>();
		moduleInfo.getPackageList().forEach(pkg -> tempClassList.addAll(pkg.getClassList()));
		ClassInfo targetClass = EntityUtil.getClassByQualifiedName(tempClassList, fullyQualifiedClassName);
		if (targetClass != null) {
			return targetClass;
		} else {
			return EntityUtil.getClassByQualifiedName(getAllClassList(), fullyQualifiedClassName);
		}
	}


	/**
	 * 优先从当前模块中查找包，如果当前模块中没有，则从整个项目中查找（包括其他模块）。 一般情况下，全限定包名是不重复的，因此只需要从整个项目中发现即可；但不排除包名重复的情况，所默认先从当前的模块中查询。
	 */
	public PackageInfo getPackageInfoByFullyQualifiedName(String fullyQualifiedPackageName, ModuleInfo moduleInfo) {
		PackageInfo packageInfo = EntityUtil.getPackageByQualifiedName(moduleInfo.getPackageList(),
		                                                               fullyQualifiedPackageName);
		if (packageInfo != null) {
			return packageInfo;
		} else {
			return EntityUtil.getPackageByQualifiedName(packageList, fullyQualifiedPackageName);
		}
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