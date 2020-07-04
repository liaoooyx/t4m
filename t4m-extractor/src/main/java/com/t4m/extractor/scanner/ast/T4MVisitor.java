package com.t4m.extractor.scanner.ast;

import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.ModuleInfo;
import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.exception.DuplicatedInnerClassFoundedException;
import com.t4m.extractor.metric.SLOCMetric;
import com.t4m.extractor.util.ASTVisitorUtil;
import com.t4m.extractor.util.EntityUtil;
import org.eclipse.jdt.core.dom.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;

/**
 * Created by Yuxiang Liao on 2020-06-18 13:31.
 */
public class T4MVisitor extends ASTVisitor {

	public static final Logger LOGGER = LoggerFactory.getLogger(ASTVisitor.class);

	private final ClassInfo outerClassInfo;
	private final ProjectInfo projectInfo;

	// 由 package 和 import 声明的包和类，对应projectInfo中的包和类
	private List<ClassInfo> importedClassList = new ArrayList<>();
	private List<PackageInfo> importedPackageList = new ArrayList<>();

	public T4MVisitor(ClassInfo outerClassInfo, ProjectInfo projectInfo) {
		this.outerClassInfo = outerClassInfo;
		this.projectInfo = projectInfo;
	}

	/**
	 * Class.InnerClass -> Class$InnerClass
	 */
	private String transferShortName(String shortName) {
		return shortName.replaceAll("\\.", Matcher.quoteReplacement("$"));
	}

	/**
	 * 先从importedClassList中查找，如果没有，则从importedPackageList中查找。 如果都没有，说明该类并不是由项目创建（来自于外部jar包），返回null。 注意 new
	 * ComplexClassB().new InnerClassC();会出现单独出现内部类名的情况。 因此还需要进入类的内部类列表进行查询
	 */
	private ClassInfo findClassInfoFromImportedListByShortName(String shortName) {
		shortName = transferShortName(shortName); //Class.InnerClass -> Class$InnerClass
		ClassInfo targetClass = null;
		targetClass = EntityUtil.getClassOrInnerClassFromOuterClassListByRawShortName(importedClassList, shortName);
		if (targetClass != null) {
			return targetClass;
		}
		for (PackageInfo packageInfo : importedPackageList) {
			targetClass = EntityUtil.getClassOrInnerClassFromOuterClassListByRawShortName(packageInfo.getClassList(),
			                                                                              shortName);
			if (targetClass != null) {
				return targetClass;
			}
		}
		return null;
	}

	public boolean isAbstractClass(List<IExtendedModifier> modifiers) {
		return modifiers.stream().anyMatch(modifier -> {
			if (modifier.isModifier()) {
				return ((Modifier) modifier).isAbstract();
			} else {
				return false;
			}
		});
	}

