package com.t4m.extractor.entity;

import com.github.javaparser.Range;

import java.io.Serializable;
import java.util.*;

/**
 * Created by Yuxiang Liao on 2020-07-07 16:35.
 */
public class FieldInfo implements Serializable {
	private static final long serialVersionUID = -8268736331184263167L;

	private String shortName;
	private transient Range rangeLocator; //理论上用shortName就足够定位一个类的字段，此字段留作备用

	private String typeString;
	private List<ClassInfo> typeAsClassInfoList = new ArrayList<>(); // 该字段涉及到的所有全限定类名（只包括跟项目有关的类）

	private boolean isStatic = false;
	private boolean isFinal = false;
	private AccessModifierEnum accessModifierEnum = AccessModifierEnum.DEFAULT;

	private Set<MethodInfo> beingPassingAccessedByLocalMethodSet = new HashSet<>(); // 所有直接访问和间接访问
	private Set<MethodInfo> beingAccessedDirectlyByLocalMethodSet = new HashSet<>();
	private Set<MethodInfo> cohesionMethodSet = new HashSet<>(); // 在调用树中最终指向该字段的方法


	public FieldInfo(String shortName, String typeString) {
		this.shortName = shortName;
		this.typeString = typeString;
	}

	@Override
	public String toString() {
		return "FieldInfo{" + "shortName='" + shortName + '\'' + ", rangeLocator=" + rangeLocator + ", typeString='" +
				typeString + '\'' + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		FieldInfo fieldInfo = (FieldInfo) o;
		return Objects.equals(shortName, fieldInfo.shortName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(shortName);
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public Range getRangeLocator() {
		return rangeLocator;
	}

	public void setRangeLocator(Range rangeLocator) {
		this.rangeLocator = rangeLocator;
	}

	public String getTypeString() {
		return typeString;
	}

	public void setTypeString(String typeString) {
		this.typeString = typeString;
	}

	public List<ClassInfo> getTypeAsClassInfoList() {
		return typeAsClassInfoList;
	}

	public void setTypeAsClassInfoList(
			List<ClassInfo> typeAsClassInfoList) {
		this.typeAsClassInfoList = typeAsClassInfoList;
	}

	public boolean isStatic() {
		return isStatic;
	}

	public void setStatic(boolean aStatic) {
		isStatic = aStatic;
	}

	public boolean isFinal() {
		return isFinal;
	}

	public void setFinal(boolean aFinal) {
		isFinal = aFinal;
	}

	public AccessModifierEnum getAccessModifierEnum() {
		return accessModifierEnum;
	}

	public void setAccessModifierEnum(AccessModifierEnum accessModifierEnum) {
		this.accessModifierEnum = accessModifierEnum;
	}

	public Set<MethodInfo> getBeingPassingAccessedByLocalMethodSet() {
		return beingPassingAccessedByLocalMethodSet;
	}

	public void setBeingPassingAccessedByLocalMethodSet(
			Set<MethodInfo> beingPassingAccessedByLocalMethodSet) {
		this.beingPassingAccessedByLocalMethodSet = beingPassingAccessedByLocalMethodSet;
	}

	public Set<MethodInfo> getBeingAccessedDirectlyByLocalMethodSet() {
		return beingAccessedDirectlyByLocalMethodSet;
	}

	public void setBeingAccessedDirectlyByLocalMethodSet(
			Set<MethodInfo> beingAccessedDirectlyByLocalMethodSet) {
		this.beingAccessedDirectlyByLocalMethodSet = beingAccessedDirectlyByLocalMethodSet;
	}

	public Set<MethodInfo> getCohesionMethodSet() {
		return cohesionMethodSet;
	}

	public void setCohesionMethodSet(Set<MethodInfo> cohesionMethodSet) {
		this.cohesionMethodSet = cohesionMethodSet;
	}
}
