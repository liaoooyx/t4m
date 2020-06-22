package com.t4m.extractor.scanner.ast;

import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.ProjectInfo;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.TypeDeclaration;

/**
 * Created by Yuxiang Liao on 2020-06-21 13:02.
 */
public class InnerClassVisitor extends ASTVisitor {


	private ClassInfo outerClassInfo;
	private ProjectInfo projectInfo;


	public boolean isInnerClass(TypeDeclaration node) {
		return !outerClassInfo.getShortName().equals(node.getName().toString());
	}

	public InnerClassVisitor(ClassInfo outerClassInfo, ProjectInfo projectInfo) {
		this.outerClassInfo = outerClassInfo;
		this.projectInfo = projectInfo;
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		//关于SLOC度量，由于内部类与非内部类的计算方式不同，因此需要进行区分。
		String[] sourceLines;
		if (isInnerClass(node)) {
			// 内部类，则创建新的ClassInfo作为内部类，并与外部类关联，并添加到projectInfo中
			String innerClassName = outerClassInfo.getShortName() + "$" + node.getName().toString(); // Class$InnerClass
			ClassInfo innerClassInfo = outerClassInfo.safeAddInnerClassList(new ClassInfo(innerClassName, outerClassInfo));
			innerClassInfo.setOuterClass(outerClassInfo);
			projectInfo.safeAddInnerClassList(innerClassInfo);
		}

		return true;
	}
}
