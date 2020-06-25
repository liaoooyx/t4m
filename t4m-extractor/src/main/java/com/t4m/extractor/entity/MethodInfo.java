package com.t4m.extractor.entity;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * Created by Yuxiang Liao on 2020-06-09 22:56.
 */
public class MethodInfo implements Serializable {

	private static final long serialVersionUID = -8167843312218383438L;

	private String shortName;
	private String fullyQualifiedName; // fully-qualified class name

	private ClassInfo belongsToClass;

	// a set of instance variables in class used by this method.
	// referring to ClassInfo.instanceSet.
	private Set<String> instancesUsedSet;



}
