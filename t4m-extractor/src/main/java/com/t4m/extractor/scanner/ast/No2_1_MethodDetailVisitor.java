package com.t4m.extractor.scanner.ast;

import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.entity.ProjectInfo;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yuxiang Liao on 2020-07-09 01:52.
 */
public class No2_1_MethodDetailVisitor extends ASTVisitor {

	private static final Logger LOGGER = LoggerFactory.getLogger(No2_MethodAndFieldInfoVisitor.class);

	private ClassInfo outerClassInfo;
	private ClassInfo currentClassInfo;
	private ProjectInfo projectInfo;

	// 由 package 和 import 声明的包和类，对应projectInfo中的包和类
	private List<ClassInfo> importedClassList = new ArrayList<>();
	private List<PackageInfo> importedPackageList = new ArrayList<>();

	public No2_1_MethodDetailVisitor(
			ClassInfo outerClassInfo, ClassInfo currentClassInfo, ProjectInfo projectInfo,
			List<ClassInfo> importedClassList, List<PackageInfo> importedPackageList) {
		this.outerClassInfo = outerClassInfo;
		this.currentClassInfo = currentClassInfo;
		this.projectInfo = projectInfo;
		this.importedClassList = importedClassList;
		this.importedPackageList = importedPackageList;
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		List properties = node.structuralPropertiesForType();
		System.out.println(properties);
		return super.visit(node);
	}
}
