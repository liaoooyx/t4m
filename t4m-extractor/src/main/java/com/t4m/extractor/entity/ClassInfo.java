package com.t4m.extractor.entity;

import java.io.Serializable;
import java.util.*;

/**
 * Created by Yuxiang Liao at 2020-06-09 22:55.
 */
public class ClassInfo implements Serializable {

	private static final long serialVersionUID = 2417256803742933401L;

	private String shortName; // The name of nested class will be outerClassName.nestedClassName
	private String fullyQualifiedName; // the fully-qualified class name
	private String absolutePath;

	private PackageInfo packageInfo;
	private String packageFullyQualifiedName;

	private ClassModifier classModifier = ClassModifier.UNIDENTIFIED;
	private ClassDeclaration classDeclaration;

	// For the nested class of package private outer class，the reference of mainOuterClass and outerClass are different.
	private ClassInfo mainPublicClass; // the only public outer class of a java file.
	private ClassInfo outerClass; // the previous outer class of a inner class.

	// ModuleLevelMetric Meta data
	private Map<SLOCType, Integer> slocCounterMap = new EnumMap<>(SLOCType.class);
	private List<ClassInfo> nestedClassList = new ArrayList<>();
	private List<ClassInfo> extraClassList = new ArrayList<>();
	private List<ClassInfo> extendsClassList = new ArrayList<>();
	private List<ClassInfo> implementsClassList = new ArrayList<>();
	private List<ClassInfo> immediateSubClassList = new ArrayList<>();
	private List<MethodInfo> methodInfoList = new ArrayList<>();
	private List<FieldInfo> fieldInfoList = new ArrayList<>(); // including static and final keywords.
	private List<ClassInfo> activeDependencyAkaFanOutList = new ArrayList<>();
	private List<ClassInfo> passiveDependencyAkaFanInList = new ArrayList<>();
	// A set of methods that belongs to other classes, and invoked by this class.
	private Map<String, Integer> outClassMethodCallQualifiedSignatureMap = new HashMap<>();
	// A set of methods that belongs to this class and invoked by within itself.
	private Map<String, Integer> localMethodCallQualifiedSignatureMap = new HashMap<>();
	private List<Integer> cyclomaticComplexityList = new ArrayList<>();
	private List<String> unresolvedExceptionList = new ArrayList<>();

	// Actual ModuleLevelMetric Data
	// Basic
	private int numberOfMethods;
	private int numberOfFields;
	private int numberOfEnumConstants;
	private int numberOfAnnotationMembers;
	// Coupling
	private int couplingBetweenObjects;
	private int afferentCoupling; // fanin
	private int efferentCoupling; // fanout
	private String instability; // fanout/fanin+out
	private int messagePassingCoupling;
	// SLOC
	/*
	0--SLOCType.LOGIC_CODE_LINES_FROM_SOURCE_FILE；
	1--SLOCType.PHYSICAL_CODE_LINES_FROM_SOURCE_FILE
	2--SLOCType.COMMENT_LINES_FROM_SOURCE_FILE；
	3--SLOCType.LOGIC_CODE_LINES_FROM_AST；
	4--SLOCType.PHYSICAL_CODE_LINES_FROM_AST；
	5--SLOCType.COMMENT_LINES_FROM_AST
	*/
	private int[] slocArray = new int[6];
	// Response For a Class
	private int responseForClass;
	// Inheritance
	private int deepOfInheritanceTree;
	private int numberOfChildren;
	// Cyclomatic Comlexity
	private int maxCyclomaticComplexity;
	private String avgCyclomaticComplexity;
	private int weightedMethodsCount;
	// Cohesion
	private int lackOfCohesionOfMethods4;
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

	public int getLackOfCohesionOfMethods4() {
		return lackOfCohesionOfMethods4;
	}

	public void setLackOfCohesionOfMethods4(int lackOfCohesionOfMethods4) {
		this.lackOfCohesionOfMethods4 = lackOfCohesionOfMethods4;
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


	private void initSlocCounterMap() {
		this.slocCounterMap.put(SLOCType.LOGIC_CODE_LINES_FROM_SOURCE_FILE, 0);
		this.slocCounterMap.put(SLOCType.COMMENT_LINES_FROM_SOURCE_FILE, 0);
		this.slocCounterMap.put(SLOCType.PHYSICAL_CODE_LINES_FROM_SOURCE_FILE, 0);
		this.slocCounterMap.put(SLOCType.LOGIC_CODE_LINES_FROM_AST, 0);
		this.slocCounterMap.put(SLOCType.COMMENT_LINES_FROM_AST, 0);
		this.slocCounterMap.put(SLOCType.PHYSICAL_CODE_LINES_FROM_AST, 0);
	}

	public enum ClassModifier {
		CLASS("class"),
		ENUM("enum"),
		ANNOTATION("annotation"),
		ABSTRACT_CLASS("abstract"),
		INTERFACE("interface"),
		NONE("*-info.java"),
		UNIDENTIFIED("unidentified");

		String str;

		ClassModifier(String str) {
			this.str = str;
		}

		@Override
		public String toString() {
			return str;
		}
	}

	/**
	 * Source File considers a java file as the minimum unit.
	 * AST considers a ClassInfo object as the minimum unit，excluding the keywords: package and import,
	 * and will format the source code (statements will be separated into different lines).
	 * <p>
	 * Logic code lines contains will exclude empty lines, symbol-only lines and comments lines.
	 * Physical code lines will include symbol-only lines and logic code lines.
	 * Comment lines will include the mixed lines, likes: code;//comment
	 */
	public enum SLOCType {
		LOGIC_CODE_LINES_FROM_SOURCE_FILE,
		PHYSICAL_CODE_LINES_FROM_SOURCE_FILE,
		COMMENT_LINES_FROM_SOURCE_FILE,
		LOGIC_CODE_LINES_FROM_AST,
		PHYSICAL_CODE_LINES_FROM_AST,
		COMMENT_LINES_FROM_AST
	}

	public enum ClassDeclaration {
		PUBLIC_OUTER_CLASS("public outer class"), // the only public outer class for a java file.
		NON_PUBLIC_OUTER_CLASS("package private outer class"), // package private class
		NESTED_CLASS("nested class"); // nested class, including static nested class and inner class

		String str;

		ClassDeclaration(String str) {
			this.str = str;
		}

		@Override
		public String toString() {
			return str;
		}
	}

	@Override
	public String toString() {
		return "ClassInfo{" + "fullyQualifiedName='" + fullyQualifiedName + '\'' + '}';
	}
}