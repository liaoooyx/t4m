package com.t4m.extractor.scanner.javaparser;

import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedConstructorDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.FieldInfo;
import com.t4m.extractor.entity.MethodInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.metric.RFCMetric;
import com.t4m.extractor.util.EntityUtil;
import com.t4m.extractor.util.JavaParserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by Yuxiang Liao on 2020-07-11 11:16.
 */
public class No3_InMethodDependencyVisitor extends VoidVisitorAdapter<Void> {

	private static final Logger LOGGER = LoggerFactory.getLogger(No3_InMethodDependencyVisitor.class);

	private ClassInfo outerClassInfo;
	private ProjectInfo projectInfo;

	public No3_InMethodDependencyVisitor(ClassInfo outerClassInfo, ProjectInfo projectInfo) {
		this.outerClassInfo = outerClassInfo;
		this.projectInfo = projectInfo;
	}

	/**
	 * 查找字段赋值中，子节点出现的度量元数据：耦合，RFC（调用其他类的方法）
	 */
	@Override
	public void visit(FieldDeclaration n, Void arg) {
		ClassInfo currentClassInfo = JavaParserUtil.resolveCurrentClassInfo(n, projectInfo);
		if (currentClassInfo == null) {
			LOGGER.error("Cannot resolve current block declaration.");
			return;
		}
		// 解析方法内部产生的依赖关系，内聚力
		resolveMetaInfoForMetric(n, currentClassInfo);
	}

	@Override
	public void visit(BlockStmt n, Void arg) {
		// 由于删除了方法声明和构造器声明中的super.visit(n, arg)，所以这里只会出现方法块的初始化
		ClassInfo currentClassInfo = JavaParserUtil.resolveCurrentClassInfo(n, projectInfo);
		if (currentClassInfo == null) {
			LOGGER.error("Cannot resolve current block declaration.");
			return;
		}
		// 解析方法内部产生的依赖关系，内聚力
		resolveMetaInfoForMetric(n, currentClassInfo);
	}

	@Override
	public void visit(ConstructorDeclaration n, Void arg) {
		ClassInfo currentClassInfo = JavaParserUtil.resolveCurrentClassInfo(n, projectInfo);
		if (currentClassInfo == null) {
			LOGGER.error(
					"Cannot resolve current constructor declaration. It may be declared in the class within a method.");
			return;
		}
		MethodInfo currentMethodInfo = EntityUtil.getMethodByQualifiedNameAndRangeLocator(
				currentClassInfo.getMethodInfoList(),
				currentClassInfo.getFullyQualifiedName() + "." + n.getNameAsString(), n.getRange().orElse(null));
		if (currentMethodInfo == null) {
			LOGGER.error("需要检查代码：Cannot resolve current constructor declaration to MethodInfo: [{}] in [{}].",
			             n.getDeclarationAsString(), currentClassInfo.getFullyQualifiedName());
			return;
		}
		BlockStmt body = n.getBody();
		// 解析方法内部产生的依赖关系，内聚力
		if (body != null) {
			resolveMetaInfoForMetric(body, currentClassInfo, currentMethodInfo);
		}

		try {
			String methodQualifiedSignature = n.resolve().getQualifiedSignature();
			RFCMetric.countRFCMethodQualifiedSignatureMap(currentClassInfo.getRfcMethodQualifiedSignatureMap(),
			                                              methodQualifiedSignature);
		} catch (UnsolvedSymbolException e) {
			//RFC：无法定位该方法，使用非全限定路径签名，有概率可以解决部分方法重载的去重问题。
			currentClassInfo.getUnresolvedExceptionList().add("When resolving ConstructorDeclaration: " + e.toString());
			RFCMetric.countRFCMethodQualifiedSignatureMap(currentClassInfo.getRfcMethodQualifiedSignatureMap(),
			                                              n.getSignature().toString());
		}
	}

