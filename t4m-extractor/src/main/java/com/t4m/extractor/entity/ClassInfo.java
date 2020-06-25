package com.t4m.extractor.entity;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;

/**
 * Created by Yuxiang Liao at 2020-06-09 22:55.
 */
public class ClassInfo implements Serializable {

	private static final long serialVersionUID = 2417256803742933401L;

	private String shortName;
	private String fullyQualifiedName; // fully-qualified class name
	private String absolutePath;

	private PackageInfo packageInfo;
	private String packageFullyQualifiedName;

	private ClassModifier classModifier;
	private boolean innerClass = false;

	// 考虑内部类
	private ClassInfo outerClass;
	private List<ClassInfo> innerClassList = new ArrayList<>();

	private ClassInfo supperClass;
	private List<ClassInfo> interfaceList = new ArrayList<>();

	//依赖（引用的类）
	private List<ClassInfo> activeDependencyList = new ArrayList<>();
	//被依赖（被其他类引用）
	private List<ClassInfo> passiveDependencyList = new ArrayList<>();
	//
	// private Set<MethodInfo> methodSet;
	//
	// // instance variables in class, used by MethodInfo
	// // Format: instancName:instanceType
	// // such as "instanceSet:Set"
	// private Set<String> instanceSet;
	//
	private int numberOfMethods;
	private int numberOfFields;

	//SLOC counts the number of lines in the source file that are not: blank or empty lines, braces, or comments.
	Map<SLOCType, Integer> slocCounterMap = new HashMap<>();

	public ClassInfo(String shortName, String absolutePath) {
		this.shortName = shortName;
		this.absolutePath = absolutePath;
	}

	public ClassInfo(String innerClassShortName, ClassInfo outerClass) {
		this.shortName = innerClassShortName;
		this.absolutePath = outerClass.absolutePath;
		this.fullyQualifiedName = outerClass.fullyQualifiedName.replaceFirst(outerClass.shortName + "$",
		                                                                     Matcher.quoteReplacement(
				                                                                     innerClassShortName));
		this.packageInfo = outerClass.packageInfo;
		this.packageFullyQualifiedName = outerClass.packageFullyQualifiedName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ClassInfo classInfo = (ClassInfo) o;
		return Objects.equals(shortName, classInfo.shortName) && Objects.equals(absolutePath, classInfo.absolutePath);
	}

