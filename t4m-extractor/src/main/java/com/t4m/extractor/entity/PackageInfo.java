package com.t4m.extractor.entity;

import java.util.Map;
import java.util.Set;

/**
 * Created by Yuxiang Liao on 2020-06-09 22:55.
 */
public class PackageInfo {
	private PackageInfo parentPackage;
	private Set<PackageInfo> childrenPackage;
	private Set<ClassInfo> classSet; // direct classes

	// TODO 需要一个类或方法专门负责根据Class的关系进行统计：
	//  在扫描目标项目时，用一个List来记录依赖链，然后使用一个专门的方法解析该List，并将关系添加到对象中
	private Map<PackageInfo,Integer> dependsOn;
	private Map<PackageInfo,Integer> dependedBy;

	public boolean isRootPackage() {
		// TODO 如果parentPackage为空，则为根包
		return true;
	}
}