	@Override
	public void visit(MethodDeclaration n, Void arg) {
		ClassInfo currentClassInfo = JavaParserUtil.resolveCurrentClassInfo(n, projectInfo);
		if (currentClassInfo == null) {
			LOGGER.error("Cannot resolve current method declaration. It may be declared in the class within a method.");
			return;
		}
		MethodInfo currentMethodInfo = EntityUtil.getMethodByQualifiedNameAndRangeLocator(
				currentClassInfo.getMethodInfoList(),
				currentClassInfo.getFullyQualifiedName() + "." + n.getNameAsString(), n.getRange().orElse(null));
		if (currentMethodInfo == null) {
			LOGGER.error("需要检查代码：Cannot resolve current method declaration to MethodInfo: [{}] in [{}].",
			             n.getDeclarationAsString(), currentClassInfo.getFullyQualifiedName());
			return;
		}
		BlockStmt body = n.getBody().orElse(null);
		// 解析方法内部产生的依赖关系，内聚力
		if (body != null) {
			resolveMetaInfoForMetric(body, currentClassInfo, currentMethodInfo);
		}

		try {
			String methodQualifiedSignature = n.resolve().getQualifiedSignature();
			RFCMetric.countRFCMethodQualifiedSignatureMap(currentClassInfo.getRfcMethodQualifiedSignatureMap(),
			                                              methodQualifiedSignature);
		} catch (UnsolvedSymbolException e) {
			//RFC：无法定位该方法，使用非全限定路径签名，有概率可以解决部分方法重载的去重问题。
			currentClassInfo.getUnresolvedExceptionList().add("When resolving MethodDeclaration: " + e.toString());
			RFCMetric.countRFCMethodQualifiedSignatureMap(currentClassInfo.getRfcMethodQualifiedSignatureMap(),
			                                              n.getSignature().toString());
		}
	}

	/**
	 * 解析方法内部产生的依赖关系，内聚力，RFC度量的元数据
	 */
	private void resolveMetaInfoForMetric(BlockStmt body, ClassInfo currentClassInfo, MethodInfo currentMethodInfo) {
		// 方法内产生的依赖
		Set<String> dependencySet = new HashSet<>();
		// 任何解析时出现的异常，用于没有提供jar路径的项目
		List<String> exceptionList = currentClassInfo.getUnresolvedExceptionList();
		// LOCM4: 涉及的本地方法调用
		List<MethodInfo> localMethodInfoList = currentMethodInfo.getLocalMethodAccessList();
		// LOCM4: 涉及的本地字段调用
		List<FieldInfo> fieldInfoList = currentMethodInfo.getFieldAccessList();

		commonOperationToResolveMetaInfo(body, currentClassInfo, dependencySet, exceptionList, localMethodInfoList,
		                                 fieldInfoList);
	}

	/**
	 * 解析字段声明或初始化块内部产生的依赖关系，内聚，RFC度量的元数据
	 */
	private void resolveMetaInfoForMetric(Node body, ClassInfo currentClassInfo) {
		// 方法内产生的依赖
		Set<String> dependencySet = new HashSet<>();
		// 任何解析时出现的异常，用于没有提供jar路径的项目
		List<String> exceptionList = currentClassInfo.getUnresolvedExceptionList();
		// LOCM4: 涉及的本地方法调用
		List<MethodInfo> localMethodInfoList = new ArrayList<>();
		// LOCM4: 涉及的本地字段调用
		List<FieldInfo> fieldInfoList = new ArrayList<>();

		commonOperationToResolveMetaInfo(body, currentClassInfo, dependencySet, exceptionList, localMethodInfoList,
		                                 fieldInfoList);
	}

	/**
	 * 包括构造需要的列表，扫描子节点，并在扫描后添加依赖
	 */
	private void commonOperationToResolveMetaInfo(
			Node body, ClassInfo currentClassInfo, Set<String> dependencySet, List<String> exceptionList,
			List<MethodInfo> localMethodInfoList, List<FieldInfo> fieldInfoList) {

		// RFC的所有方法全限定签名
		Map<String, Integer> rfcMethodQualifiedSignatureMap = currentClassInfo.getRfcMethodQualifiedSignatureMap();

		//扫描子节点
		scanChildNode(body, currentClassInfo, dependencySet, exceptionList, localMethodInfoList, fieldInfoList,
		              rfcMethodQualifiedSignatureMap);

		// 添加方法内部出现的依赖关系
		for (String dependencyClassName : dependencySet) {
			ClassInfo referenceClass = EntityUtil.getClassByQualifiedName(projectInfo.getAllClassList(),
			                                                              dependencyClassName);
			if (referenceClass != null) {
				JavaParserUtil.addDependency(currentClassInfo, referenceClass);
			}
		}
	}

