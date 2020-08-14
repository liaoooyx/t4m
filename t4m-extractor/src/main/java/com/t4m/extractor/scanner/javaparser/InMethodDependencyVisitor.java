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
public class InMethodDependencyVisitor extends VoidVisitorAdapter<Void> {

	private static final Logger LOGGER = LoggerFactory.getLogger(InMethodDependencyVisitor.class);

	private final ClassInfo outerClassInfo;
	private final ProjectInfo projectInfo;

	public InMethodDependencyVisitor(ClassInfo outerClassInfo, ProjectInfo projectInfo) {
		this.outerClassInfo = outerClassInfo;
		this.projectInfo = projectInfo;
	}

	@Override
	public void visit(FieldDeclaration n, Void arg) {
		ClassInfo currentClassInfo = JavaParserUtil.resolveCurrentClassInfo(n, projectInfo);
		if (currentClassInfo == null) {
			LOGGER.debug("Cannot resolve current field declaration.\n{}", n);
			return;
		}
		resolveMetadataForMetric(n, currentClassInfo);
	}

	@Override
	public void visit(BlockStmt n, Void arg) {
		// Only appears the initial block stmt. Method blocks are not include here,
		// as the super.visit(n, arg) in ConstructorDeclaration and MethodDeclaration have been deleted.
		ClassInfo currentClassInfo = JavaParserUtil.resolveCurrentClassInfo(n, projectInfo);
		if (currentClassInfo == null) {
			LOGGER.debug("Cannot resolve current block declaration.\n{}", n);
			return;
		}
		resolveMetadataForMetric(n, currentClassInfo);
	}

	@Override
	public void visit(ConstructorDeclaration n, Void arg) {
		ClassInfo currentClassInfo = JavaParserUtil.resolveCurrentClassInfo(n, projectInfo);
		if (currentClassInfo == null) {
			LOGGER.debug(
					"Cannot resolve current constructor declaration. It may be declared in the class within a method.");
			return;
		}
		MethodInfo currentMethodInfo = EntityUtil.getMethodByQualifiedNameAndRangeLocator(
				currentClassInfo.getMethodInfoList(),
				currentClassInfo.getFullyQualifiedName() + "." + n.getNameAsString(), n.getRange().orElse(null));
		if (currentMethodInfo == null) {
			LOGGER.error(
					"Need to check the code：Cannot resolve current constructor declaration to MethodInfo: [{}] in [{}].",
					n.getDeclarationAsString(), currentClassInfo.getFullyQualifiedName());
			return;
		}
		BlockStmt body = n.getBody();
		if (body != null) {
			resolveMetadataForMetric(body, currentClassInfo, currentMethodInfo);
		}
		// Metadata that relevant to RFC metric.
		try {
			String methodQualifiedSignature = n.resolve().getQualifiedSignature();
			RFCMetric.countRFCMethodQualifiedSignatureMap(currentClassInfo.getLocalMethodCallQualifiedSignatureMap(),
			                                              methodQualifiedSignature);
		} catch (UnsolvedSymbolException e) {
			// For RFC: cannot locate the method precisely.
			// Using the short path method name as signature, may have chance to resolve the overloaded method.
			currentClassInfo.getUnresolvedExceptionList().add("When resolving ConstructorDeclaration: " + e.toString());
			RFCMetric.countRFCMethodQualifiedSignatureMap(currentClassInfo.getLocalMethodCallQualifiedSignatureMap(),
			                                              n.getSignature().toString());
		}
	}

