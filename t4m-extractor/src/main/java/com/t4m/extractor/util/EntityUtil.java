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

	private EntityUtil() {
	}

	public static FieldInfo getFieldByShortName(List<FieldInfo> fieldInfoList, String shortName) {
		Optional<FieldInfo> target = fieldInfoList.stream().filter(
				fieldInfo -> shortName.equals(fieldInfo.getShortName())).findFirst();
		return target.orElse(null);
	}

	public static MethodInfo getMethodByQualifiedNameAndRangeLocator(
			List<MethodInfo> methodInfoList, String qualifiedName, Range rangeLocator) {
		return methodInfoList.stream().filter(methodInfo -> qualifiedName.equals(methodInfo.getFullyQualifiedName()) &&
				rangeLocator.equals(methodInfo.getRangeLocator())).findFirst().orElse(null);
	}

	public static ClassInfo getClassByQualifiedName(
			List<ClassInfo> classInfoList, String fullyQualifiedClassName) {
		Optional<ClassInfo> optProjectInfo = classInfoList.stream().filter(
				classInfo -> fullyQualifiedClassName.equals(classInfo.getFullyQualifiedName())).findFirst();
		return optProjectInfo.orElse(null);
	}

	public static ClassInfo getClassByShortName(List<ClassInfo> classInfoList, String shortName) {
		Optional<ClassInfo> target = classInfoList.stream().filter(
				classInfo -> shortName.equals(classInfo.getShortName())).findFirst();
		return target.orElse(null);
	}


	public static ModuleInfo getModuleByShortName(List<ModuleInfo> moduleInfoList, String shortName) {
		Optional<ModuleInfo> target = moduleInfoList.stream().filter(
				moduleInfo -> shortName.equals(moduleInfo.getShortName())).findFirst();
		return target.orElse(null);
	}

	public static ModuleInfo getModuleByRelativeName(List<ModuleInfo> moduleInfoList, String relativeName) {
		Optional<ModuleInfo> target = moduleInfoList.stream().filter(
				moduleInfo -> relativeName.equals(moduleInfo.getRelativePath())).findFirst();
		return target.orElse(null);
	}

	public static PackageInfo getPackageByQualifiedName(List<PackageInfo> packageInfoList, String qualifiedName) {
		Optional<PackageInfo> target = packageInfoList.stream().filter(
				packageInfo -> qualifiedName.equals(packageInfo.getFullyQualifiedName())).findFirst();
		return target.orElse(null);
	}

	public static PackageInfo getPackageInfoByAbsolutePath(List<PackageInfo> packageInfoList, String absolotePath) {
		Optional<PackageInfo> target = packageInfoList.stream().filter(
				packageInfo -> absolotePath.equals(packageInfo.getAbsolutePath())).findFirst();
		return target.orElse(null);
	}

	/**
	 * Add a entity into list if it does not exist.
	 * @return the T entity itself.
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
	 * Add dependencies for both {@code current} and {@code target}
	 */
	public static void addDependency(ClassInfo current, ClassInfo target) {
		if (!Objects.equals(current, target)) {
			EntityUtil.safeAddEntityToList(target, current.getActiveDependencyAkaFanOutList());
			EntityUtil.safeAddEntityToList(current, target.getPassiveDependencyAkaFanInList());
			addDependency(current.getPackageInfo(), target.getPackageInfo());
		}
	}

	/**
	 * Add dependencies for {@code current} and each element in {@code referenceClassList}
	 */
	public static void addDependency(ClassInfo currentClassInfo, List<ClassInfo> referenceClassList) {
		for (ClassInfo referClass : referenceClassList) {
			addDependency(currentClassInfo, referClass);
		}
	}

	/**
	 * Add dependencies for both {@code current} and {@code target}
	 */
	public static void addDependency(PackageInfo current, PackageInfo target) {
		if (!Objects.equals(current, target)) {
			EntityUtil.safeAddEntityToList(target, current.getActiveDependencyAkaFanOutList());
			EntityUtil.safeAddEntityToList(current, target.getPassiveDependencyAkaFanInList());
		}
	}

}