	/**
	 * 递归的向下扫描方法声明的子节点，将需要的信息保存在传入参数的列表中
	 */
	private void scanChildNode(
			Node n, ClassInfo currentClassInfo, Set<String> dependencySet, List<String> exceptionList,
			List<MethodInfo> localMethodInfoList, List<FieldInfo> fieldInfoList,
			Map<String, Integer> rfcMethodQualifiedSignatureMap) {
		if (n.getChildNodes().isEmpty()) {
			return;
		}
		for (Node node : n.getChildNodes()) {
			System.out.println("[" + node.getMetaModel().getTypeName() + "]: " + node.toString());
			if (node instanceof MethodCallExpr) {
				// 方法调用 methodA()
				// 只能在这里解析调用的方法所属的类，子节点无法解析出包装类
				// 方法名最终只作为SimpleName节点，
				// 如果调用的是外部类，最后会出现FieldAccessExpr以及NameExpr，如果没有上述2个，那么一定是类内的非静态方法（也可能是内部类调用外部类的方法）
				// 如果它的父类是方法调用，那么通过解析方法的返回类型，可以间接的方法所属的类
				resolveMethodCallExpr((MethodCallExpr) node, currentClassInfo, dependencySet, exceptionList,
				                      localMethodInfoList, rfcMethodQualifiedSignatureMap);
			} else if (node instanceof MethodReferenceExpr) {
				// System.out::println
				// 只能在这里解析调用的方法所属的类，子类无法解析包装类
				resolveMethodReferenceExpr((MethodReferenceExpr) node, currentClassInfo, dependencySet, exceptionList,
				                           localMethodInfoList, rfcMethodQualifiedSignatureMap);
			} else if (node instanceof ObjectCreationExpr) {
				// ObjectCreationExpr，它的子节点ClassOrInterfaceType是无法解析的(会产生异常)
				resloveObjectCreationExpr((ObjectCreationExpr) node, currentClassInfo, dependencySet, exceptionList,
				                          localMethodInfoList, rfcMethodQualifiedSignatureMap);
			} else if (node instanceof ExplicitConstructorInvocationStmt) {
				// ExplicitConstructorInvocationStmt，没有子节点
				resolveExplicitConstructorInvocationStmt((ExplicitConstructorInvocationStmt) node, currentClassInfo,
				                                         dependencySet, exceptionList, localMethodInfoList,
				                                         rfcMethodQualifiedSignatureMap);
			} else if (node instanceof FieldAccessExpr) {
				// 字段访问,可能会访问到其他类的字段，比如System.out,
				resolveFieldAccessExpr((FieldAccessExpr) node, currentClassInfo, dependencySet, exceptionList,
				                       fieldInfoList);
			} else if (node instanceof NameExpr) {
				// 可以解析变量对应的类型，如果它的父类是方法调用，那么间接的可以定位方法所属的类
				resolveNameExpr((NameExpr) node, currentClassInfo, dependencySet, exceptionList, fieldInfoList);
			} else if (node instanceof ClassOrInterfaceType) {
				// 出现了类型引用，即直接声明了某个类，比如 ClassA a = ..., ClassA.class
				// MethodReferenceExpr的子节点TypeExpr中出现的类最终还是会在这里出现
				resolveClassOrInterfaceType((ClassOrInterfaceType) node, dependencySet, exceptionList);
			}
			scanChildNode(node, currentClassInfo, dependencySet, exceptionList, localMethodInfoList, fieldInfoList,
			              rfcMethodQualifiedSignatureMap);
		}
	}