	@Override
	public void visit(MethodDeclaration n, Void arg) {
		ClassInfo currentClassInfo = JavaParserUtil.resolveCurrentClassInfo(n, projectInfo);
		if (currentClassInfo == null) {
			LOGGER.debug("Cannot resolve current method declaration. It may be declared in the class within a method.");
			return;
		}
		MethodInfo currentMethodInfo = EntityUtil.getMethodByQualifiedNameAndRangeLocator(
				currentClassInfo.getMethodInfoList(),
				currentClassInfo.getFullyQualifiedName() + "." + n.getNameAsString(), n.getRange().orElse(null));
		if (currentMethodInfo == null) {
			LOGGER.error(
					"Need to check the code：Cannot resolve current method declaration to MethodInfo: [{}] in [{}].",
					n.getDeclarationAsString(), currentClassInfo.getFullyQualifiedName());
			return;
		}
		n.getBody().ifPresent(body -> resolveMetadataForMetric(body, currentClassInfo, currentMethodInfo));
		// Metadata that relevant to RFC metric.
		try {
			String methodQualifiedSignature = n.resolve().getQualifiedSignature();
			RFCMetric.countRFCMethodQualifiedSignatureMap(currentClassInfo.getLocalMethodCallQualifiedSignatureMap(),
			                                              methodQualifiedSignature);
		} catch (UnsolvedSymbolException e) {
			// For RFC: cannot locate the method precisely.
			// Using the short path method name as signature, may have chance to resolve the overloaded method.
			currentClassInfo.getUnresolvedExceptionList().add("When resolving MethodDeclaration: " + e.toString());
			RFCMetric.countRFCMethodQualifiedSignatureMap(currentClassInfo.getLocalMethodCallQualifiedSignatureMap(),
			                                              n.getSignature().toString());
		}
	}

	/**
	 * Resolve the metadata that produced in the method block
	 */
	private void resolveMetadataForMetric(BlockStmt body, ClassInfo currentClassInfo, MethodInfo currentMethodInfo) {
		// Dependencies occur in the method block.
		Set<String> dependencySet = new HashSet<>();
		// The exceptions that occur when resolving the code. Especially when lack of the jar file paths.
		List<String> exceptionList = currentClassInfo.getUnresolvedExceptionList();
		// LCOM4 metadata: local method invocation
		Set<MethodInfo> localMethodInfoSet = currentMethodInfo.getLocalMethodAccessSet();
		// LCOM4 metadata: local field access
		Set<FieldInfo> fieldInfoSet = currentMethodInfo.getFieldAccessSet();
		// RFC的所有方法全限定签名：只包括方法内的调用
		// RFC metadata: qualified method signatures that invoked in the method blocks.
		Map<String, Integer> rfcMethodQualifiedSignatureMap =
				currentClassInfo.getOutClassMethodCallQualifiedSignatureMap();

		commonOperationToResolveMetaInfo(body, currentClassInfo, dependencySet, exceptionList, localMethodInfoSet,
		                                 fieldInfoSet, rfcMethodQualifiedSignatureMap);
	}

	/**
	 * Resolve the metadata that produced elsewhere except the method blocks.
	 */
	private void resolveMetadataForMetric(Node body, ClassInfo currentClassInfo) {
		// Ignored
		Set<String> dependencySet = new HashSet<>();
		// The exceptions that occur when resolving the code. Especially when lack of the jar file paths.
		List<String> exceptionList = currentClassInfo.getUnresolvedExceptionList();
		// Ignored
		Set<MethodInfo> localMethodInfoSet = new HashSet<>();
		// Ignored
		Set<FieldInfo> fieldInfoSet = new HashSet<>();
		// Ignored
		Map<String, Integer> rfcMethodQualifiedSignatureMap = new HashMap<>();

		commonOperationToResolveMetaInfo(body, currentClassInfo, dependencySet, exceptionList, localMethodInfoSet,
		                                 fieldInfoSet, rfcMethodQualifiedSignatureMap);
	}

	/**
	 * Scan the children nodes and add dependencies after scanning.
	 */
	private void commonOperationToResolveMetaInfo(
			Node body, ClassInfo currentClassInfo, Set<String> dependencySet, List<String> exceptionList,
			Set<MethodInfo> localMethodInfoList, Set<FieldInfo> fieldInfoSet,
			Map<String, Integer> rfcMethodQualifiedSignatureMap) {

		// Scan the children nodes
		scanChildNode(body, currentClassInfo, dependencySet, exceptionList, localMethodInfoList, fieldInfoSet,
		              rfcMethodQualifiedSignatureMap);

		// Add dependencies
		for (String dependencyClassName : dependencySet) {
			ClassInfo referenceClass = EntityUtil.getClassByQualifiedName(projectInfo.getAllClassList(),
			                                                              dependencyClassName);
			if (referenceClass != null) {
				EntityUtil.addDependency(currentClassInfo, referenceClass);
			}
		}
	}

