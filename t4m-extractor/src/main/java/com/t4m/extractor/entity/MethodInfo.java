package com.t4m.extractor.entity;

import com.t4m.extractor.util.EntityUtil;

import java.awt.*;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Created by Yuxiang Liao on 2020-06-09 22:56.
 */
public class MethodInfo implements Serializable {

	private static final long serialVersionUID = -8167843312218383438L;

	private String shortName;
	private String fullyQualifiedName; // fully-qualified class name: pkga.b.c#method

	private String returnTypeQualifiedName = ""; // 返回类型的全限定类名，空字符串表示void
	private List<String> paramsQualifiedNameList = new ArrayList<>(); // 参数的全限定类名列表，空列表表示无参数

	private boolean abstractMethod = false;
	private boolean staticMethod = false;

	// 方法列表只存在于ClassInfo下，因此只用shortName即可

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		MethodInfo that = (MethodInfo) o;
		return Objects.equals(shortName, that.shortName) && Objects.equals(returnTypeQualifiedName,
		                                                                   that.returnTypeQualifiedName) &&
				Objects.equals(paramsQualifiedNameList, that.paramsQualifiedNameList);
	}

	@Override
	public int hashCode() {
		return Objects.hash(shortName, returnTypeQualifiedName, paramsQualifiedNameList);
	}

	private ClassInfo classInfo;

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

	public String getReturnTypeQualifiedName() {
		return returnTypeQualifiedName;
	}

	public void setReturnTypeQualifiedName(String returnTypeQualifiedName) {
		this.returnTypeQualifiedName = returnTypeQualifiedName;
	}

	public List<String> getParamsQualifiedNameList() {
		return paramsQualifiedNameList;
	}

	public void setParamsQualifiedNameList(List<String> paramsQualifiedNameList) {
		this.paramsQualifiedNameList = paramsQualifiedNameList;
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

	public ClassInfo getClassInfo() {
		return classInfo;
	}

	public void setClassInfo(ClassInfo classInfo) {
		this.classInfo = classInfo;
	}

	/**
	 * 返回returnTypeQualifiedName对应的ClassInfo，如果不存在，则返回Null
	 */
	public ClassInfo resolveReturnTypeToClassInfo(List<ClassInfo> classInfoList) {
		return EntityUtil.getClassByQualifiedName(classInfoList, this.returnTypeQualifiedName);
	}

	/**
	 * 遍历列表paramsQualifiedNameList，将入参的全限定类型名解析为ClassInfo，如果不存在，则跳过并解析下一条，如果都不存在，则返回空列表
	 */
	public List<ClassInfo> resolveParamsListToClassInfoList(List<ClassInfo> classInfoList) {
		List<ClassInfo> paramsLsit = new ArrayList<>();
		for (String paramName : paramsQualifiedNameList) {
			ClassInfo classInfo = EntityUtil.getClassByQualifiedName(classInfoList, paramName);
			if (classInfo != null) {
				paramsLsit.add(classInfo);
			}
		}
		return paramsLsit;
	}

}
