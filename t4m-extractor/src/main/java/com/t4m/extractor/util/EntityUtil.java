package com.t4m.extractor.util;

import com.github.javaparser.Range;
import com.t4m.extractor.entity.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
		return methodInfoList.stream().filter(methodInfo -> shortName.equals(methodInfo.getShortName())).collect(
				Collectors.toList());
	}

	/**
	 * 在列表中查找指定的方法名
	 */
	public static MethodInfo getMethodByQualifiedNameAndRangeLocator(
			List<MethodInfo> methodInfoList, String qualifiedName, Range rangeLocator) {
		return methodInfoList.stream().filter(methodInfo -> qualifiedName.equals(methodInfo.getFullyQualifiedName()) &&
				rangeLocator.equals(methodInfo.getRangeLocator())).findFirst().orElse(null);
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
	 * 根据包绝对路径获取对象，返回获得的第一个对象，如果不存在则返回{@code null}.
	 */
	public static PackageInfo getPackageInfoByAbsolutePath(List<PackageInfo> packageInfoList, String absolotePath) {
		Optional<PackageInfo> target = packageInfoList.stream().filter(
				packageInfo -> absolotePath.equals(packageInfo.getAbsolutePath())).findFirst();
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

	/**
	 * 为两边都添加依赖关系
	 */
	public static void addDependency(ClassInfo current, ClassInfo target) {
		if (!Objects.equals(current, target)) {
			EntityUtil.safeAddEntityToList(target, current.getActiveDependencyAkaFanOutList());
			EntityUtil.safeAddEntityToList(current, target.getPassiveDependencyAkaFanInList());
			addDependency(current.getPackageInfo(), target.getPackageInfo());
		}
	}

	/**
	 * 为列表中的所有类添加依赖关系
	 */
	public static void addDependency(ClassInfo currentClassInfo, List<ClassInfo> referenceClass) {
		for (ClassInfo referClass : referenceClass) {
			addDependency(currentClassInfo, referClass);
		}
	}

	/**
	 * 为两边都添加依赖关系
	 */
	public static void addDependency(PackageInfo current, PackageInfo target) {
		if (!Objects.equals(current, target)) {
			EntityUtil.safeAddEntityToList(target, current.getActiveDependencyAkaFanOutList());
			EntityUtil.safeAddEntityToList(current, target.getPassiveDependencyAkaFanInList());
		}
	}

}
