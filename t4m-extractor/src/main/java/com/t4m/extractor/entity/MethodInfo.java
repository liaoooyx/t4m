package com.t4m.extractor.entity;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Yuxiang Liao on 2020-06-09 22:56.
 */
public class MethodInfo implements Serializable {

	private static final long serialVersionUID = -8167843312218383438L;

	private String shortName;
	private String fullyQualifiedName; // fully-qualified class name: pkga.b.c#method

	private String returnTypeString = ""; // 空字符串表示无返回（即构造器）
	private List<ClassInfo> returnTypeAsClassInfoList = new ArrayList<>();

	private Map<String, String> paramsNameTypeMap = new LinkedHashMap<>(); // key为参数名，value为参数类型字符串
	private Map<String, List<ClassInfo>> paramsTypeAsClassInfoListMap = new LinkedHashMap<>();

	private boolean abstractMethod = false;
	private boolean staticMethod = false;
	private AccessModifierEnum accessModifierEnum = AccessModifierEnum.DEFAULT;

	public MethodInfo(String shortName) {
		this.shortName = shortName;
	}

	@Override
	public String toString() {
		return "MethodInfo{" + "shortName='" + shortName + '\'' + ", returnTypeString='" + returnTypeString + '\'' +
				", paramsNameTypeMap=" + paramsNameTypeMap + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		MethodInfo that = (MethodInfo) o;
		return Objects.equals(shortName, that.shortName) && Objects.equals(returnTypeString, that.returnTypeString) &&
				Objects.equals(paramsNameTypeMap, that.paramsNameTypeMap);
	}

	@Override
	public int hashCode() {
		return Objects.hash(shortName, returnTypeString, paramsNameTypeMap);
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

	/**
	 * 将paramsTypeAsClassInfoListMap中每个val都是一个list，该方法将之合并为一个list
	 */
	public List<ClassInfo> getParamsTypeAsClassInfoList() {
		return paramsTypeAsClassInfoListMap.values().stream().flatMap(Collection::stream).collect(
				Collectors.toList());
	}
}