	/**
	 * Recursively scan the child nodes, storing the necessary metadata to the input parameters.
	 */
	private void scanChildNode(
			Node n, ClassInfo currentClassInfo, Set<String> dependencySet, List<String> exceptionList,
			Set<MethodInfo> localMethodInfoSet, Set<FieldInfo> fieldInfoSet,
			Map<String, Integer> rfcMethodQualifiedSignatureMap) {
		if (n.getChildNodes().isEmpty()) {
			return;
		}
		for (Node node : n.getChildNodes()) {
			if (node instanceof MethodCallExpr) {
				resolveMethodCallExpr((MethodCallExpr) node, currentClassInfo, dependencySet, exceptionList,
				                      localMethodInfoSet, rfcMethodQualifiedSignatureMap);
			} else if (node instanceof MethodReferenceExpr) {
				resolveMethodReferenceExpr((MethodReferenceExpr) node, currentClassInfo, dependencySet, exceptionList,
				                           localMethodInfoSet, rfcMethodQualifiedSignatureMap);
			} else if (node instanceof ObjectCreationExpr) {
				resolveObjectCreationExpr((ObjectCreationExpr) node, currentClassInfo, dependencySet, exceptionList,
				                          localMethodInfoSet, rfcMethodQualifiedSignatureMap);
			} else if (node instanceof ExplicitConstructorInvocationStmt) {
				resolveExplicitConstructorInvocationStmt((ExplicitConstructorInvocationStmt) node, currentClassInfo,
				                                         dependencySet, exceptionList, localMethodInfoSet,
				                                         rfcMethodQualifiedSignatureMap);
			} else if (node instanceof FieldAccessExpr) {
				resolveFieldAccessExpr((FieldAccessExpr) node, currentClassInfo, dependencySet, exceptionList,
				                       fieldInfoSet);
			} else if (node instanceof NameExpr) {
				resolveNameExpr((NameExpr) node, currentClassInfo, dependencySet, exceptionList, fieldInfoSet);
			} else if (node instanceof ClassOrInterfaceType) {
				resolveClassOrInterfaceType((ClassOrInterfaceType) node, dependencySet, exceptionList);
			}
			scanChildNode(node, currentClassInfo, dependencySet, exceptionList, localMethodInfoSet, fieldInfoSet,
			              rfcMethodQualifiedSignatureMap);
		}
	}


	/**
	 * Here can only resolve the classes to which the invoked methods belong. It cannot resolve the wrapped classes.
	 * The name of method will be a SimpleName Node at last,
	 * <p>
	 * If the invoked method belongs to other classes, then the node will be FieldAccessExpr or NameExpr at last,
	 * otherwise, the method must be a local method or belongs to the outer class.
	 * <p>
	 * If the previous node is a MethodCallExpr, then we can resolve the return type of previous node.
	 * That return type is the class to which the method of current MethodCallExpr node belongs.
	 */
	private void resolveMethodCallExpr(
			MethodCallExpr methodCallExpr, ClassInfo currentClassInfo, Set<String> dependencySet,
			List<String> exceptionList, Set<MethodInfo> localMethodInfoList,
			Map<String, Integer> rfcMethodQualifiedSignatureMap) {
		try {
			ResolvedMethodDeclaration resolvedMethodDeclaration = methodCallExpr.resolve();
			methodCallOrReference(resolvedMethodDeclaration, currentClassInfo, dependencySet, localMethodInfoList,
			                      rfcMethodQualifiedSignatureMap);
		} catch (UnsolvedSymbolException e) {
			exceptionList.add("When resolving MethodCallExpr: " + e.toString());
			// For RFC: cannot locate the method precisely.
			// Using the short path method name as signature, may have chance to resolve the overloaded method.
			RFCMetric.countRFCMethodQualifiedSignatureMap(rfcMethodQualifiedSignatureMap,
			                                              RFCMetric.UNSOLVED_METHOD_INVOCATION);
			LOGGER.debug("Error occurred when resolving MethodCallExpr: {}", e.toString(), e);
		} catch (Exception e) {
			// For RFC: cannot locate the method precisely.
			// Using the short path method name as signature, may have chance to resolve the overloaded method.
			exceptionList.add("When resolving MethodCallExpr: " + e.toString());
			RFCMetric.countRFCMethodQualifiedSignatureMap(rfcMethodQualifiedSignatureMap,
			                                              RFCMetric.UNSOLVED_METHOD_INVOCATION);
			LOGGER.debug("Unexpected error occurred when resolving MethodCallExpr: {}", e.toString(), e);
		}
	}

