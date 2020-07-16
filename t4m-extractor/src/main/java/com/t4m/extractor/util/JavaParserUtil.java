package com.t4m.extractor.util;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.ProjectInfo;

import java.util.List;

/**
 * Created by Yuxiang Liao on 2020-07-12 01:40.
 */
public class JavaParserUtil {

	/**
	 * 返回当前节点所属的ClassInfo
	 */
	public static ClassInfo resolveCurrentClassInfo(Node node, ProjectInfo projectInfo) {
		String qualifiedName;
		if (node instanceof TypeDeclaration) {
			qualifiedName = String.valueOf(((TypeDeclaration) node).getFullyQualifiedName().orElse(null));
		} else {
			qualifiedName = String.valueOf(node.findAncestor(TypeDeclaration.class).orElse(null).getFullyQualifiedName()
			                                   .orElse(null));
		}
		return EntityUtil.getClassByQualifiedName(projectInfo.getAllClassList(), qualifiedName);
	}

	/**
	 * 添加依赖关系
	 */
	public static void addDependency(ClassInfo currentClassInfo, List<ClassInfo> referenceClass) {
		for (ClassInfo referClass : referenceClass) {
			EntityUtil.safeAddEntityToList(referClass, currentClassInfo.getActiveDependencyAkaFanOutList());
			EntityUtil.safeAddEntityToList(currentClassInfo, referClass.getPassiveDependencyAkaFanInList());
		}
	}

	/**
	 * 添加依赖关系
	 */
	public static void addDependency(ClassInfo currentClassInfo, ClassInfo referenceClass) {
		EntityUtil.safeAddEntityToList(referenceClass, currentClassInfo.getActiveDependencyAkaFanOutList());
		EntityUtil.safeAddEntityToList(currentClassInfo, referenceClass.getPassiveDependencyAkaFanInList());
	}
}
