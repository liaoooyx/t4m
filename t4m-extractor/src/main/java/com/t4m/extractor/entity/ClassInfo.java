package com.t4m.extractor.entity;

import com.t4m.extractor.metric.SLOCMetric;

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

	private ClassModifier classModifier; // 如果该类的源文件为package-info.java，那么可能不存在类修饰符，因为该文件可以不存在类，只包含注释。
	private ClassDeclaration classDeclaration;

	// 对于extraClass的innerClass来说，mainOuterClass与outerClass是不一致的。
	private ClassInfo mainPublicClass; //唯一的公共外部类
	private ClassInfo outerClass; //内部类的外部类

	private List<ClassInfo> innerClassList = new ArrayList<>();
	private List<ClassInfo> extraClassList = new ArrayList<>();

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
	private int numberOfEnumConstants;
	private int numberOfAnnotationMembers;

	//SLOC counts the number of lines in the source file that are not: blank or empty lines, braces, or comments.
	Map<SLOCType, Integer> slocCounterMap = new HashMap<>();

	public ClassInfo(String shortName, String absolutePath) {
		this.shortName = shortName;
		this.absolutePath = absolutePath;
	}

	public ClassInfo(String innerClassShortName, ClassInfo mainPublicClass) {
		this.shortName = innerClassShortName;
		this.absolutePath = mainPublicClass.absolutePath;
		this.fullyQualifiedName = mainPublicClass.fullyQualifiedName.replaceFirst(mainPublicClass.shortName + "$",
		                                                                          Matcher.quoteReplacement(
				                                                                     innerClassShortName));
		this.packageInfo = mainPublicClass.packageInfo;
		this.packageFullyQualifiedName = mainPublicClass.packageFullyQualifiedName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ClassInfo classInfo = (ClassInfo) o;
		return Objects.equals(fullyQualifiedName, classInfo.fullyQualifiedName) && Objects.equals(absolutePath,
		                                                                                          classInfo.absolutePath);
	}

	@Override
	public int hashCode() {
		return Objects.hash(fullyQualifiedName, absolutePath);
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

	public ClassDeclaration getClassDeclaration() {
		return classDeclaration;
	}

	public void setClassDeclaration(ClassDeclaration classDeclaration) {
		this.classDeclaration = classDeclaration;
	}

	public ClassInfo getMainPublicClass() {
		return mainPublicClass;
	}

	public void setMainPublicClass(ClassInfo mainPublicClass) {
		this.mainPublicClass = mainPublicClass;
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

	public List<ClassInfo> getExtraClassList() {
		return extraClassList;
	}

	public void setExtraClassList(List<ClassInfo> extraClassList) {
		this.extraClassList = extraClassList;
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

	public List<ClassInfo> getActiveDependencyList() {
		return activeDependencyList;
	}

	public void setActiveDependencyList(List<ClassInfo> activeDependencyList) {
		this.activeDependencyList = activeDependencyList;
	}

	public List<ClassInfo> getPassiveDependencyList() {
		return passiveDependencyList;
	}

	public void setPassiveDependencyList(List<ClassInfo> passiveDependencyList) {
		this.passiveDependencyList = passiveDependencyList;
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

	public int getNumberOfEnumConstants() {
		return numberOfEnumConstants;
	}

	public void setNumberOfEnumConstants(int numberOfEnumConstants) {
		this.numberOfEnumConstants = numberOfEnumConstants;
	}

	public int getNumberOfAnnotationMembers() {
		return numberOfAnnotationMembers;
	}

	public void setNumberOfAnnotationMembers(int numberOfAnnotationMembers) {
		this.numberOfAnnotationMembers = numberOfAnnotationMembers;
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

	/**
	 * 如果是内部类，那么它关于source file的三项将为0，因为无法从source file中判断出内部类。 如果是外部类，那么它的六项都包括内部类 对于AST格式的SLOC，它的数值与源文件可能不一致，因为AST格式会对部分代码行合并，比如方法注解和方法声明会合并为一行
	 */
	public Map<SLOCType, Integer> initSlocCounterMap() {
		this.slocCounterMap.put(SLOCType.LOGIC_CODE_LINES_FROM_SOURCE_FILE, 0); // 不包括空白行，单独大括号和注释行
		this.slocCounterMap.put(SLOCType.ALL_COMMENT_LINES_FROM_SOURCE_FILE, 0); // 包括这样的注释和代码混合的行
		this.slocCounterMap.put(SLOCType.PHYSICAL_CODE_LINES_FROM_SOURCE_FILE, 0);  // 包括代码行、大括号，不包括单独的注释行
		this.slocCounterMap.put(SLOCType.LOGIC_CODE_LINES_FROM_AST, 0); // 不包括空白行，单独大括号和注释行
		this.slocCounterMap.put(SLOCType.DOC_COMMENT_LINES_FROM_AST, 0); // 不包括"//"注释行，只包括"/**/"的doc注释行
		this.slocCounterMap.put(SLOCType.PHYSICAL_CODE_LINES_FROM_AST, 0);  // 包括代码行、大括号，不包括单独的注释行
		return slocCounterMap;
	}

	/**
	 * 获取自身的SLOC，以数组形式返回。索引与对应的值，查看{@link SLOCMetric.sumSLOC()}
	 */
	public int[] getSumOfSLOC() {
		int[] slocArray = new int[6];
		Arrays.fill(slocArray, 0);
		SLOCMetric.sumSLOC(slocArray, slocCounterMap);
		return slocArray;
	}

	public static enum ClassModifier {
		CLASS,
		ENUM,
		ANNOTATION,
		ABSTRACT_CLASS,
		INTERFACE;
	}

	public static enum SLOCType {
		LOGIC_CODE_LINES_FROM_SOURCE_FILE,
		PHYSICAL_CODE_LINES_FROM_SOURCE_FILE,
		ALL_COMMENT_LINES_FROM_SOURCE_FILE,
		LOGIC_CODE_LINES_FROM_AST,
		PHYSICAL_CODE_LINES_FROM_AST,
		DOC_COMMENT_LINES_FROM_AST;
	}

	public static enum ClassDeclaration {
		INNER_CLASS, // 内部类
		EXTRA_CLASS, // 非public的外部类
		MAIN_PUBLIC_CLASS; // 唯一的public外部类，与java文件名一致
	}

	@Override
	public String toString() {
		return "ClassInfo{" + "fullyQualifiedName='" + fullyQualifiedName + '\'' + '}';
	}
}