	/**
	 * For example, System.out::println,
	 * typically shown as a method parameter or at the right-hand side of the lambda expression.
	 */
	private void resolveMethodReferenceExpr(
			MethodReferenceExpr methodReferenceExpr, ClassInfo currentClassInfo, Set<String> dependencySet,
			List<String> exceptionList, Set<MethodInfo> localMethodInfoList,
			Map<String, Integer> rfcMethodQualifiedSignatureMap) {
		try {
			ResolvedMethodDeclaration resolvedMethodDeclaration = methodReferenceExpr.resolve();
			methodCallOrReference(resolvedMethodDeclaration, currentClassInfo, dependencySet, localMethodInfoList,
			                      rfcMethodQualifiedSignatureMap);
		} catch (UnsolvedSymbolException e) {
			exceptionList.add("When resolving MethodReferenceExpr: " + e.toString());
			// For RFC: cannot locate the method precisely.
			// Using the short path method name as signature, may have chance to resolve the overloaded method.
			RFCMetric.countRFCMethodQualifiedSignatureMap(rfcMethodQualifiedSignatureMap,
			                                              RFCMetric.UNSOLVED_METHOD_INVOCATION);
			LOGGER.debug("Error occurred when resolving MethodReferenceExpr: {}", e.toString(), e);
		} catch (Exception e) {
			// For RFC: cannot locate the method precisely.
			// Using the short path method name as signature, may have chance to resolve the overloaded method.
			exceptionList.add("When resolving MethodReferenceExpr: " + e.toString());
			RFCMetric.countRFCMethodQualifiedSignatureMap(rfcMethodQualifiedSignatureMap,
			                                              RFCMetric.UNSOLVED_METHOD_INVOCATION);
			LOGGER.debug("Unexpected error occurred when resolving MethodReferenceExpr: {}", e.toString(), e);
		}
	}

	private void resolveObjectCreationExpr(
			ObjectCreationExpr objectCreationExpr, ClassInfo currentClassInfo, Set<String> dependencySet,
			List<String> exceptionList, Set<MethodInfo> localMethodInfoList,
			Map<String, Integer> rfcMethodQualifiedSignatureMap) {
		try {
			// Notice The child node of ObjectCreationExpr, ClassOrInterfaceType, is unresolvable.
			ResolvedConstructorDeclaration constructorDeclaration = objectCreationExpr.resolve();
			constructorCall(constructorDeclaration, currentClassInfo, dependencySet, localMethodInfoList,
			                rfcMethodQualifiedSignatureMap);
		} catch (UnsolvedSymbolException e) {
			// For RFC: cannot locate the method precisely.
			// Using the short path method name as signature, may have chance to resolve the overloaded method.
			exceptionList.add("When resolving ObjectCreationExpr: " + e.toString());
			RFCMetric.countRFCMethodQualifiedSignatureMap(rfcMethodQualifiedSignatureMap,
			                                              RFCMetric.UNSOLVED_METHOD_INVOCATION);
			LOGGER.debug("Error occurred when resolving ObjectCreationExpr: {}", e.toString(), e);
		} catch (Exception e) {
			// For RFC: cannot locate the method precisely.
			// Using the short path method name as signature, may have chance to resolve the overloaded method.
			exceptionList.add("When resolving ObjectCreationExpr: " + e.toString());
			RFCMetric.countRFCMethodQualifiedSignatureMap(rfcMethodQualifiedSignatureMap,
			                                              RFCMetric.UNSOLVED_METHOD_INVOCATION);
			LOGGER.debug("Unexpected error occurred when resolving ObjectCreationExpr: {}", e.toString(), e);
		}
	}

