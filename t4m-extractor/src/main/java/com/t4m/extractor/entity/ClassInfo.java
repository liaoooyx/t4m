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

	// Metric Meta data
	private Map<SLOCType, Integer> slocCounterMap = new EnumMap<>(SLOCType.class);
	private List<ClassInfo> nestedClassList = new ArrayList<>();
	private List<ClassInfo> extraClassList = new ArrayList<>();
	private List<ClassInfo> extendsClassList = new ArrayList<>();
	private List<ClassInfo> implementsClassList = new ArrayList<>();
	private List<ClassInfo> immediateSubClassList = new ArrayList<>();
	private List<MethodInfo> methodInfoList = new ArrayList<>();// 方法列表
	private List<FieldInfo> fieldInfoList = new ArrayList<>();// 类的class-variable，包括静态变量，（当前实现包括常量）
	private List<ClassInfo> activeDependencyAkaFanOutList = new ArrayList<>();//依赖（引用的类）
	private List<ClassInfo> passiveDependencyAkaFanInList = new ArrayList<>();//被依赖（被其他类引用）
	private Map<String, Integer> outClassMethodCallQualifiedSignatureMap = new HashMap<>(); // 调用的其他类的方法集合
	private Map<String, Integer> localMethodCallQualifiedSignatureMap = new HashMap<>(); // 类的本身方法集合
	private List<Integer> cyclomaticComplexityList = new ArrayList<>();
	private List<String> unresolvedExceptionList = new ArrayList<>();

	// Actual Metric Data
	// basic
	private int numberOfMethods;
	private int numberOfFields;
	private int numberOfEnumConstants;
	private int numberOfAnnotationMembers;
	// Coupling
	private int couplingBetweenObjects;
	private int afferentCoupling; // fanin
	private int efferentCoupling; // fanout
	private String instability; // fanout/fanin+out
	private int messagePassingCoupling; // 类中的本地方法，调用其他类的方法的数量
	//SLOC
	/*
	0--SLOCType.LOGIC_CODE_LINES_FROM_SOURCE_FILE；
	1--SLOCType.PHYSICAL_CODE_LINES_FROM_SOURCE_FILE
	2--SLOCType.COMMENT_LINES_FROM_SOURCE_FILE；
	3--SLOCType.LOGIC_CODE_LINES_FROM_AST；
	4--SLOCType.PHYSICAL_CODE_LINES_FROM_AST；
	5--SLOCType.COMMENT_LINES_FROM_AST
	*/
	private int[] slocArray = new int[6];
	//Response for class
	private int responseForClass;// 所有可以对一个类的消息做出响应的方法个数: 父类方法集合+本地方法集合+调用其他类的方法集合
	// Inheritance
	private int deepOfInheritanceTree;//一个类的父类可以向上追溯的数量，也就是在继承树中，一个类到根类经过了多少次继承。
	private int numberOfChildren;//一个类的直接子类的数量
	//圈复杂度
	private int maxCyclomaticComplexity;
	private String avgCyclomaticComplexity;
	private int weightedMethodsCount;    // sum of all methods complexity
	// cohesion
	private int lackOfCohesionInMethods4;
	private String tightClassCohesion;
	private String looseClassCohesion;

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

	public List<ClassInfo> getExtendsClassList() {
		return extendsClassList;
	}

	public void setExtendsClassList(List<ClassInfo> extendsClassList) {
		this.extendsClassList = extendsClassList;
	}

	public List<ClassInfo> getImplementsClassList() {
		return implementsClassList;
	}

	public void setImplementsClassList(List<ClassInfo> implementsClassList) {
		this.implementsClassList = implementsClassList;
	}

	public List<ClassInfo> getImmediateSubClassList() {
		return immediateSubClassList;
	}

	public void setImmediateSubClassList(List<ClassInfo> immediateSubClassList) {
		this.immediateSubClassList = immediateSubClassList;
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

	public int getCouplingBetweenObjects() {
		return couplingBetweenObjects;
	}

	public void setCouplingBetweenObjects(int couplingBetweenObjects) {
		this.couplingBetweenObjects = couplingBetweenObjects;
	}

	public int getAfferentCoupling() {
		return afferentCoupling;
	}

	public void setAfferentCoupling(int afferentCoupling) {
		this.afferentCoupling = afferentCoupling;
	}

	public int getEfferentCoupling() {
		return efferentCoupling;
	}

	public void setEfferentCoupling(int efferentCoupling) {
		this.efferentCoupling = efferentCoupling;
	}

	public String getInstability() {
		return instability;
	}

	public void setInstability(String instability) {
		this.instability = instability;
	}

	public int getMessagePassingCoupling() {
		return messagePassingCoupling;
	}

	public void setMessagePassingCoupling(int messagePassingCoupling) {
		this.messagePassingCoupling = messagePassingCoupling;
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

	public Map<String, Integer> getOutClassMethodCallQualifiedSignatureMap() {
		return outClassMethodCallQualifiedSignatureMap;
	}

	public void setOutClassMethodCallQualifiedSignatureMap(
			Map<String, Integer> outClassMethodCallQualifiedSignatureMap) {
		this.outClassMethodCallQualifiedSignatureMap = outClassMethodCallQualifiedSignatureMap;
	}

	public Map<String, Integer> getLocalMethodCallQualifiedSignatureMap() {
		return localMethodCallQualifiedSignatureMap;
	}

	public void setLocalMethodCallQualifiedSignatureMap(
			Map<String, Integer> localMethodCallQualifiedSignatureMap) {
		this.localMethodCallQualifiedSignatureMap = localMethodCallQualifiedSignatureMap;
	}

	public int getResponseForClass() {
		return responseForClass;
	}

	public void setResponseForClass(int responseForClass) {
		this.responseForClass = responseForClass;
	}

	public int getDeepOfInheritanceTree() {
		return deepOfInheritanceTree;
	}

	public void setDeepOfInheritanceTree(int deepOfInheritanceTree) {
		this.deepOfInheritanceTree = deepOfInheritanceTree;
	}

	public int getNumberOfChildren() {
		return numberOfChildren;
	}

	public void setNumberOfChildren(int numberOfChildren) {
		this.numberOfChildren = numberOfChildren;
	}

	public List<Integer> getCyclomaticComplexityList() {
		return cyclomaticComplexityList;
	}

	public void setCyclomaticComplexityList(List<Integer> cyclomaticComplexityList) {
		this.cyclomaticComplexityList = cyclomaticComplexityList;
	}

	public int getMaxCyclomaticComplexity() {
		return maxCyclomaticComplexity;
	}

	public void setMaxCyclomaticComplexity(int maxCyclomaticComplexity) {
		this.maxCyclomaticComplexity = maxCyclomaticComplexity;
	}

	public String getAvgCyclomaticComplexity() {
		return avgCyclomaticComplexity;
	}

	public void setAvgCyclomaticComplexity(String avgCyclomaticComplexity) {
		this.avgCyclomaticComplexity = avgCyclomaticComplexity;
	}

	public int getWeightedMethodsCount() {
		return weightedMethodsCount;
	}

	public void setWeightedMethodsCount(int weightedMethodsCount) {
		this.weightedMethodsCount = weightedMethodsCount;
	}

	public int getLackOfCohesionInMethods4() {
		return lackOfCohesionInMethods4;
	}

	public void setLackOfCohesionInMethods4(int lackOfCohesionInMethods4) {
		this.lackOfCohesionInMethods4 = lackOfCohesionInMethods4;
	}

	public String getTightClassCohesion() {
		return tightClassCohesion;
	}

	public void setTightClassCohesion(String tightClassCohesion) {
		this.tightClassCohesion = tightClassCohesion;
	}

	public String getLooseClassCohesion() {
		return looseClassCohesion;
	}

	public void setLooseClassCohesion(String looseClassCohesion) {
		this.looseClassCohesion = looseClassCohesion;
	}

	public int[] getSlocArray() {
		return slocArray;
	}

	public void setSlocArray(int[] slocArray) {
		this.slocArray = slocArray;
	}

	/**
	 * Source File以文件为单位，包括了package, import, nested class，non public class
	 * AST以类为单位，不包括package和 import，对于注释和代码也会进行格式化
	 * （比如注释会与代码行不会混合，注释行会被单独提取成行；一个stmt成一行）
	 */
	public Map<SLOCType, Integer> initSlocCounterMap() {
		this.slocCounterMap.put(SLOCType.LOGIC_CODE_LINES_FROM_SOURCE_FILE, 0); // 不包括空白行，单独大括号和注释行
		this.slocCounterMap.put(SLOCType.COMMENT_LINES_FROM_SOURCE_FILE, 0); // 包括这样的注释和代码混合的行
		this.slocCounterMap.put(SLOCType.PHYSICAL_CODE_LINES_FROM_SOURCE_FILE, 0);  // 包括代码行、大括号，不包括单独的注释行
		this.slocCounterMap.put(SLOCType.LOGIC_CODE_LINES_FROM_AST, 0); // 不包括空白行，单独大括号和注释行
		this.slocCounterMap.put(SLOCType.COMMENT_LINES_FROM_AST, 0); // 不包括"//"注释行，只包括"/**/"的doc注释行
		this.slocCounterMap.put(SLOCType.PHYSICAL_CODE_LINES_FROM_AST, 0);  // 包括代码行、大括号，不包括单独的注释行
		return slocCounterMap;
	}

	public enum ClassModifier {
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

	public enum SLOCType {
		LOGIC_CODE_LINES_FROM_SOURCE_FILE,
		PHYSICAL_CODE_LINES_FROM_SOURCE_FILE,
		COMMENT_LINES_FROM_SOURCE_FILE,
		LOGIC_CODE_LINES_FROM_AST,
		PHYSICAL_CODE_LINES_FROM_AST,
		COMMENT_LINES_FROM_AST
	}

	public enum ClassDeclaration {
		NESTED_CLASS, // 嵌套类：包括static nested class和inner class
		EXTRA_CLASS, // 非public的外部类
		MAIN_PUBLIC_CLASS // 唯一的public外部类，与java文件名一致
	}

	@Override
	public String toString() {
		return "ClassInfo{" + "fullyQualifiedName='" + fullyQualifiedName + '\'' + '}';
	}
}