	/**
	 * 方法调用 methodA()
	 * 只能在这里解析调用的方法所属的类，子节点无法解析出包装类
	 * 方法名最终只作为SimpleName节点，
	 * 如果调用的是外部类，最后会出现FieldAccessExpr以及NameExpr，如果没有上述2个，那么一定是类内的非静态方法（也可能是内部类调用外部类的方法）
	 * 如果它的父类是方法调用，那么通过解析方法的返回类型，可以间接的方法所属的类
	 */
	private void resolveMethodCallExpr(
			MethodCallExpr methodCallExpr, ClassInfo currentClassInfo, Set<String> dependencySet,
			List<String> exceptionList, List<MethodInfo> localMethodInfoList,
			Map<String, Integer> rfcMethodQualifiedSignatureMap) {
		try {
			ResolvedMethodDeclaration resolvedMethodDeclaration = methodCallExpr.resolve();
			methodCallOrReference(resolvedMethodDeclaration, currentClassInfo, dependencySet, exceptionList,
			                      localMethodInfoList, rfcMethodQualifiedSignatureMap);
		} catch (UnsolvedSymbolException e) {
			exceptionList.add("When resolving MethodCallExpr: " + e.toString());
			System.out.println("\t" + " -- " + e.getMessage());
			//RFC：无法定位该方法，添加默认计数
			RFCMetric.countRFCMethodQualifiedSignatureMap(rfcMethodQualifiedSignatureMap,
			                                              RFCMetric.UNSOLVED_METHOD_INVOCATION);
		}
	}

	/**
	 * System.out::println，通常作为方法参数或lambda的右侧出现。
	 * 只能在这里解析调用的方法所属的类，子类无法解析包装类
	 */
	private void resolveMethodReferenceExpr(
			MethodReferenceExpr methodReferenceExpr, ClassInfo currentClassInfo, Set<String> dependencySet,
			List<String> exceptionList, List<MethodInfo> localMethodInfoList,
			Map<String, Integer> rfcMethodQualifiedSignatureMap) {
		// System.out::println
		// 只能在这里解析调用的方法所属的类，子类无法解析包装类
		try {
			ResolvedMethodDeclaration resolvedMethodDeclaration = methodReferenceExpr.resolve();
			methodCallOrReference(resolvedMethodDeclaration, currentClassInfo, dependencySet, exceptionList,
			                      localMethodInfoList, rfcMethodQualifiedSignatureMap);
		} catch (UnsolvedSymbolException e) {
			exceptionList.add("When resolving MethodReferenceExpr: " + e.toString());
			System.out.println("\t" + " -- " + e.getMessage());
			//RFC：无法定位该方法，添加默认计数
			RFCMetric.countRFCMethodQualifiedSignatureMap(rfcMethodQualifiedSignatureMap,
			                                              RFCMetric.UNSOLVED_METHOD_INVOCATION);
		}
	}

	/**
	 * ObjectCreationExpr，它的子节点ClassOrInterfaceType是无法解析的(会产生异常)
	 */
	private void resloveObjectCreationExpr(
			ObjectCreationExpr objectCreationExpr, ClassInfo currentClassInfo, Set<String> dependencySet,
			List<String> exceptionList, List<MethodInfo> localMethodInfoList,
			Map<String, Integer> rfcMethodQualifiedSignatureMap) {
		try {
			ResolvedConstructorDeclaration constructorDeclaration = objectCreationExpr.resolve();
			constructorCall(constructorDeclaration, currentClassInfo, dependencySet, exceptionList, localMethodInfoList,
			                rfcMethodQualifiedSignatureMap);
		} catch (UnsolvedSymbolException e) {
			exceptionList.add("When resolving ObjectCreationExpr: " + e.toString());
			System.out.println("\t" + " -- " + e.getMessage());
			RFCMetric.countRFCMethodQualifiedSignatureMap(rfcMethodQualifiedSignatureMap,
			                                              RFCMetric.UNSOLVED_METHOD_INVOCATION);
		}
	}

