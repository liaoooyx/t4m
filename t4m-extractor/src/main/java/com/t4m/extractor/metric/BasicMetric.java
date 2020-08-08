package com.t4m.extractor.metric;

import com.t4m.extractor.entity.ModuleInfo;
import com.t4m.extractor.entity.PackageInfo;

/**
 * Created by Yuxiang Liao on 2020-07-18 03:55.
 */
public class BasicMetric implements PackageLevelMetric, ModuleLevelMetric {

	@Override
	public void calculate(PackageInfo packageInfo) {
		packageInfo.setNumberOfJavaFile(packageInfo.getClassList().size());
		packageInfo.setNumberOfAllClass(packageInfo.getAllClassList().size());
	}

	@Override
	public void calculate(ModuleInfo moduleInfo) {
		int numOfJavaFile = 0;
		int numOfAllClass = 0;
		for (PackageInfo packageInfo : moduleInfo.getPackageList()) {
			numOfJavaFile += packageInfo.getNumberOfJavaFile();
			numOfAllClass += packageInfo.getNumberOfAllClass();
		}
		moduleInfo.setNumberOfJavaFile(numOfJavaFile);
		moduleInfo.setNumberOfAllClass(numOfAllClass);
	}
}
