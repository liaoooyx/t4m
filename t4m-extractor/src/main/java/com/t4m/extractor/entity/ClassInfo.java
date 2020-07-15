package com.t4m.extractor.entity;

import com.t4m.extractor.metric.SLOCMetric;

import java.io.Serializable;
import java.util.*;

/**
 * Created by Yuxiang Liao at 2020-06-09 22:55.
 */
public class ClassInfo implements Serializable {

	private static final long serialVersionUID = 2417256803742933401L;

	private String shortName; // 嵌套类的类名为 A.B
	private String fullyQualifiedName; // fully-qualified class name
	private String absolutePath;

	private PackageInfo packageInfo;
	private String packageFullyQualifiedName;

	private ClassModifier classModifier; // 如果该类的源文件为package-info.java，那么可能不存在类修饰符，因为该文件可以不存在类，只包含注释。
	private ClassDeclaration classDeclaration;

	// 对于extraClass的innerClass来说，mainOuterClass与outerClass是不一致的。
	private ClassInfo mainPublicClass; //唯一的公共外部类
	private ClassInfo outerClass; //内部类的外部类

	private List<ClassInfo> nestedClassList = new ArrayList<>();
	private List<ClassInfo> extraClassList = new ArrayList<>();

	private List<ClassInfo> extendedClassList = new ArrayList<>();
	private List<ClassInfo> implementedClassList = new ArrayList<>();

	//依赖（引用的类）
	private List<ClassInfo> activeDependencyAkaFanOutList = new ArrayList<>();
	//被依赖（被其他类引用）
	private List<ClassInfo> passiveDependencyAkaFanInList = new ArrayList<>();

	// 方法列表
	private List<MethodInfo> methodInfoList = new ArrayList<>();
	// 类的class-variable，包括静态变量，不包括常量
	private List<FieldInfo> fieldInfoList = new ArrayList<>();

	private int numberOfMethods;
	private int numberOfFields;
	private int numberOfEnumConstants;
	private int numberOfAnnotationMembers;

	private List<String> unresolvedExceptionList = new ArrayList<>();

	//SLOC counts the number of lines in the source file that are not: blank or empty lines, braces, or comments.
	private Map<SLOCType, Integer> slocCounterMap = new HashMap<>();

	//Response for class
	private Map<String,Integer> rfcMethodQualifiedSignatureMap = new HashMap<>(); // 调用的其他类的方法集合

	//圈复杂度
	private List<Integer> cyclomaticComplexityList = new ArrayList<>();

	public ClassInfo(String shortName, String absolutePath) {
		this.shortName = shortName;
		this.absolutePath = absolutePath;
	}

	public ClassInfo(String innerClassShortName, ClassInfo mainPublicClass) {
		this.shortName = innerClassShortName;
		this.absolutePath = mainPublicClass.absolutePath;
		this.fullyQualifiedName = mainPublicClass.getFullyQualifiedName() + "." + innerClassShortName;
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
		return Objects.equals(shortName, classInfo.shortName) && Objects.equals(fullyQualifiedName,
		                                                                        classInfo.fullyQualifiedName) &&
				Objects.equals(absolutePath, classInfo.absolutePath);
	}

	@Override
	public int hashCode() {
		return Objects.hash(shortName, fullyQualifiedName, absolutePath);
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

	public List<ClassInfo> getExtendedClassList() {
		return extendedClassList;
	}

	public void setExtendedClassList(List<ClassInfo> extendedClassList) {
		this.extendedClassList = extendedClassList;
	}

	public List<ClassInfo> getImplementedClassList() {
		return implementedClassList;
	}

	public void setImplementedClassList(List<ClassInfo> implementedClassList) {
		this.implementedClassList = implementedClassList;
	}

	public List<ClassInfo> getActiveDependencyAkaFanOutList() {
		return activeDependencyAkaFanOutList;
	}

	public void setActiveDependencyAkaFanOutList(List<ClassInfo> activeDependencyAkaFanOutList) {
		this.activeDependencyAkaFanOutList = activeDependencyAkaFanOutList;
	}

	public List<ClassInfo> getPassiveDependencyAkaFanInList() {
		return passiveDependencyAkaFanInList;
	}

	public void setPassiveDependencyAkaFanInList(List<ClassInfo> passiveDependencyAkaFanInList) {
		this.passiveDependencyAkaFanInList = passiveDependencyAkaFanInList;
	}

	public List<MethodInfo> getMethodInfoList() {
		return methodInfoList;
	}

	public void setMethodInfoList(List<MethodInfo> methodInfoList) {
		this.methodInfoList = methodInfoList;
	}

	public List<FieldInfo> getFieldInfoList() {
		return fieldInfoList;
	}

	public void setFieldInfoList(List<FieldInfo> fieldInfoList) {
		this.fieldInfoList = fieldInfoList;
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

	public List<String> getUnresolvedExceptionList() {
		return unresolvedExceptionList;
	}

	public void setUnresolvedExceptionList(List<String> unresolvedExceptionList) {
		this.unresolvedExceptionList = unresolvedExceptionList;
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

	public Map<String, Integer> getRfcMethodQualifiedSignatureMap() {
		return rfcMethodQualifiedSignatureMap;
	}

	public void setRfcMethodQualifiedSignatureMap(Map<String, Integer> rfcMethodQualifiedSignatureMap) {
		this.rfcMethodQualifiedSignatureMap = rfcMethodQualifiedSignatureMap;
	}

	public List<Integer> getCyclomaticComplexityList() {
		return cyclomaticComplexityList;
	}

	public void setCyclomaticComplexityList(List<Integer> cyclomaticComplexityList) {
		this.cyclomaticComplexityList = cyclomaticComplexityList;
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
	 * 获取自身的SLOC，以数组形式返回。索引与对应的值，查看{@link SLOCMetric#sumSLOC(int[], Map)}
	 */
	public int[] getSumOfSLOC() {
		int[] slocArray = new int[6];
		Arrays.fill(slocArray, 0);
		SLOCMetric.sumSLOC(slocArray, slocCounterMap);
		return slocArray;
	}

	public static enum ClassModifier {
		CLASS("class"),
		ENUM("enum"),
		ANNOTATION("annotation"),
		ABSTRACT_CLASS("abstract"),
		INTERFACE("interface");

		String str;

		ClassModifier(String str) {
			this.str = str;
		}

		@Override
		public String toString() {
			return str;
		}
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