	/**
	 * 判断当前节点属于内部类或外部类，并返回对应的ClassInfo
	 */
	public ClassInfo resolveClassInfo(ASTNode node) {
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
				currentClassInfo = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(),
				                                                      outerClassQualifiedName);
			}
		}
		return currentClassInfo;
	}

	/**
	 * 判断是否为方法声明中的返回类型修饰符
	 */
	private boolean isMethodReturnModifier(SimpleType node) {
		if (node.getParent() instanceof MethodDeclaration) {
			MethodDeclaration methodNode = (MethodDeclaration) node.getParent();
			Objects.equals(methodNode.getReturnType2().toString(), node.getName().getFullyQualifiedName());
			return true;
		}
		return false;
	}

	/**
	 * 从限定名中查找对应的类名 Class.InnerClass, com.foo.Class, com.foo.Class.InnnerClass, var, Class 如果不存在，则返回null。
	 */
	public ClassInfo retriveClassInfoInQualifiedName(String qualifiedName) {
		String[] splitNames = qualifiedName.split("\\.");
		ClassInfo classInfo = null;
		for (int i = 0; i < splitNames.length; i++) {
			ClassInfo outerClassInfo = null;
			try {
				outerClassInfo = findClassInfoFromImportedListByShortName(splitNames[i]);
			} catch (DuplicatedInnerClassFoundedException e) {
				LOGGER.error("默认不会在此处发生异常", e);
			}
			if (outerClassInfo != null && (i + 1) < splitNames.length) {
				String possibleInnerClassName = splitNames[i] + "$" + splitNames[i + 1];
				ClassInfo innerClassInfo = EntityUtil.getClassByShortName(outerClassInfo.getInnerClassList(),
				                                                          possibleInnerClassName);
				if (innerClassInfo != null) {
					return innerClassInfo;
				} else {
					return outerClassInfo;
				}
			} else {
				return outerClassInfo;
			}
		}
		return null;
	}

	/*------------------------------------------------------------------------------------------*/

	/**
	 * 计算SLOC
	 */
	public void slocMetric(ClassInfo currentClassInfo, AbstractTypeDeclaration node) {
		// 关于SLOC度量，由于内部类与非内部类的计算方式不同，因此需要进行区分：
		// 内部类，获取源码行；非内部类，需要获取包括package和import关键字的行
		ASTNode sourceLineNode = ASTVisitorUtil.isInnerClass(node) ? node : node.getParent();
		String[] sourceLines = sourceLineNode.toString().split(System.lineSeparator());

		Map<ClassInfo.SLOCType, Integer> slocCounterMap = currentClassInfo.getSlocCounterMap();
		Arrays.stream(sourceLines).forEach(line -> SLOCMetric.slocCounterFromAST(line, slocCounterMap));
		currentClassInfo.setSlocCounterMap(slocCounterMap);
	}

	/*------------------------------------------------------------------------------------------*/

	/**
	 * 将当前的包加入列表中，后续可用于依赖探测等
	 */
	@Override
	public boolean visit(PackageDeclaration node) {
		ModuleInfo currentModule = outerClassInfo.getPackageInfo().getModuleInfo();
		String pkgQualifiedName = node.getName().getFullyQualifiedName();
		PackageInfo packageInfo = projectInfo.getPackageInfoByFullyQualifiedName(pkgQualifiedName, currentModule);
		importedPackageList.add(packageInfo);
		return true;
	}

	/**
	 * 将引入的包和类加入列表中，后续可用于依赖探测等
	 */
	@Override
	public boolean visit(ImportDeclaration node) {
		// import 直接表明了不同包之间的依赖关系，但包内类的依赖关系需要用其他方法。
		// 但引入的类可能属于项目外的Jar包，因此需要过滤方式。
		ModuleInfo currentModule = outerClassInfo.getPackageInfo().getModuleInfo();
		String qualifiedImportedName = node.getName().getFullyQualifiedName();
		//判断引入的是包还是类: 先检索是否为包, 如果不是再检索是否为类
		// TODO 当出现多个同名时，该如何处理
		PackageInfo packageInfo = projectInfo.getPackageInfoByFullyQualifiedName(qualifiedImportedName, currentModule);
		if (packageInfo != null) {
			importedPackageList.add(packageInfo);
		} else {
			ClassInfo classInfo = projectInfo.getClassInfoByFullyQualifiedName(qualifiedImportedName, currentModule);
			if (classInfo != null) {
				importedClassList.add(classInfo);
			}
		}
		return true;
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		ClassInfo currentClassInfo = resolveClassInfo(node);

		// 提取关于SLOC的信息
		slocMetric(currentClassInfo, node);

		// 类的类型
		if (node.isInterface()) {
			currentClassInfo.setClassModifier(ClassInfo.ClassModifier.INTERFACE);
		} else if (isAbstractClass(node.modifiers())) {
			currentClassInfo.setClassModifier(ClassInfo.ClassModifier.ABSTRACT_CLASS);
		} else {
			currentClassInfo.setClassModifier(ClassInfo.ClassModifier.CLASS);
		}
		// 方法数量
		currentClassInfo.setNumberOfMethods(node.getMethods().length);
		// 字段数量
		currentClassInfo.setNumberOfFields(node.getFields().length);
		// 父类
		if (node.getSuperclassType() != null) {
			String supperClassShortName = node.getSuperclassType().toString();
			ClassInfo supperClassInfo = null;
			supperClassInfo = findClassInfoFromImportedListByShortName(supperClassShortName);
			currentClassInfo.setSupperClass(supperClassInfo);
		}
		// 接口
		if (node.superInterfaceTypes() != null) {
			node.superInterfaceTypes().forEach(interf -> {
				String interfaceShortName = interf.toString();
				ClassInfo interfaceClass = null;
				interfaceClass = findClassInfoFromImportedListByShortName(interfaceShortName);
				EntityUtil.safeAddEntityToList(interfaceClass,currentClassInfo.getInterfaceList());
			});
		}
		return true;
	}

	@Override
	public boolean visit(EnumDeclaration node) {
		ClassInfo currentClassInfo = resolveClassInfo(node);

		// 提取关于SLOC的信息
		slocMetric(currentClassInfo, node);

		// 补充类的基本信息
		// 枚举类型
		currentClassInfo.setClassModifier(ClassInfo.ClassModifier.ENUM);
		// 枚举常量
		currentClassInfo.setNumberOfEnumConstants(node.enumConstants().size());
		int numOfFields = 0;
		int numOfMethods = 0;
		for (Object tempNode : node.bodyDeclarations()) {
			if (tempNode instanceof FieldDeclaration) {
				numOfFields++;
			} else if (tempNode instanceof MethodDeclaration) {
				numOfMethods++;
			}
		}
		// 字段数量
		currentClassInfo.setNumberOfFields(numOfFields);
		// 方法数量
		currentClassInfo.setNumberOfMethods(numOfMethods);
		// 枚举默认继承自Enum类，无法继承
		// 接口
		if (node.superInterfaceTypes() != null) {
			node.superInterfaceTypes().forEach(interf -> {
				String interfaceShortName = interf.toString();
				ClassInfo interfaceClass = null;
				interfaceClass = findClassInfoFromImportedListByShortName(interfaceShortName);
				EntityUtil.safeAddEntityToList(interfaceClass,currentClassInfo.getInterfaceList());
			});
		}
		return true;
	}

	@Override
	public boolean visit(AnnotationTypeDeclaration node) {
		ClassInfo currentClassInfo = resolveClassInfo(node);

		// 提取关于SLOC的信息
		slocMetric(currentClassInfo, node);

		// 枚举类型
		currentClassInfo.setClassModifier(ClassInfo.ClassModifier.ANNOTATION);
		// 枚举常量
		int numOfAnnotationMember = 0;
		for (Object tempNode : node.bodyDeclarations()) {
			if (tempNode instanceof AnnotationTypeMemberDeclaration) {
				numOfAnnotationMember++;
			}
		}
		// 注解参数
		currentClassInfo.setNumberOfAnnotationMembers(numOfAnnotationMember);

		return true;
	}

	@Override
	public boolean visit(SimpleType node) {
		// 方法返回类型不属于依赖关系
		if (!isMethodReturnModifier(node)) {
			// 判断是否存在于importedList列表中
			// 注意 new ComplexClassB().new InnerClassC();会出现单独出现内部类名的情况。
			// 因此还需要进入类的内部类列表进行查询
			String varClassShortName = transferShortName(node.getName().toString());
			ClassInfo varDeclaringClassInfo = null;
			try {
				varDeclaringClassInfo = findClassInfoFromImportedListByShortName(varClassShortName);
			} catch (DuplicatedInnerClassFoundedException e) {
				LOGGER.error("当前类节点 [{}] 与多个内部类重名，无法正确解析", varClassShortName, e);
			}
			// 判断当前是内部类或外部类
			ClassInfo currentClassInfo = resolveClassInfo(node);
			// 添加依赖关系
			if (varDeclaringClassInfo != null) {
				EntityUtil.safeAddEntityToList(varDeclaringClassInfo,currentClassInfo.getActiveDependencyList());
				EntityUtil.safeAddEntityToList(currentClassInfo,varDeclaringClassInfo.getPassiveDependencyList());
			}
		}
		return true;
	}

	@Override
	public boolean visit(MethodInvocation node) {
		// 静态方法调用需要想办法解决识别类
		// 普通方法调用无所谓，因为需要操作依赖时，必定要声明对象，否则无法操作，如果是通过方法操作的，那也只是依赖于方法所属的类
		// Class.InnerClass, com.foo.Class, com.foo.Class.InnnerClass
		// 下面步骤只适用于通过类之间调用静态方法；如果通过对象调用静态方法，那么默认该对象之前已经被声明过
		if (node.getExpression() != null) {
			String qualifiedNameAheadMethodName = node.getExpression().toString();
			ClassInfo staticInvokingClassInfo = retriveClassInfoInQualifiedName(qualifiedNameAheadMethodName);
			// 判断内部类或外部类
			ClassInfo currentClassInfo = resolveClassInfo(node);
			// 添加依赖关系
			if (staticInvokingClassInfo != null) {
				EntityUtil.safeAddEntityToList(staticInvokingClassInfo,currentClassInfo.getActiveDependencyList());
				EntityUtil.safeAddEntityToList(currentClassInfo,staticInvokingClassInfo.getPassiveDependencyList());
			}
		}
		return true;
	}

}
