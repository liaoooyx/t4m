package com.t4m.extractor.metric;

import com.t4m.extractor.entity.ClassInfo;

/**
 * Created by Yuxiang Liao on 2020-07-17 03:49.
 */
public class InheritanceMetric implements ClassLevelMetric {


	private int countInheritanceDeep(ClassInfo classInfo) {
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

	@Override
	public void calculate(ClassInfo classInfo) {
		classInfo.setDeepOfInheritanceTree(countInheritanceDeep(classInfo));
		classInfo.setNumberOfChildren(classInfo.getImmediateSubClassList().size());
	}
}
