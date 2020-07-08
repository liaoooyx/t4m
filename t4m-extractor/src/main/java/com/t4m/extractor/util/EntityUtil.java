package com.t4m.extractor.util;

import com.t4m.extractor.entity.*;
import com.t4m.extractor.exception.DuplicatedInnerClassFoundedException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Yuxiang Liao on 2020-06-21 15:52.
 */
public class EntityUtil {

	/**
	 * 在列表中查找指定的字段名
	 */
	public static FieldInfo getFieldByShortName(List<FieldInfo> fieldInfoList, String shortName) {
		Optional<FieldInfo> target = fieldInfoList.stream().filter(
				fieldInfo -> shortName.equals(fieldInfo.getShortName())).findFirst();
		return target.orElse(null);
	}

	/**
	 * 在列表中查找指定的方法名
	 */
	public static List<MethodInfo> getMethodByShortName(List<MethodInfo> methodInfoList, String shortName) {
		return methodInfoList.stream().filter(fieldInfo -> shortName.equals(fieldInfo.getShortName())).collect(
				Collectors.toList());
	}

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
	 * @param rawShortName 原始类名。即Class或InnerClass或Class$InnerClass）
	 * @throws DuplicatedInnerClassFoundedException 如果指定的列表中包含的类的内部类的类名重复，那么会抛出异常。
	 */
	public static ClassInfo getClassOrNestedClassFromOuterClassListByShortName(
			List<ClassInfo> classInfoList, String rawShortName) throws DuplicatedInnerClassFoundedException {
		ClassInfo target = null;
		target = getClassByShortName(classInfoList, rawShortName);
		if (target == null) {
			target = getNestedClassFromOuterClassListByShortName(classInfoList, rawShortName);
		}
		return target;
	}

	/**
	 * 根据类名，查找指定列表中的类的内部类。
	 *
	 * @param classInfoList 储存着外部类的列表
	 * @param innerClassShortName 内部类的类名: InnerClass或Class$InnerClass
	 * @throws DuplicatedInnerClassFoundedException 如果指定的列表中的类的内部类的类名重复，那么会抛出异常。
	 */
	public static ClassInfo getNestedClassFromOuterClassListByShortName(
			List<ClassInfo> classInfoList, String innerClassShortName) throws DuplicatedInnerClassFoundedException {
		ClassInfo target = null;
		List<ClassInfo> multipleInnerClassList = new ArrayList<>();
		for (ClassInfo outerClassInfo : classInfoList) {
			for (ClassInfo innerClassInfo : outerClassInfo.getInnerClassList()) {
				if (innerClassShortName.equals(innerClassInfo.getShortName().split("\\$")[1]) ||
						innerClassShortName.equals(innerClassInfo.getShortName())) {
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
	 * 在无法判断传入的类名是外部类还是内部类的情况下，调用该方法。该方法优先查找列表中的外部类，如果不存在则查找列表中的类的持有的内部类列表。
	 *
	 * @param classInfoList 储存着外部类的列表
	 * @param qualifiedName 全限定类名。即com.a.Class或com.a.Class$InnerClass
	 * @throws DuplicatedInnerClassFoundedException 如果指定的列表中包含的类的内部类的类名重复，那么会抛出异常。
	 */
	public static ClassInfo getClassOrNestedClassFromOuterClassListByQualifiedName(
			List<ClassInfo> classInfoList, String qualifiedName) throws DuplicatedInnerClassFoundedException {
		ClassInfo target = null;
		target = getClassByQualifiedName(classInfoList, qualifiedName);
		if (target == null) {
			target = getNestedClassFromOuterClassListByQualifiedName(classInfoList, qualifiedName);
		}
		return target;
	}

	/**
	 * 根据类名，查找指定列表中的类的内部类。
	 *
	 * @param classInfoList 储存着外部类的列表
	 * @param nestedClassQualifiedName 内部类的全限定类名。即com.a.Class或com.a.Class$InnerClass
	 * @throws DuplicatedInnerClassFoundedException 如果指定的列表中的类的内部类的类名重复，那么会抛出异常。
	 */
	public static ClassInfo getNestedClassFromOuterClassListByQualifiedName(
			List<ClassInfo> classInfoList, String nestedClassQualifiedName) throws
	                                                                        DuplicatedInnerClassFoundedException {
		ClassInfo target = null;
		List<ClassInfo> multipleInnerClassList = new ArrayList<>();
		for (ClassInfo outerClassInfo : classInfoList) {
			for (ClassInfo innerClassInfo : outerClassInfo.getInnerClassList()) {
				if (nestedClassQualifiedName.equals(innerClassInfo.getFullyQualifiedName())) {
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

	/**
	 * 避免添加重复元素，参数类需要重写{@code equals()}和{@code hashCode()}方法。 如果对象不存在列表中，则添加并返回该对象；如果对象已存在，则从列表中获取并返回该对象。
	 */
	public static <T> T safeAddEntityToList(T entity, List<T> targetList) {
		int index;
		if ((index = targetList.indexOf(entity)) == -1) {
			targetList.add(entity);
			return entity;
		} else {
			return targetList.get(index);
		}
	}

}
