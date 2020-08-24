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
	private transient Range rangeLocator; // Reserved field

	private String typeString;
	private List<ClassInfo> typeAsClassInfoList = new ArrayList<>(); // All relevant ClassInfo objects of this field.

	private boolean isStatic = false;
	private boolean isFinal = false;
	private boolean isTransient = false;
	private boolean isAbstract = false;
	private boolean isSynchronized = false;
	private boolean isStrictfp = false;
	private boolean isNative = false;
	private AccessModifierEnum accessModifierEnum = AccessModifierEnum.DEFAULT;

	// All direct access or indirect access
	private Set<MethodInfo> beingPassingAccessedByLocalMethodSet = new HashSet<>();
	private Set<MethodInfo> beingAccessedDirectlyByLocalMethodSet = new HashSet<>();
	// All of the methods that access to this field through the Invocation Tree.
	private Set<MethodInfo> cohesionMethodSet = new HashSet<>();


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

	public boolean isTransient() {
		return isTransient;
	}

	public void setTransient(boolean aTransient) {
		isTransient = aTransient;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public void setAbstract(boolean anAbstract) {
		isAbstract = anAbstract;
	}

	public boolean isSynchronized() {
		return isSynchronized;
	}

	public void setSynchronized(boolean aSynchronized) {
		isSynchronized = aSynchronized;
	}

	public boolean isStrictfp() {
		return isStrictfp;
	}

	public void setStrictfp(boolean aStrictfp) {
		isStrictfp = aStrictfp;
	}

	public boolean isNative() {
		return isNative;
	}

	public void setNative(boolean aNative) {
		isNative = aNative;
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

	public boolean isInstanceVariable() {
		return !isAbstract && !isSynchronized && !isStrictfp && !isStatic && !isNative;
	}
}
