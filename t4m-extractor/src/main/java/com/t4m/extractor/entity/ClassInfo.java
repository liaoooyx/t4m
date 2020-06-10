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

	private PackageInfo belongsToPackage;

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

}