	/**
	 * ExplicitConstructorInvocationStmt，没有子节点
	 */
	private void resolveExplicitConstructorInvocationStmt(
			ExplicitConstructorInvocationStmt constructorInvocationStmt, ClassInfo currentClassInfo,
			Set<String> dependencySet, List<String> exceptionList, List<MethodInfo> localMethodInfoList,
			Map<String, Integer> rfcMethodQualifiedSignatureMap) {
		try {
			ResolvedConstructorDeclaration constructorDeclaration = constructorInvocationStmt.resolve();
			constructorCall(constructorDeclaration, currentClassInfo, dependencySet, exceptionList, localMethodInfoList,
			                rfcMethodQualifiedSignatureMap);
		} catch (UnsolvedSymbolException e) {
			exceptionList.add("When resolving ExplicitConstructorInvocationStmt: " + e.toString());
			System.out.println("\t" + " -- " + e.getMessage());
			RFCMetric.countRFCMethodQualifiedSignatureMap(rfcMethodQualifiedSignatureMap,
			                                              RFCMetric.UNSOLVED_METHOD_INVOCATION);
		}
	}


	/**
	 * 字段访问,可能会访问到其他类的字段，比如System.out,
	 */
	private void resolveFieldAccessExpr(
			FieldAccessExpr fieldAccessExpr, ClassInfo currentClassInfo, Set<String> dependencySet,
			List<String> exceptionList, List<FieldInfo> fieldInfoList) {
		try {
			ResolvedValueDeclaration valueDeclaration = fieldAccessExpr.resolve();
			ResolvedType resolvedType = valueDeclaration.getType();
			// 依赖
			if (resolvedType.isReferenceType()) {
				// 这里会解析项目内的类。变量则获取声明类型
				String declaredClassName = resolvedType.asReferenceType().getQualifiedName();
				System.out.println("\tFieldAccessExpr: " + declaredClassName);
				dependencySet.add(declaredClassName);
			}
			// 字段访问，用于LOCM
			FieldInfo fieldInfo = EntityUtil.getFieldByShortName(currentClassInfo.getFieldInfoList(),
			                                                     valueDeclaration.getName());
			if (fieldInfo != null) {
				EntityUtil.safeAddEntityToList(fieldInfo, fieldInfoList);
			}

		} catch (UnsolvedSymbolException e) {
			exceptionList.add("When resolving FieldAccessExpr: " + e.toString());
			System.out.println("\t" + " -- " + e.getMessage());
		}
	}

	/**
	 * 可以解析变量对应的类型，如果它的父类是方法调用，那么间接的可以定位方法所属的类
	 */
	private void resolveNameExpr(
			NameExpr nameExpr, ClassInfo currentClassInfo, Set<String> dependencySet, List<String> exceptionList,
			List<FieldInfo> fieldInfoList) {
		try {
			ResolvedValueDeclaration valueDeclaration = nameExpr.resolve();
			//依赖
			ResolvedType resolvedType = valueDeclaration.getType();
			if (resolvedType.isReferenceType()) {
				// 这里会解析项目内的类。变量则获取声明类型
				String declaredClassName = resolvedType.asReferenceType().getQualifiedName();
				System.out.println("\tNameExpr: " + declaredClassName);
				dependencySet.add(declaredClassName);
			}
			// 字段访问，用于LOCM
			if (valueDeclaration.isField()) {
				FieldInfo fieldInfo = EntityUtil.getFieldByShortName(currentClassInfo.getFieldInfoList(),
				                                                     valueDeclaration.getName());
				if (fieldInfo != null) {
					EntityUtil.safeAddEntityToList(fieldInfo, fieldInfoList);
				}
			}
		} catch (UnsolvedSymbolException e) {
			exceptionList.add("When resolving NameExpr: " + e.toString());
			System.out.println("\t" + " -- " + e.getMessage());
		}
	}

	/**
	 * 出现了类型引用，即直接声明了某个类，比如 ClassA a = ..., ClassA.class
	 * MethodReferenceExpr的子节点TypeExpr中出现的类最终还是会在这里出现
	 */
	private void resolveClassOrInterfaceType(
			ClassOrInterfaceType classOrInterfaceType, Set<String> dependencySet, List<String> exceptionList) {
		try {
			String declaredClassName = classOrInterfaceType.resolve().getQualifiedName();
			System.out.println("\tClassOrInterfaceType: " + declaredClassName);
			dependencySet.add(declaredClassName);
		} catch (UnsolvedSymbolException e) {
			exceptionList.add("When resolving ClassOrInterfaceType: " + e.toString());
			System.out.println("\t" + " -- " + e.getMessage());
		}
	}