	@Override
	public int hashCode() {
		return Objects.hash(shortName, absolutePath);
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

	public ClassModifier getClassModifier() {
		return classModifier;
	}

	public void setClassModifier(ClassModifier classModifier) {
		this.classModifier = classModifier;
	}

	public boolean isInnerClass() {
		return innerClass;
	}

	public void setInnerClass(boolean innerClass) {
		this.innerClass = innerClass;
	}

	public ClassInfo getOuterClass() {
		return outerClass;
	}

	public void setOuterClass(ClassInfo outerClass) {
		this.outerClass = outerClass;
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

	public ClassInfo getSupperClass() {
		return supperClass;
	}

	public void setSupperClass(ClassInfo supperClass) {
		this.supperClass = supperClass;
	}

	public List<ClassInfo> getInterfaceList() {
		return interfaceList;
	}

	public void setInterfaceList(List<ClassInfo> interfaceList) {
		this.interfaceList = interfaceList;
	}

	/**
	 * 避免添加重复元素，参数类需要重写{@code equals()}和{@code hashCode()}方法。 如果对象不存在列表中，则添加并返回该对象；如果对象已存在，则从列表中获取并返回该对象。
	 */
	public ClassInfo safeAddInterfaceList(ClassInfo classInfo) {
		int index;
		if ((index = interfaceList.indexOf(classInfo)) == -1) {
			this.interfaceList.add(classInfo);
			return classInfo;
		} else {
			return this.interfaceList.get(index);
		}
	}

	public List<ClassInfo> getActiveDependencyList() {
		return activeDependencyList;
	}

	public void setActiveDependencyList(List<ClassInfo> activeDependencyList) {
		this.activeDependencyList = activeDependencyList;
	}

	/**
	 * 避免添加重复元素，参数类需要重写{@code equals()}和{@code hashCode()}方法。 如果对象不存在列表中，则添加并返回该对象；如果对象已存在，则从列表中获取并返回该对象。
	 */
	public ClassInfo safeAddActiveDependencyList(ClassInfo classInfo) {
		int index;
		if ((index = activeDependencyList.indexOf(classInfo)) == -1) {
			this.activeDependencyList.add(classInfo);
			return classInfo;
		} else {
			return this.activeDependencyList.get(index);
		}
	}

	public List<ClassInfo> getPassiveDependencyList() {
		return passiveDependencyList;
	}

	public void setPassiveDependencyList(List<ClassInfo> passiveDependencyList) {
		this.passiveDependencyList = passiveDependencyList;
	}

	/**
	 * 避免添加重复元素，参数类需要重写{@code equals()}和{@code hashCode()}方法。 如果对象不存在列表中，则添加并返回该对象；如果对象已存在，则从列表中获取并返回该对象。
	 */
	public ClassInfo safeAddPassiveDependencyList(ClassInfo classInfo) {
		int index;
		if ((index = passiveDependencyList.indexOf(classInfo)) == -1) {
			this.passiveDependencyList.add(classInfo);
			return classInfo;
		} else {
			return this.passiveDependencyList.get(index);
		}
	}

	public int getNumberOfMethods() {
		return numberOfMethods;
	}

	public void setNumberOfMethods(int numberOfMethods) {
		this.numberOfMethods = numberOfMethods;
	}

	public int getNumberOfFields() {
		return numberOfFields;
	}

	public void setNumberOfFields(int numberOfFields) {
		this.numberOfFields = numberOfFields;
	}

	public Map<SLOCType, Integer> getSlocCounterMap() {
		if (slocCounterMap == null || slocCounterMap.isEmpty()) {
			initSlocCounterMap();
		}
		return slocCounterMap;
	}

	public void setSlocCounterMap(Map<SLOCType, Integer> slocCounterMap) {
		this.slocCounterMap = slocCounterMap;
	}

	public Map<SLOCType, Integer> initSlocCounterMap() {
		this.slocCounterMap.put(SLOCType.CODE_LINES_FROM_SOURCE_FILE, 0); // 不包括空白行，单独大括号和注释行
		this.slocCounterMap.put(SLOCType.COMMENT_LINES_FROM_SOURCE_FILE, 0); // 包括这样的注释和代码混合的行
		this.slocCounterMap.put(SLOCType.PHYSICAL_LINES_FROM_SOURCE_FILE, 0);  // 包括代码行、大括号，不包括单独的注释行
		this.slocCounterMap.put(SLOCType.CODE_LINES_FROM_AST, 0); // 不包括空白行，单独大括号和注释行
		this.slocCounterMap.put(SLOCType.COMMENT_LINES_FROM_AST, 0); // 包括这样的注释和代码混合的行
		this.slocCounterMap.put(SLOCType.PHYSICAL_LINES_FROM_AST, 0);  // 包括代码行、大括号，不包括单独的注释行
		return slocCounterMap;
	}

	public static enum ClassModifier {
		CLASS,
		ABSTRACT_CLASS,
		INTERFACE;
	}

	public static enum SLOCType {
		CODE_LINES_FROM_SOURCE_FILE,
		COMMENT_LINES_FROM_SOURCE_FILE,
		PHYSICAL_LINES_FROM_SOURCE_FILE,
		CODE_LINES_FROM_AST,
		COMMENT_LINES_FROM_AST,
		PHYSICAL_LINES_FROM_AST;
	}

	@Override
	public String toString() {
		return "ClassInfo{" + "fullyQualifiedName='" + fullyQualifiedName + '\'' + '}';
	}
}
