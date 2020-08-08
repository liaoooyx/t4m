package com.t4m.extractor.scanner.javaparser;

import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.util.EntityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yuxiang Liao on 2020-07-16 15:52.
 */
public class No1_ClassInfoVisitor extends VoidVisitorAdapter<Void> {

	private static final Logger LOGGER = LoggerFactory.getLogger(No1_ClassInfoVisitor.class);

	private ClassInfo outerClassInfo;
	private ProjectInfo projectInfo;
	private List<ClassInfo> allShownClassInfoList = new ArrayList<>();

	public No1_ClassInfoVisitor(ClassInfo outerClassInfo, ProjectInfo projectInfo) {
		this.outerClassInfo = outerClassInfo;
		this.projectInfo = projectInfo;
		allShownClassInfoList.add(outerClassInfo);
	}

	private void createClassInfo(TypeDeclaration n) {
		if (n.isNestedType()) {
			// Need to check which is the previous level
			TypeDeclaration parentClassNode = (TypeDeclaration) n.findAncestor(TypeDeclaration.class).get();
			ClassInfo parentClassInfo = EntityUtil.getClassByShortName(allShownClassInfoList,
			                                                           parentClassNode.getName().getIdentifier());
			// Create a new ClassInfo object for nested class, connecting to outer class, and added to projectInfo object.
			String innerClassName = n.getName().toString();
			ClassInfo innerClassInfo = EntityUtil.safeAddEntityToList(new ClassInfo(innerClassName, parentClassInfo),
			                                                          parentClassInfo.getNestedClassList());
			innerClassInfo.setClassDeclaration(ClassInfo.ClassDeclaration.NESTED_CLASS);
			innerClassInfo.setOuterClass(parentClassInfo);
			innerClassInfo.setMainPublicClass(outerClassInfo);
			EntityUtil.safeAddEntityToList(innerClassInfo, projectInfo.getNestedClassList());
			EntityUtil.safeAddEntityToList(innerClassInfo, innerClassInfo.getPackageInfo().getNestedClassList());
			allShownClassInfoList.add(innerClassInfo);
		} else {
			// For package private outer classes.
			String shortName = n.getName().getIdentifier();
			if (!shortName.equals(outerClassInfo.getShortName())) {
				ClassInfo extraClassInfo = new ClassInfo(shortName, outerClassInfo.getAbsolutePath());
				extraClassInfo.setFullyQualifiedName(outerClassInfo.getPackageFullyQualifiedName() + "." + shortName);
				extraClassInfo.setPackageInfo(outerClassInfo.getPackageInfo());
				extraClassInfo.setPackageFullyQualifiedName(outerClassInfo.getPackageFullyQualifiedName());
				extraClassInfo.setClassDeclaration(ClassInfo.ClassDeclaration.NON_PUBLIC_OUTER_CLASS);
				extraClassInfo.setMainPublicClass(outerClassInfo);
				EntityUtil.safeAddEntityToList(extraClassInfo, outerClassInfo.getExtraClassList());
				EntityUtil.safeAddEntityToList(extraClassInfo, projectInfo.getExtraClassList());
				EntityUtil.safeAddEntityToList(extraClassInfo, extraClassInfo.getPackageInfo().getExtraClassList());
				allShownClassInfoList.add(extraClassInfo);
			}
		}
	}

	@Override
	public void visit(ClassOrInterfaceDeclaration n, Void arg) {
		createClassInfo(n);
		super.visit(n, arg);
	}

	@Override
	public void visit(AnnotationDeclaration n, Void arg) {
		createClassInfo(n);
		super.visit(n, arg);
	}

	@Override
	public void visit(EnumDeclaration n, Void arg) {
		createClassInfo(n);
		super.visit(n, arg);
	}
}
