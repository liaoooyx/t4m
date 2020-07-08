package com.t4m.extractor.scanner.ast;

import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.util.ASTVisitorUtil;
import com.t4m.extractor.util.EntityUtil;
import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于补充额外信息，比如内部类、非public的类，方法
 * Created by Yuxiang Liao on 2020-06-21 13:02.
 */
public class CreateClassInfoVisitor extends ASTVisitor {

	private ClassInfo outerClassInfo;
	private ProjectInfo projectInfo;
	private List<ClassInfo> extraClassInfoList = new ArrayList<>();

	public CreateClassInfoVisitor(ClassInfo outerClassInfo, ProjectInfo projectInfo) {
		this.outerClassInfo = outerClassInfo;
		this.projectInfo = projectInfo;
		extraClassInfoList.add(outerClassInfo);
	}

	private void createClassInfo(AbstractTypeDeclaration node) {
		if (ASTVisitorUtil.isInnerClass(node)) {
			// 需要先确定对应外部类是哪个
			AbstractTypeDeclaration parentClassNode = ASTVisitorUtil.getParentAbstractTypeDeclaration(node);
			ClassInfo parentClassInfo = EntityUtil.getClassByShortName(extraClassInfoList,
			                                                           parentClassNode.getName().getIdentifier());
			// 创建新的ClassInfo作为内部类，并与外部类关联，并添加到projectInfo中
			String innerClassName =
					parentClassInfo.getShortName() + "$" + node.getName().toString(); // Class$InnerClass
			ClassInfo innerClassInfo = EntityUtil.safeAddEntityToList(new ClassInfo(innerClassName, parentClassInfo),
			                                                          parentClassInfo.getInnerClassList());
			innerClassInfo.setClassDeclaration(ClassInfo.ClassDeclaration.INNER_CLASS);
			innerClassInfo.setOuterClass(parentClassInfo);
			innerClassInfo.setMainPublicClass(outerClassInfo);
			EntityUtil.safeAddEntityToList(innerClassInfo, projectInfo.getNestedClassList());
		} else {
			//由于一个类文件可以创建多个类，因此还需要对这些其他类进行创建。
			String shortName = node.getName().getIdentifier();
			if (!shortName.equals(outerClassInfo.getShortName())) {
				ClassInfo extraClassInfo = new ClassInfo(shortName, outerClassInfo.getAbsolutePath());
				extraClassInfo.setFullyQualifiedName(outerClassInfo.getPackageFullyQualifiedName() + "." + shortName);
				extraClassInfo.setPackageInfo(outerClassInfo.getPackageInfo());
				extraClassInfo.setPackageFullyQualifiedName(outerClassInfo.getPackageFullyQualifiedName());
				extraClassInfo.setClassDeclaration(ClassInfo.ClassDeclaration.EXTRA_CLASS);
				extraClassInfo.setMainPublicClass(outerClassInfo);
				EntityUtil.safeAddEntityToList(extraClassInfo, outerClassInfo.getExtraClassList());
				EntityUtil.safeAddEntityToList(extraClassInfo, projectInfo.getExtraClassList());
				extraClassInfoList.add(extraClassInfo);
			}
		}
	}

	/*------------------------------------------------------------------------------------------*/

	@Override
	public boolean visit(TypeDeclaration node) {
		createClassInfo(node);
		return true;
	}

	@Override
	public boolean visit(EnumDeclaration node) {
		createClassInfo(node);
		return true;
	}

	@Override
	public boolean visit(AnnotationTypeDeclaration node) {
		createClassInfo(node);
		return true;
	}
}
