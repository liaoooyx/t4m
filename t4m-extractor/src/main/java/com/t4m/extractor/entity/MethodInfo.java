package com.t4m.extractor.entity;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * Created by Yuxiang Liao on 2020-06-09 22:56.
 */
public class MethodInfo {

	private String methodName;
	private String fullMethodName; // fully-qualified class name

	private ClassInfo belongsToClass;

	// a set of instance variables in class used by this method.
	// referring to ClassInfo.instanceSet.
	private Set<String> instancesUsedSet;

}
