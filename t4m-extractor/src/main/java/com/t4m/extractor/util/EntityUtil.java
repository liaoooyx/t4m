package com.t4m.extractor.util;

import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.ModuleInfo;
import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.exception.DuplicatedInnerClassFoundedException;

import java.util.*;

/**
 * Created by Yuxiang Liao on 2020-06-21 15:52.
 */
public class EntityUtil {

	/**
	 * 在列表中查找指定的全限定类名
	 */
	public static ClassInfo getClassByQualifiedName(
			List<ClassInfo> classInfoList, String fullyQualifiedClassName) {
		Optional<ClassInfo> optProjectInfo = classInfoList.stream().filter(
				classInfo -> fullyQualifiedClassName.equals(classInfo.getFullyQualifiedName())).findFirst();
		return optProjectInfo.orElse(null);
	}

	/**
	 * 在列表中查找指定的类名
	 */
	public static ClassInfo getClassByShortName(List<ClassInfo> classInfoList, String shortName) {
		Optional<ClassInfo> target = classInfoList.stream().filter(
				classInfo -> shortName.equals(classInfo.getShortName())).findFirst();
		return target.orElse(null);
	}

	/**
	 * 在无法判断传入的类名是外部类还是内部类的情况下，调用该方法。该方法优先查找列表中的外部类，如果不存在则查找列表中的类的持有的内部类列表。
	 *
	 * @param classInfoList 储存着外部类的列表
	 * @param rawShortName 原始类名。即Class或InnerClass（不是Class$InnerClass）
	 * @throws DuplicatedInnerClassFoundedException 如果指定的列表中包含的类的内部类的类名重复，那么会抛出异常。
	 */
	public static ClassInfo getClassOrInnerClassFromOuterClassListByRawShortName(
			List<ClassInfo> classInfoList, String rawShortName) throws DuplicatedInnerClassFoundedException {
		ClassInfo target = null;
		target = getClassByShortName(classInfoList, rawShortName);
		if (target == null) {
			target = getInnerClassFromOuterClassListByShortName(classInfoList, rawShortName);
		}
		return target;
	}

	/**
	 * 根据类名，查找指定列表中的类的内部类。
	 *
	 * @param classInfoList 储存着外部类的列表
	 * @param innerClassRawShortName 内部类的类目。类名有两种格式：原始类名比如：InnerClass；相对的是转化后的类名：Class$InnerClass。
	 * @throws DuplicatedInnerClassFoundedException 如果指定的列表中的类的内部类的类名重复，那么会抛出异常。
	 */
	public static ClassInfo getInnerClassFromOuterClassListByShortName(
			List<ClassInfo> classInfoList, String innerClassRawShortName) throws DuplicatedInnerClassFoundedException {
		ClassInfo target = null;
		List<ClassInfo> multipleInnerClassList = new ArrayList<>();
		for (ClassInfo outerClassInfo : classInfoList) {
			for (ClassInfo innerClassInfo : outerClassInfo.getInnerClassList()) {
				if (innerClassRawShortName.equals(innerClassInfo.getShortName().split("\\$")[1])) {
					target = innerClassInfo;
					multipleInnerClassList.add(target);
					break;
				}
			}
		}
		if (multipleInnerClassList.size() > 1) {
			String msg = multipleInnerClassList.toString();
			throw new DuplicatedInnerClassFoundedException(msg);
		}
		return target;
	}

	/**
	 * 在列表中查找指定的模块名
	 */
	public static ModuleInfo getModuleByShortName(List<ModuleInfo> moduleInfoList, String shortName) {
		Optional<ModuleInfo> target = moduleInfoList.stream().filter(
				moduleInfo -> shortName.equals(moduleInfo.getShortName())).findFirst();
		return target.orElse(null);
	}

	/**
	 * 在列表中查找指定的模块名
	 */
	public static ModuleInfo getModuleByRelativeName(List<ModuleInfo> moduleInfoList, String relativeName) {
		Optional<ModuleInfo> target = moduleInfoList.stream().filter(
				moduleInfo -> relativeName.equals(moduleInfo.getRelativePath())).findFirst();
		return target.orElse(null);
	}

	/**
	 * 在列表中查找指定的全限定包名
	 */
	public static PackageInfo getPackageByQualifiedName(List<PackageInfo> packageInfoList, String qualifiedName) {
		Optional<PackageInfo> target = packageInfoList.stream().filter(
				packageInfo -> qualifiedName.equals(packageInfo.getFullyQualifiedName())).findFirst();
		return target.orElse(null);
	}

}
