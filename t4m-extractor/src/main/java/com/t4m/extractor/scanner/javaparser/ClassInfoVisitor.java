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
import java.util.Optional;

/**
 * Created by Yuxiang Liao on 2020-07-16 15:52.
 */
public class ClassInfoVisitor extends VoidVisitorAdapter<Void> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClassInfoVisitor.class);

	private final ClassInfo outerClassInfo;
	private final ProjectInfo projectInfo;
	private final List<ClassInfo> allShownClassInfoList = new ArrayList<>();

	public ClassInfoVisitor(ClassInfo outerClassInfo, ProjectInfo projectInfo) {
		this.outerClassInfo = outerClassInfo;
		this.projectInfo = projectInfo;
		allShownClassInfoList.add(outerClassInfo);
	}

	private void createClassInfo(TypeDeclaration n) {
		if (n.isNestedType()) {
			// Need to check which is the previous level
			TypeDeclaration parentClassNode;
			Optional<TypeDeclaration> optional = n.findAncestor(TypeDeclaration.class);
			if (optional.isPresent()){
				parentClassNode = optional.get();
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
				if (innerClassInfo.getPackageInfo() != null) {
					EntityUtil.safeAddEntityToList(innerClassInfo, innerClassInfo.getPackageInfo().getNestedClassList());
				} else {
					LOGGER.error("Cannot get the package to which the inner class {} belongs",
					             innerClassInfo.getFullyQualifiedName());
				}
				allShownClassInfoList.add(innerClassInfo);
			}
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
