package com.t4m.extractor.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yuxiang Liao on 2020-07-07 16:35.
 */
public class FieldInfo implements Serializable {
	private static final long serialVersionUID = -8268736331184263167L;

	private String shortName;
	private String typeString;

	private List<ClassInfo> typeToClassInfoList = new ArrayList<>(); // 该字段涉及到的所有全限定类名（只包括跟项目有关的类）

	private boolean isStatic = false;

	public FieldInfo(String shortName, String typeString) {
		this.shortName = shortName;
		this.typeString = typeString;
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
		return typeToClassInfoList;
	}

	public void setTypeToClassInfoList(
			List<ClassInfo> typeToClassInfoList) {
		this.typeToClassInfoList = typeToClassInfoList;
	}

	public boolean isStatic() {
		return isStatic;
	}

	public void setStatic(boolean aStatic) {
		isStatic = aStatic;
	}

}
