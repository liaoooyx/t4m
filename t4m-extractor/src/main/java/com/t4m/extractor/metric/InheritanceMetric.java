package com.t4m.extractor.metric;

import com.t4m.extractor.entity.ClassInfo;

/**
 * Created by Yuxiang Liao on 2020-07-17 03:49.
 */
public class InheritanceMetric {


	private static int countInheritanceDeep(ClassInfo classInfo) {
		if (classInfo.getExtendsClassList().isEmpty()) {
			return 1;
		}
		int maxDeepLevel = 0;
		for (ClassInfo extendsClass : classInfo.getExtendsClassList()) {
			int currentDeepLevel = countInheritanceDeep(extendsClass) + 1;
			maxDeepLevel = Math.max(maxDeepLevel, currentDeepLevel);
		}
		return maxDeepLevel;
	}

	/**
	 * 一个类的父类可以向上追溯的数量，也就是在继承树中，一个类到根类经过了多少次继承。
	 * 一个类的直接子类的数量
	 */
	public static void calculateInheritance(ClassInfo classInfo){
		classInfo.setDeepOfInheritanceTree(countInheritanceDeep(classInfo));
		classInfo.setNumberOfChildren(classInfo.getImmediateSubClassList().size());
	}
}
