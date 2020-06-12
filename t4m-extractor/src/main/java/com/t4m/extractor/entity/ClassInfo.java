package com.t4m.extractor.entity;

import java.util.List;
import java.util.Set;

/**
 * Created by Yuxiang Liao at 2020-06-09 22:55.
 */
public class ClassInfo {

	private String className;
	private String fullClassName; // fully-qualified class name
	private ClassType classType;

	private PackageInfo packageInfo;
	private String packageFullName = "(null)"; // default to be (null) if a class don't have package.

	private String absolutePath;

	private ClassInfo hasAbstractClass;
	private Set<ClassInfo> hasInterfaceClass;

	private List<ClassInfo> dependsOn;
	private List<ClassInfo> dependedBy;

	private Set<MethodInfo> methodSet;

	// instance variables in class, used by MethodInfo
	// Format: instancName:instanceType
	// such as "instanceSet:Set"
	private Set<String> instanceSet;

	private int numberOfMethods;
	private int numberOfInstances;

	private int sourceLinesOfCode;
	private int blankLines;
	private int effectiveLinesOfCode;
	private int commentLinesOfCode;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getFullClassName() {
		return fullClassName;
	}

	public void setFullClassName(String fullClassName) {
		this.fullClassName = fullClassName;
	}

	public ClassType getClassType() {
		return classType;
	}

	public void setClassType(ClassType classType) {
		this.classType = classType;
	}

	public PackageInfo getPackageInfo() {
		return packageInfo;
	}

	public void setPackageInfo(PackageInfo packageInfo) {
		this.packageInfo = packageInfo;
	}

	public String getPackageFullName() {
		return packageFullName;
	}

	public void setPackageFullName(String packageFullName) {
		this.packageFullName = packageFullName;
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	public ClassInfo getHasAbstractClass() {
		return hasAbstractClass;
	}

	public void setHasAbstractClass(ClassInfo hasAbstractClass) {
		this.hasAbstractClass = hasAbstractClass;
	}

	public Set<ClassInfo> getHasInterfaceClass() {
		return hasInterfaceClass;
	}

	public void setHasInterfaceClass(Set<ClassInfo> hasInterfaceClass) {
		this.hasInterfaceClass = hasInterfaceClass;
	}

	public List<ClassInfo> getDependsOn() {
		return dependsOn;
	}

	public void setDependsOn(List<ClassInfo> dependsOn) {
		this.dependsOn = dependsOn;
	}

	public List<ClassInfo> getDependedBy() {
		return dependedBy;
	}

	public void setDependedBy(List<ClassInfo> dependedBy) {
		this.dependedBy = dependedBy;
	}

	public Set<MethodInfo> getMethodSet() {
		return methodSet;
	}

	public void setMethodSet(Set<MethodInfo> methodSet) {
		this.methodSet = methodSet;
	}

	public Set<String> getInstanceSet() {
		return instanceSet;
	}

	public void setInstanceSet(Set<String> instanceSet) {
		this.instanceSet = instanceSet;
	}

	public int getNumberOfMethods() {
		return numberOfMethods;
	}

	public void setNumberOfMethods(int numberOfMethods) {
		this.numberOfMethods = numberOfMethods;
	}

	public int getNumberOfInstances() {
		return numberOfInstances;
	}

	public void setNumberOfInstances(int numberOfInstances) {
		this.numberOfInstances = numberOfInstances;
	}

	public int getSourceLinesOfCode() {
		return sourceLinesOfCode;
	}

	public void setSourceLinesOfCode(int sourceLinesOfCode) {
		this.sourceLinesOfCode = sourceLinesOfCode;
	}

	public int getBlankLines() {
		return blankLines;
	}

	public void setBlankLines(int blankLines) {
		this.blankLines = blankLines;
	}

	public int getEffectiveLinesOfCode() {
		return effectiveLinesOfCode;
	}

	public void setEffectiveLinesOfCode(int effectiveLinesOfCode) {
		this.effectiveLinesOfCode = effectiveLinesOfCode;
	}

	public int getCommentLinesOfCode() {
		return commentLinesOfCode;
	}

	public void setCommentLinesOfCode(int commentLinesOfCode) {
		this.commentLinesOfCode = commentLinesOfCode;
	}
}