	/**
	 * 用于处理：方法调用MethodCallExpr，或方法引用MethodReferenceExpr
	 */
	private void methodCallOrReference(
			ResolvedMethodDeclaration resolvedMethodDeclaration, ClassInfo currentClassInfo, Set<String> dependencySet,
			List<String> exceptionList, List<MethodInfo> localMethodInfoList,
			Map<String, Integer> rfcMethodQualifiedSignatureMap) {
		// RFC：唯一定位方法：方法全限定路径+参数
		String methodQualifiedSignature = resolvedMethodDeclaration.getQualifiedSignature();
		RFCMetric.countRFCMethodQualifiedSignatureMap(rfcMethodQualifiedSignatureMap, methodQualifiedSignature);

		// 耦合：找到定义该方法的位置（类）
		String declaringClassName = resolvedMethodDeclaration.declaringType().getQualifiedName();
		dependencySet.add(declaringClassName);

		// 内聚：找到本地方法调用
		if (declaringClassName.equals(currentClassInfo.getFullyQualifiedName())) {
			resolvedMethodDeclaration.toAst().ifPresent(methodDeclaration -> {
				String methodQualifiedName =
						currentClassInfo.getFullyQualifiedName() + "." + methodDeclaration.getNameAsString();
				Range range = methodDeclaration.getRange().orElse(null);
				MethodInfo methodInfo = EntityUtil.getMethodByQualifiedNameAndRangeLocator(
						currentClassInfo.getMethodInfoList(), methodQualifiedName, range);
				EntityUtil.safeAddEntityToList(methodInfo, localMethodInfoList);
			});
		}
		System.out.println("\tMethodCallOrReference-declaring: " + declaringClassName);

		// 耦合找到方法的返回值。链式方法调用中，中间调用的方法的返回值也算依赖
		ResolvedType resolvedType = resolvedMethodDeclaration.getReturnType();
		if (resolvedType.isReferenceType()) {
			// 这里会解析项目内的类。获取返回类型
			String returnClassName = resolvedType.asReferenceType().getQualifiedName();
			System.out.println("\tMethodCallOrReference-return: " + returnClassName);
			dependencySet.add(returnClassName);
		}
	}

	/**
	 * 用于处理：创建对象ObjectCreationExpr，或this,super构造器调用ExplicitConstructorInvocationStmt
	 */
	private void constructorCall(
			ResolvedConstructorDeclaration resolvedConstructorDeclaration, ClassInfo currentClassInfo,
			Set<String> dependencySet, List<String> exceptionList, List<MethodInfo> localMethodInfoList,
			Map<String, Integer> rfcMethodQualifiedSignatureMap) {
		// RFC：唯一定位方法：方法全限定路径+参数
		String methodQualifiedSignature = resolvedConstructorDeclaration.getQualifiedSignature();
		RFCMetric.countRFCMethodQualifiedSignatureMap(rfcMethodQualifiedSignatureMap, methodQualifiedSignature);

		// 耦合：找到定义该构造器的位置（类）
		String declaringClassName = resolvedConstructorDeclaration.declaringType().getQualifiedName();
		dependencySet.add(declaringClassName);

		// 内聚：本地构造器调用
		// 比如this()，或普通构造对象
		// 有时候类没有重写任何构造器（找不到对应的构造器），此时isPresent将不会执行内部的代码
		if (declaringClassName.equals(currentClassInfo.getFullyQualifiedName())) {
			resolvedConstructorDeclaration.toAst().ifPresent(constructorDeclaration -> {
				String methodQualifiedName =
						currentClassInfo.getFullyQualifiedName() + "." + constructorDeclaration.getNameAsString();
				Range range = constructorDeclaration.getRange().orElse(null);
				MethodInfo methodInfo = EntityUtil.getMethodByQualifiedNameAndRangeLocator(
						currentClassInfo.getMethodInfoList(), methodQualifiedName, range);
				EntityUtil.safeAddEntityToList(methodInfo, localMethodInfoList);
			});
		}
	}

}
