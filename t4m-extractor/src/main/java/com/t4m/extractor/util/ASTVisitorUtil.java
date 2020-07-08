package com.t4m.extractor.util;

import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.entity.ProjectInfo;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.util.List;
import java.util.regex.Matcher;

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

	/**
	 * Class.InnerClass -> Class$InnerClass
	 */
	public static String transferShortName(String shortName) {
		return shortName.replaceAll("\\.", Matcher.quoteReplacement("$"));
	}

	/**
	 * Class.InnerClass -> Class$InnerClass; com.a.b.c.Class.InnerClass -> com.a.b.c.Class$InnerClass
	 */
	public static String transferQualifiedName(String name) {
		if (name.contains(".")) {
			int index = name.lastIndexOf(".");
			return name.substring(0, index) + "$" + name.substring(index + 1);
		} else {
			return name;
		}
	}

	/**
	 * 传入非全限定类名。 先从importedClassList中查找，如果没有，则从importedPackageList中查找。 如果都没有，说明该类并不是由项目创建（来自于外部jar包），返回null。 (注意 new
	 * ComplexClassB().new InnerClassC();会出现单独出现内部类名的情况。 因此还需要进入类的内部类列表进行查询)
	 */
	public static ClassInfo findClassInfoFromImportedListByShortName(
			String unIdentifiedName, List<ClassInfo> importedClassList, List<PackageInfo> importedPackageList) {
		// com.a.b.c.Class.InnerClass -> com.a.b.c.Class$InnerClass
		ClassInfo targetClass = null;
		targetClass = EntityUtil.getClassOrNestedClassFromOuterClassListByShortName(importedClassList,
		                                                                            unIdentifiedName);
		if (targetClass != null) {
			return targetClass;
		}
		for (PackageInfo packageInfo : importedPackageList) {
			targetClass = EntityUtil.getClassOrNestedClassFromOuterClassListByShortName(packageInfo.getClassList(),
			                                                                            unIdentifiedName);
			if (targetClass != null) {
				return targetClass;
			}
		}
		return null;
	}


	/**
	 * 传入全限定类名，但类名可能不合法，如果不合法则无法找到对应的类 先从importedClassList中查找，如果没有，则从importedPackageList中查找。
	 * 如果都没有，说明该类并不是由项目创建（来自于外部jar包），返回null。 (注意 new ComplexClassB().new InnerClassC();会出现单独出现内部类名的情况。
	 * 因此还需要进入类的内部类列表进行查询)
	 */
	public static ClassInfo findClassInfoFromImportedListByQualifiedName(
			String unIdentifiedName, List<ClassInfo> importedClassList, List<PackageInfo> importedPackageList) {
		// com.a.b.c.Class.InnerClass -> com.a.b.c.Class$InnerClass
		ClassInfo targetClass = null;
		targetClass = EntityUtil.getClassOrNestedClassFromOuterClassListByQualifiedName(importedClassList,
		                                                                                unIdentifiedName);
		if (targetClass != null) {
			return targetClass;
		}
		for (PackageInfo packageInfo : importedPackageList) {
			targetClass = EntityUtil.getClassOrNestedClassFromOuterClassListByQualifiedName(packageInfo.getClassList(),
			                                                                                unIdentifiedName);
			if (targetClass != null) {
				return targetClass;
			}
		}
		return null;
	}


	/**
	 * 判断当前节点属于内部类或外部类，并返回对应的ClassInfo
	 */
	public static ClassInfo resolveClassInfo(ASTNode node, ClassInfo outerClassInfo, ProjectInfo projectInfo) {
		AbstractTypeDeclaration classNode = null;
		switch (node.getNodeType()) {
			case ASTNode.TYPE_DECLARATION:
			case ASTNode.ANNOTATION_TYPE_DECLARATION:
			case ASTNode.ENUM_DECLARATION:
				classNode = (AbstractTypeDeclaration) node;
				break;
			default:
				classNode = ASTVisitorUtil.getParentAbstractTypeDeclaration(node);
		}
		ClassInfo currentClassInfo;
		String shortClassName = transferShortName(classNode.getName().toString());
		if (ASTVisitorUtil.isInnerClass(classNode)) {
			// 需要先确定对应外部类是哪个
			AbstractTypeDeclaration parentClassNode = ASTVisitorUtil.getParentAbstractTypeDeclaration(classNode);
			ClassInfo parentClassInfo = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(), outerClassInfo
					.getPackageFullyQualifiedName() + "." + parentClassNode.getName().getIdentifier());
			// 再找内部类
			currentClassInfo = EntityUtil.getClassByQualifiedName(parentClassInfo.getInnerClassList(),
			                                                      parentClassInfo.getFullyQualifiedName() + "$" +
					                                                      shortClassName);
		} else {
			String outerClassQualifiedName = outerClassInfo.getPackageFullyQualifiedName() + "." + shortClassName;
			if (outerClassQualifiedName.equals(outerClassInfo.getFullyQualifiedName())) {
				currentClassInfo = outerClassInfo;
			} else {
				currentClassInfo = EntityUtil.getClassByQualifiedName(projectInfo.getExtraClassList(),
				                                                      outerClassQualifiedName);
			}
		}
		return currentClassInfo;
	}
}
