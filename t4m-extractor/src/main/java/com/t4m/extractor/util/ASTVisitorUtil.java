package com.t4m.extractor.util;

import com.t4m.extractor.entity.ClassInfo;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.util.List;

/**
 * Created by Yuxiang Liao on 2020-07-04 02:15.
 */
public class ASTVisitorUtil {

	/**
	 * 如果是最外层节点，那么就是外部类，否则是内部类
	 */
	public static boolean isInnerClass(AbstractTypeDeclaration node) {
		return !(node.getParent() instanceof CompilationUnit);
	}

	/**
	 * 获取上一层的类声明节点：以递归的方式，向上查找所属的类的TypeDeclaration，EnumDeclaration，或ANNOTATION_TYPE_DECLARATION（内部类或外部类）
	 */
	public static AbstractTypeDeclaration getParentAbstractTypeDeclaration(ASTNode node) {
		if (node.getParent() instanceof CompilationUnit) {
			return (AbstractTypeDeclaration) node;
		}
		ASTNode parentNode = node.getParent();
		switch (parentNode.getNodeType()) {
			case ASTNode.TYPE_DECLARATION:
			case ASTNode.ANNOTATION_TYPE_DECLARATION:
			case ASTNode.ENUM_DECLARATION:
				return (AbstractTypeDeclaration) parentNode;
			default:
				return getParentAbstractTypeDeclaration(parentNode);
		}
	}

}
