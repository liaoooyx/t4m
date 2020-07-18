package com.t4m.extractor.entity;

import com.github.javaparser.Range;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Yuxiang Liao on 2020-06-09 22:56.
 */
public class MethodInfo implements Serializable {

	private static final long serialVersionUID = -8167843312218383438L;

	private String shortName;
	private String fullyQualifiedName; // fully-qualified class name: pkg.b.c.Class.method
	private transient Range rangeLocator;
	private String methodDeclarationString;

	private ClassInfo classInfo;

	private String returnTypeString = ""; // 空字符串表示无返回（即构造器）
	private List<ClassInfo> returnTypeAsClassInfoList = new ArrayList<>();

	private Map<String, String> paramsNameTypeMap = new LinkedHashMap<>(); // key为参数名，value为参数类型字符串
	private Map<String, List<ClassInfo>> paramsTypeAsClassInfoListMap = new LinkedHashMap<>();

	private List<String> thrownExceptionStringList = new ArrayList<>(); // 异常类型字符串
	private List<ClassInfo> thrownExceptionClassList = new ArrayList<>();

	private boolean abstractMethod = false;
	private boolean staticMethod = false;
	private AccessModifierEnum accessModifierEnum = AccessModifierEnum.DEFAULT;

	// LOCM4度量
	private Set<FieldInfo> fieldAccessSet = new HashSet<>(); // 该方法内涉及的本地字段访问
	private Set<MethodInfo> localMethodAccessSet = new HashSet<>(); // 该方法内涉及的本地方法访问
	private Set<MethodInfo> beingAccessedByLocalMethodSet = new HashSet<>(); // 被哪些本地方法访问

	//if、while、for、&&、||、cases and default of switch, catches of try
	private int cyclomaticComplexity;

	public MethodInfo(String shortName) {
		this.shortName = shortName;
	}

	@Override
	public String toString() {
		return "MethodInfo{" + "rangeLocator=" + rangeLocator + ", methodDeclarationString='" +
				methodDeclarationString + '\'' + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		MethodInfo that = (MethodInfo) o;
		return Objects.equals(fullyQualifiedName, that.fullyQualifiedName) && Objects.equals(rangeLocator,
		                                                                                     that.rangeLocator);
	}

	@Override
	public int hashCode() {
		return Objects.hash(fullyQualifiedName, rangeLocator);
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

	public Range getRangeLocator() {
		return rangeLocator;
	}

	public void setRangeLocator(Range rangeLocator) {
		this.rangeLocator = rangeLocator;
	}

	public String getMethodDeclarationString() {
		return methodDeclarationString;
	}

	public void setMethodDeclarationString(String methodDeclarationString) {
		this.methodDeclarationString = methodDeclarationString;
	}

	public ClassInfo getClassInfo() {
		return classInfo;
	}

	public void setClassInfo(ClassInfo classInfo) {
		this.classInfo = classInfo;
	}

	public String getReturnTypeString() {
		return returnTypeString;
	}

	public void setReturnTypeString(String returnTypeString) {
		this.returnTypeString = returnTypeString;
	}

	public List<ClassInfo> getReturnTypeAsClassInfoList() {
		return returnTypeAsClassInfoList;
	}

	public void setReturnTypeAsClassInfoList(List<ClassInfo> returnTypeAsClassInfoList) {
		this.returnTypeAsClassInfoList = returnTypeAsClassInfoList;
	}

	public Map<String, String> getParamsNameTypeMap() {
		return paramsNameTypeMap;
	}

	public void setParamsNameTypeMap(Map<String, String> paramsNameTypeMap) {
		this.paramsNameTypeMap = paramsNameTypeMap;
	}

	public Map<String, List<ClassInfo>> getParamsTypeAsClassInfoListMap() {
		return paramsTypeAsClassInfoListMap;
	}

	public void setParamsTypeAsClassInfoListMap(
			Map<String, List<ClassInfo>> paramsTypeAsClassInfoListMap) {
		this.paramsTypeAsClassInfoListMap = paramsTypeAsClassInfoListMap;
	}

	public List<String> getThrownExceptionStringList() {
		return thrownExceptionStringList;
	}

	public void setThrownExceptionStringList(List<String> thrownExceptionStringList) {
		this.thrownExceptionStringList = thrownExceptionStringList;
	}

	public List<ClassInfo> getThrownExceptionClassList() {
		return thrownExceptionClassList;
	}

	public void setThrownExceptionClassList(List<ClassInfo> thrownExceptionClassList) {
		this.thrownExceptionClassList = thrownExceptionClassList;
	}

	public boolean isAbstractMethod() {
		return abstractMethod;
	}

	public void setAbstractMethod(boolean abstractMethod) {
		this.abstractMethod = abstractMethod;
	}

	public boolean isStaticMethod() {
		return staticMethod;
	}

	public void setStaticMethod(boolean staticMethod) {
		this.staticMethod = staticMethod;
	}

	public AccessModifierEnum getAccessModifierEnum() {
		return accessModifierEnum;
	}

	public void setAccessModifierEnum(AccessModifierEnum accessModifierEnum) {
		this.accessModifierEnum = accessModifierEnum;
	}

	public Set<FieldInfo> getFieldAccessSet() {
		return fieldAccessSet;
	}

	public void setFieldAccessSet(Set<FieldInfo> fieldAccessSet) {
		this.fieldAccessSet = fieldAccessSet;
	}

	public Set<MethodInfo> getLocalMethodAccessSet() {
		return localMethodAccessSet;
	}

	public void setLocalMethodAccessSet(Set<MethodInfo> localMethodAccessSet) {
		this.localMethodAccessSet = localMethodAccessSet;
	}

	public Set<MethodInfo> getBeingAccessedByLocalMethodSet() {
		return beingAccessedByLocalMethodSet;
	}

	public void setBeingAccessedByLocalMethodSet(
			Set<MethodInfo> beingAccessedByLocalMethodSet) {
		this.beingAccessedByLocalMethodSet = beingAccessedByLocalMethodSet;
	}

	public int getCyclomaticComplexity() {
		return cyclomaticComplexity;
	}

	public void setCyclomaticComplexity(int cyclomaticComplexity) {
		this.cyclomaticComplexity = cyclomaticComplexity;
	}

	/**
	 * 将paramsTypeAsClassInfoListMap中每个val都是一个list，该方法将之合并为一个list
	 */
	public List<ClassInfo> getParamsTypeAsClassInfoList() {
		return paramsTypeAsClassInfoListMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
	}
}
