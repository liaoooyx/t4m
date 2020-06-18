package com.t4m.extractor.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Yuxiang Liao at 2020-06-09 22:55.
 */
public class ClassInfo {

	private String shortName;
	private String fullyQualifiedName; // fully-qualified class name
	private String absolutePath;

	private PackageInfo packageInfo;
	private String packageFullyQualifiedName;

	private ClassInfo.Type type;

	// 考虑内部类
	private List<ClassInfo> innerClassList = new ArrayList<>();

	// private ClassInfo hasAbstractClass;
	// private Set<ClassInfo> hasInterfaceClass;
	//
	// private List<ClassInfo> dependsOn;
	// private List<ClassInfo> dependedBy;
	//
	// private Set<MethodInfo> methodSet;
	//
	// // instance variables in class, used by MethodInfo
	// // Format: instancName:instanceType
	// // such as "instanceSet:Set"
	// private Set<String> instanceSet;
	//
	// private int numberOfMethods;
	// private int numberOfInstances;
	//
	// private int sourceLinesOfCode;
	// private int blankLines;
	// private int effectiveLinesOfCode;
	// private int commentLinesOfCode;

	public ClassInfo(String absolutePath) {
		this.fullyQualifiedName = fullyQualifiedName;
		this.absolutePath = absolutePath;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ClassInfo classInfo = (ClassInfo) o;
		return Objects.equals(absolutePath, classInfo.absolutePath);
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

	public String getFullyQualifiedName() {
		return fullyQualifiedName;
	}

	public void setFullyQualifiedName(String fullyQualifiedName) {
		this.fullyQualifiedName = fullyQualifiedName;
	}

	public PackageInfo getPackageInfo() {
		return packageInfo;
	}

	public void setPackageInfo(PackageInfo packageInfo) {
		this.packageInfo = packageInfo;
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	public String getPackageFullyQualifiedName() {
		return packageFullyQualifiedName;
	}

	public void setPackageFullyQualifiedName(String packageFullyQualifiedName) {
		this.packageFullyQualifiedName = packageFullyQualifiedName;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public List<ClassInfo> getInnerClassList() {
		return innerClassList;
	}

	public void setInnerClassList(List<ClassInfo> innerClassList) {
		this.innerClassList = innerClassList;
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
			return this.innerClassList.get(index);
		}
	}

	public static enum Type {
		CLASS,
		ABSTRACT,
		INTERFACE;
	}
}
