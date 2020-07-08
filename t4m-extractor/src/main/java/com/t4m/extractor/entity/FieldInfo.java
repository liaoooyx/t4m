package com.t4m.extractor.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Yuxiang Liao on 2020-07-07 16:35.
 */
public class FieldInfo implements Serializable {
	private static final long serialVersionUID = -8268736331184263167L;

	private String shortName;

	private String typeString;
	private List<ClassInfo> typeAsClassInfoList = new ArrayList<>(); // 该字段涉及到的所有全限定类名（只包括跟项目有关的类）

	private boolean isStatic = false;
	private boolean isFinal = false;
	private AccessModifierEnum accessModifierEnum = AccessModifierEnum.DEFAULT;

	public FieldInfo(String shortName, String typeString) {
		this.shortName = shortName;
		this.typeString = typeString;
	}

	@Override
	public String toString() {
		return "FieldInfo{" + "shortName='" + shortName + '\'' + ", typeString='" + typeString + '\'' + '}';
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
}