	private void resolveExplicitConstructorInvocationStmt(
			ExplicitConstructorInvocationStmt constructorInvocationStmt, ClassInfo currentClassInfo,
			Set<String> dependencySet, List<String> exceptionList, Set<MethodInfo> localMethodInfoList,
			Map<String, Integer> rfcMethodQualifiedSignatureMap) {
		try {
			// ExplicitConstructorInvocationStmt does not have child nodes
			ResolvedConstructorDeclaration constructorDeclaration = constructorInvocationStmt.resolve();
			constructorCall(constructorDeclaration, currentClassInfo, dependencySet, localMethodInfoList,
			                rfcMethodQualifiedSignatureMap);
		} catch (UnsolvedSymbolException e) {
			exceptionList.add("When resolving ExplicitConstructorInvocationStmt: " + e.toString());
			RFCMetric.countRFCMethodQualifiedSignatureMap(rfcMethodQualifiedSignatureMap,
			                                              RFCMetric.UNSOLVED_METHOD_INVOCATION);
			LOGGER.debug("Error occurred when resolving ExplicitConstructorInvocationStmt: {}", e.toString(), e);
		} catch (Exception e) {
			exceptionList.add("When resolving ExplicitConstructorInvocationStmt: " + e.toString());
			RFCMetric.countRFCMethodQualifiedSignatureMap(rfcMethodQualifiedSignatureMap,
			                                              RFCMetric.UNSOLVED_METHOD_INVOCATION);
			LOGGER.debug("Unexpected error occurred when resolving ExplicitConstructorInvocationStmt: {}", e.toString(),
			             e);
		}
	}

	/**
	 * Field access. May be accessing the field of other classes, such as System.out
	 */
	private void resolveFieldAccessExpr(
			FieldAccessExpr fieldAccessExpr, ClassInfo currentClassInfo, Set<String> dependencySet,
			List<String> exceptionList, Set<FieldInfo> fieldInfoSet) {
		try {
			ResolvedValueDeclaration valueDeclaration = fieldAccessExpr.resolve();
			ResolvedType resolvedType = valueDeclaration.getType();
			if (resolvedType.isReferenceType()) {
				String declaredClassName = resolvedType.asReferenceType().getQualifiedName();
				dependencySet.add(declaredClassName);
			}
			FieldInfo fieldInfo = EntityUtil.getFieldByShortName(currentClassInfo.getFieldInfoList(),
			                                                     valueDeclaration.getName());
			if (fieldInfo != null) {
				fieldInfoSet.add(fieldInfo);
			}

		} catch (UnsolvedSymbolException e) {
			exceptionList.add("When resolving FieldAccessExpr: " + e.toString());
			LOGGER.debug("Error occurred when resolving FieldAccessExpr: {}", e.toString(), e);
		} catch (Exception e) {
			exceptionList.add("When resolving FieldAccessExpr: " + e.toString());
			LOGGER.debug("Unexpected error occurred when resolving FieldAccessExpr: {}", e.toString(), e);
		}
	}

	/**
	 * Here can resolve the corresponding type of the variable.
	 * If the previous node is a method call, it can also locate the class to which the method belongs
	 */
	private void resolveNameExpr(
			NameExpr nameExpr, ClassInfo currentClassInfo, Set<String> dependencySet, List<String> exceptionList,
			Set<FieldInfo> fieldInfoSet) {
		try {
			ResolvedValueDeclaration valueDeclaration = nameExpr.resolve();
			ResolvedType resolvedType = valueDeclaration.getType();
			if (resolvedType.isReferenceType()) {
				String declaredClassName = resolvedType.asReferenceType().getQualifiedName();
				dependencySet.add(declaredClassName);
			}
			if (valueDeclaration.isField()) {
				FieldInfo fieldInfo = EntityUtil.getFieldByShortName(currentClassInfo.getFieldInfoList(),
				                                                     valueDeclaration.getName());
				if (fieldInfo != null) {
					fieldInfoSet.add(fieldInfo);
				}
			}
		} catch (UnsolvedSymbolException e) {
			exceptionList.add("When resolving NameExpr: " + e.toString());
			LOGGER.debug("Error occurred when resolving NameExpr: {}", e.toString(), e);
		} catch (Exception e) {
			exceptionList.add("When resolving NameExpr: " + e.toString());
			LOGGER.debug("Unexpected error occurred when resolving NameExpr: {}", e.toString(), e);
		}
	}

