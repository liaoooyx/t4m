package com.t4m.extractor.entity;

import com.t4m.extractor.util.EntityUtil;
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

	private DirHierarchyNode rootDirHierarchyNode;

	private List<ModuleInfo> moduleList = new ArrayList<>();
	private List<PackageInfo> packageList = new ArrayList<>();
	private List<ClassInfo> classList = new ArrayList<>();
	private List<ClassInfo> innerClassList = new ArrayList<>();

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

	public List<ClassInfo> getInnerClassList() {
		return innerClassList;
	}


	/**
	 * 避免添加重复元素，参数类需要重写{@code equals()}和{@code hashCode()}方法。 如果对象不存在列表中，则添加并返回该对象；如果对象已存在，则从列表中获取并返回该对象。
	 */
	public ClassInfo safeAddInnerClassList(ClassInfo classInfo) {
		int index;
		if ((index = innerClassList.indexOf(classInfo)) == -1) {
			this.innerClassList.add(classInfo);
			return classInfo;
		} else {
			LOGGER.debug("{} exists in {}", classInfo.getFullyQualifiedName(), classInfo.getAbsolutePath());
			return this.innerClassList.get(index);
		}
	}

	public void setInnerClassList(List<ClassInfo> innerClassList) {
		this.innerClassList = innerClassList;
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
	public PackageInfo getPackageInfoByFullyQualifiedName(String fullyQualifiedPackageName) {
		return findPackageInfoFromList(packageList, fullyQualifiedPackageName);
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