	/**
	 * For example, ClassA.class or ClassA a.
	 *
	 * The class that appears in the child class of MethodReferenceExpr, TypeExpr, will be resolved here at last.
	 */
	private void resolveClassOrInterfaceType(
			ClassOrInterfaceType classOrInterfaceType, Set<String> dependencySet, List<String> exceptionList) {
		try {
			String declaredClassName = classOrInterfaceType.resolve().getQualifiedName();
			dependencySet.add(declaredClassName);
		} catch (UnsolvedSymbolException e) {
			exceptionList.add("When resolving ClassOrInterfaceType: " + e.toString());
			LOGGER.debug("Error occurred when resolving ClassOrInterfaceType: {}", e.toString(), e);
		} catch (Exception e) {
			exceptionList.add("When resolving ClassOrInterfaceType: " + e.toString());
			LOGGER.debug("Unexpected error occurred when resolving ClassOrInterfaceType: {}", e.toString(), e);
		}
	}

	/**
	 * To deal with MethodCallExpr or MethodReferenceExpr
	 */
	private void methodCallOrReference(
			ResolvedMethodDeclaration resolvedMethodDeclaration, ClassInfo currentClassInfo, Set<String> dependencySet,
			Set<MethodInfo> localMethodInfoList, Map<String, Integer> rfcMethodQualifiedSignatureMap) {
		// Locate the method for RFC
		String methodQualifiedSignature = resolvedMethodDeclaration.getQualifiedSignature();
		RFCMetric.countRFCMethodQualifiedSignatureMap(rfcMethodQualifiedSignatureMap, methodQualifiedSignature);

		// Locate the class to which the methods belongs for Coupling
		String declaringClassName = resolvedMethodDeclaration.declaringType().getQualifiedName();
		dependencySet.add(declaringClassName);

		// Find out all local method calls for Cohesion
		if (declaringClassName.equals(currentClassInfo.getFullyQualifiedName())) {
			resolvedMethodDeclaration.toAst().ifPresent(methodDeclaration -> {
				String methodQualifiedName =
						currentClassInfo.getFullyQualifiedName() + "." + methodDeclaration.getNameAsString();
				Range range = methodDeclaration.getRange().orElse(null);
				MethodInfo methodInfo = EntityUtil.getMethodByQualifiedNameAndRangeLocator(
						currentClassInfo.getMethodInfoList(), methodQualifiedName, range);
				localMethodInfoList.add(methodInfo);
			});
		}

		// Find out the return type of methods.
		// In the chain of method call, the return types of the intermediate method calls are also considered as dependencies.
		ResolvedType resolvedType = resolvedMethodDeclaration.getReturnType();
		if (resolvedType.isReferenceType()) {
			// Only take the ClassInfo objects into account.
			String returnClassName = resolvedType.asReferenceType().getQualifiedName();
			dependencySet.add(returnClassName);
		}
	}

	/**
	 * To deal with objectCreationExpr or ExplicitConstructorInvocationStmt (this and super keywords)
	 */
	private void constructorCall(
			ResolvedConstructorDeclaration resolvedConstructorDeclaration, ClassInfo currentClassInfo,
			Set<String> dependencySet, Set<MethodInfo> localMethodInfoList,
			Map<String, Integer> rfcMethodQualifiedSignatureMap) {
		// Locate the method for RFC
		String methodQualifiedSignature = resolvedConstructorDeclaration.getQualifiedSignature();
		RFCMetric.countRFCMethodQualifiedSignatureMap(rfcMethodQualifiedSignatureMap, methodQualifiedSignature);

		// Locate the class to which the methods belongs for Coupling
		String declaringClassName = resolvedConstructorDeclaration.declaringType().getQualifiedName();
		dependencySet.add(declaringClassName);

		// For Cohesion: local constructor invocation. For example, this() and normal constructor.
		if (declaringClassName.equals(currentClassInfo.getFullyQualifiedName())) {
			// If there is no declared constructor, isPresent will not be executed.
			resolvedConstructorDeclaration.toAst().ifPresent(constructorDeclaration -> {
				String methodQualifiedName =
						currentClassInfo.getFullyQualifiedName() + "." + constructorDeclaration.getNameAsString();
				Range range = constructorDeclaration.getRange().orElse(null);
				MethodInfo methodInfo = EntityUtil.getMethodByQualifiedNameAndRangeLocator(
						currentClassInfo.getMethodInfoList(), methodQualifiedName, range);
				localMethodInfoList.add(methodInfo);
			});
		}
	}

}
