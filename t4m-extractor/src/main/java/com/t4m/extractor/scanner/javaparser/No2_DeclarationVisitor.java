package com.t4m.extractor.scanner.javaparser;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.nodeTypes.NodeWithExtends;
import com.github.javaparser.ast.nodeTypes.NodeWithImplements;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.t4m.extractor.entity.*;
import com.t4m.extractor.metric.ComplexityMetric;
import com.t4m.extractor.metric.SLOCMetric;
import com.t4m.extractor.util.EntityUtil;
import com.t4m.extractor.util.JavaParserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Set the rest info for the classInfo object.
 * Create MethodInfo and FieldInfo entities.
 *
 * Created by Yuxiang Liao on 2020-07-12 13:38.
 */
public class No2_DeclarationVisitor extends VoidVisitorAdapter<Void> {

	private static final Logger LOGGER = LoggerFactory.getLogger(No2_DeclarationVisitor.class);

	private ClassInfo outerClassInfo;
	private ProjectInfo projectInfo;

	public No2_DeclarationVisitor(ClassInfo outerClassInfo, ProjectInfo projectInfo) {
		this.outerClassInfo = outerClassInfo;
		this.projectInfo = projectInfo;
	}

	/**
	 * Set class modifier, inheritance relationship and relevant dependencies, and SLOC metadata.
	 */
	@Override
	public void visit(ClassOrInterfaceDeclaration n, Void arg) {
		super.visit(n, arg);
		ClassInfo currentClassInfo = JavaParserUtil.resolveCurrentClassInfo(n, projectInfo);
		if (currentClassInfo == null) {
			LOGGER.debug("Cannot resolve current class declaration. It may be declared within a method.\n{}", n);
			return;
		}
		if (n.isInterface()) {
			currentClassInfo.setClassModifier(ClassInfo.ClassModifier.INTERFACE);
		} else if (n.isAbstract()) {
			currentClassInfo.setClassModifier(ClassInfo.ClassModifier.ABSTRACT_CLASS);
		} else {
			currentClassInfo.setClassModifier(ClassInfo.ClassModifier.CLASS);
		}
		addExtendsRelationship(n, currentClassInfo);
		addImplementationRelationship(n, currentClassInfo);
		countAstSLOCMetaAndAddToClassInfo(n, currentClassInfo);
	}

	/**
	 * Set class modifier, inheritance relationship and relevant dependencies, and SLOC metadata.
	 */
	@Override
	public void visit(EnumDeclaration n, Void arg) {
		super.visit(n, arg);
		ClassInfo currentClassInfo = JavaParserUtil.resolveCurrentClassInfo(n, projectInfo);
		if (currentClassInfo == null) {
			LOGGER.debug("Cannot resolve current enum declaration. It may be declared within a method.\n", n);
			return;
		}
		currentClassInfo.setClassModifier(ClassInfo.ClassModifier.ENUM);
		int numOfEnumConstant = 0;
		for (Node node : n.getChildNodes()) {
			if (node instanceof EnumConstantDeclaration) {
				numOfEnumConstant++;
			}
		}
		currentClassInfo.setNumberOfEnumConstants(numOfEnumConstant);
		addImplementationRelationship(n, currentClassInfo);
		countAstSLOCMetaAndAddToClassInfo(n, currentClassInfo);
	}

	/**
	 * Set class modifier, and SLOC metadata.
	 */
	@Override
	public void visit(AnnotationDeclaration n, Void arg) {
		super.visit(n, arg);
		ClassInfo currentClassInfo = JavaParserUtil.resolveCurrentClassInfo(n, projectInfo);
		if (currentClassInfo == null) {
			LOGGER.debug("Cannot resolve current annotation declaration. It may be declared within a method.\n{}", n);
			return;
		}
		currentClassInfo.setClassModifier(ClassInfo.ClassModifier.ANNOTATION);
		currentClassInfo.setNumberOfAnnotationMembers(n.getMembers().size());
		countAstSLOCMetaAndAddToClassInfo(n, currentClassInfo);
	}

	/**
	 * Construct FieldInfo entities, as well as resolving relevant dependencies.
	 */
	@Override
	public void visit(FieldDeclaration n, Void arg) {
		super.visit(n, arg);
		ClassInfo currentClassInfo = JavaParserUtil.resolveCurrentClassInfo(n, projectInfo);
		if (currentClassInfo == null) {
			LOGGER.debug("Cannot resolve current field declaration. It may be declared within a method.\n{}", n);
			return;
		}
		boolean isStatic = false;
		boolean isFinal = false;
		AccessModifierEnum accessModifierEnum = AccessModifierEnum.DEFAULT;
		for (Modifier modifier : n.getModifiers()) {
			switch (modifier.getKeyword()) {
				case FINAL:
					isFinal = true;
					break;
				case STATIC:
					isStatic = true;
					break;
				case PUBLIC:
					accessModifierEnum = AccessModifierEnum.PUBLIC;
					break;
				case PROTECTED:
					accessModifierEnum = AccessModifierEnum.PROTECTED;
					break;
				case PRIVATE:
					accessModifierEnum = AccessModifierEnum.PRIVATE;
					break;
				default: // do nothing
					break;
			}
		}
		List<ClassInfo> typeAsClassInfoList = new ArrayList<>();
		fillRelevantClassToList(n.getElementType(), typeAsClassInfoList);
		for (VariableDeclarator variableDeclarator : n.getVariables()) {
			FieldInfo fieldInfo = new FieldInfo(variableDeclarator.getNameAsString(),
			                                    variableDeclarator.getTypeAsString());
			fieldInfo.setFinal(isFinal);
			fieldInfo.setStatic(isStatic);
			fieldInfo.setAccessModifierEnum(accessModifierEnum);
			fieldInfo.setTypeAsClassInfoList(typeAsClassInfoList);
			fieldInfo.setRangeLocator(variableDeclarator.getRange().orElse(null));
			EntityUtil.safeAddEntityToList(fieldInfo, currentClassInfo.getFieldInfoList());
		}

		currentClassInfo.setNumberOfFields(currentClassInfo.getFieldInfoList().size());
		EntityUtil.addDependency(currentClassInfo, typeAsClassInfoList);
	}

	/**
	 * Construct MethodInfo entity，as well as resolving relevant dependencies and the complexity metadata.
	 * Recall that a constructor does not have return keyword but must be a method body.
	 */
	@Override
	public void visit(ConstructorDeclaration n, Void arg) {
		ClassInfo currentClassInfo = JavaParserUtil.resolveCurrentClassInfo(n, projectInfo);
		if (currentClassInfo == null) {
			LOGGER.debug(
					"Cannot resolve current method declaration. It may be declared in the class within a method.\n{}",
					n);
			return;
		}
		// Construct MethodInfo
		MethodInfo methodInfo = new MethodInfo(n.getNameAsString());
		methodInfo.setReturnTypeString("");
		commonMethodInitOperation(n, methodInfo, currentClassInfo);

		EntityUtil.addDependency(currentClassInfo, methodInfo.getReturnTypeAsClassInfoList());
		EntityUtil.addDependency(currentClassInfo, methodInfo.getParamsTypeAsClassInfoList());
		EntityUtil.addDependency(currentClassInfo, methodInfo.getThrownExceptionClassList());

		BlockStmt body = n.getBody();
		//	Complexity metadata
		if (body != null) {
			int complexityCount = ComplexityMetric.resolveComplexity(body, 1);
			methodInfo.setCyclomaticComplexity(complexityCount);
			currentClassInfo.getCyclomaticComplexityList().add(complexityCount);
		}
	}

	/**
	 * Construct MethodInfo entity，as well as resolving relevant dependencies and the complexity metadata.
	 */
	@Override
	public void visit(MethodDeclaration n, Void arg) {
		ClassInfo currentClassInfo = JavaParserUtil.resolveCurrentClassInfo(n, projectInfo);
		if (currentClassInfo == null) {
			LOGGER.debug(
					"Cannot resolve current method declaration. It may be declared in the class within a method.\n{}",
					n);
			return;
		}
		MethodInfo methodInfo = new MethodInfo(n.getNameAsString());
		methodInfo.setReturnTypeString(n.getTypeAsString());
		fillRelevantClassToList(n.getType(), methodInfo.getReturnTypeAsClassInfoList());
		commonMethodInitOperation(n, methodInfo, currentClassInfo);

		EntityUtil.addDependency(currentClassInfo, methodInfo.getReturnTypeAsClassInfoList());
		EntityUtil.addDependency(currentClassInfo, methodInfo.getParamsTypeAsClassInfoList());
		EntityUtil.addDependency(currentClassInfo, methodInfo.getThrownExceptionClassList());

		BlockStmt body = n.getBody().orElse(null);

		// Complexity metadata
		if (body != null) {
			int complexityCount = ComplexityMetric.resolveComplexity(body, 1);
			methodInfo.setCyclomaticComplexity(complexityCount);
			currentClassInfo.getCyclomaticComplexityList().add(complexityCount);
		}

	}

	/* -------------------------------------------------------------------------------------------------*/


	/**
	 * Calculate the SLOC in class (AST) level.
	 * Normally, the number of code lines will decrease as the cross-lines stmt will be combined in one line,
	 * while the number of comment lines will increase as the mixed comment lines will be separated to different lines.
	 * Notice that the comment outside a class may be ignored (JavaDoc will be kept).
	 */
	private void countAstSLOCMetaAndAddToClassInfo(Node n, ClassInfo currentClassInfo) {
		// LexicalPreservingPrinter.setup(n);
		// LexicalPreservingPrinter.print(n);
		String[] sourceLines = n.toString().split(System.lineSeparator());
		SLOCMetric.SLOCCounter slocCounter = new SLOCMetric.SLOCCounter();
		Arrays.stream(sourceLines).forEach(slocCounter::countSLOCByLine);
		slocCounter.setASTSLOCToCounterMap(currentClassInfo.getSlocCounterMap());
	}

	private void commonMethodInitOperation(CallableDeclaration n, MethodInfo methodInfo, ClassInfo currentClassInfo) {
		methodInfo.setMethodDeclarationString(n.getDeclarationAsString());
		methodInfo.setRangeLocator(n.getRange().orElse(null));
		methodInfo.setFullyQualifiedName(currentClassInfo.getFullyQualifiedName() + "." + n.getNameAsString());
		methodInfo.setClassInfo(currentClassInfo);

		Map<String, String> paramStrMap = methodInfo.getParamsNameTypeMap();
		Map<String, List<ClassInfo>> paramClassMap = methodInfo.getParamsTypeAsClassInfoListMap();
		for (Parameter parameter : (NodeList<Parameter>) n.getParameters()) {
			String paramName = parameter.getName().toString();
			paramStrMap.put(paramName, parameter.getType().toString());
			List<ClassInfo> couplingClassList = new ArrayList<>();
			fillRelevantClassToList(parameter, couplingClassList);
			if (!couplingClassList.isEmpty()) {
				paramClassMap.put(paramName, couplingClassList);
			}
		}

		List<String> thrownExceptionStringList = methodInfo.getThrownExceptionStringList(); // 异常类型字符串
		List<ClassInfo> thrownExceptionClassList = methodInfo.getThrownExceptionClassList();
		for (ReferenceType referenceType : (NodeList<ReferenceType>) n.getThrownExceptions()) {
			thrownExceptionStringList.add(referenceType.toString());
			fillRelevantClassToList(referenceType, thrownExceptionClassList);
		}

		EntityUtil.safeAddEntityToList(methodInfo, currentClassInfo.getMethodInfoList());
		EntityUtil.safeAddEntityToList(methodInfo, projectInfo.getMethodList());
		currentClassInfo.setNumberOfMethods(currentClassInfo.getMethodInfoList().size());
	}

	private void addImplementationRelationship(NodeWithImplements n, ClassInfo currentClassInfo) {
		// 实现关系
		List<ClassOrInterfaceType> implementedTypes = n.getImplementedTypes();
		for (ClassOrInterfaceType implementedType : implementedTypes) {
			try {
				ClassInfo implementedClass = EntityUtil.getClassByQualifiedName(projectInfo.getAllClassList(),
				                                                                implementedType.resolve()
				                                                                               .getQualifiedName());
				EntityUtil.safeAddEntityToList(implementedClass, currentClassInfo.getImplementsClassList());
			} catch (UnsolvedSymbolException e) {
				//无法解析，说明不是项目内定义的类，使用类名创建单独的的ClassInfo
				EntityUtil.safeAddEntityToList(new ClassInfo(implementedType.getNameAsString(), ""),
				                               currentClassInfo.getImplementsClassList());
			}
		}
	}

	private void addExtendsRelationship(NodeWithExtends n, ClassInfo currentClassInfo) {
		// 实现关系
		List<ClassOrInterfaceType> extendedTypes = n.getExtendedTypes();
		for (ClassOrInterfaceType extendedType : extendedTypes) {
			try {
				ClassInfo extendedClass = EntityUtil.getClassByQualifiedName(projectInfo.getAllClassList(),
				                                                             extendedType.resolve().getQualifiedName());
				if (extendedClass == null) {
					ClassInfo unknownClassInfo = new ClassInfo(extendedType.getNameAsString(), "");
					EntityUtil.safeAddEntityToList(unknownClassInfo, currentClassInfo.getExtendsClassList());
					EntityUtil.safeAddEntityToList(currentClassInfo, unknownClassInfo.getImmediateSubClassList());
				} else {
					EntityUtil.safeAddEntityToList(extendedClass, currentClassInfo.getExtendsClassList());
					EntityUtil.safeAddEntityToList(currentClassInfo, extendedClass.getImmediateSubClassList());
				}
			} catch (UnsolvedSymbolException e) {
				//无法解析，说明不是项目内定义的类，使用类名创建单独的的ClassInfo
				ClassInfo unknownClassInfo = new ClassInfo(extendedType.getNameAsString(), "");
				EntityUtil.safeAddEntityToList(unknownClassInfo, currentClassInfo.getExtendsClassList());
				EntityUtil.safeAddEntityToList(currentClassInfo, unknownClassInfo.getImmediateSubClassList());
			}
		}
	}

	/**
	 * Find out the relevant ClassInfo objects that involved in this Node.
	 */
	private void fillRelevantClassToList(Node node, List<ClassInfo> typeAsClassInfoList) {
		if (node instanceof Type && ((Type) node).isReferenceType()) {
			try {
				String qualifiedName = ((Type) node).asReferenceType().resolve().describe();
				ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getAllClassList(), qualifiedName);
				if (classInfo != null) {
					EntityUtil.safeAddEntityToList(classInfo, typeAsClassInfoList);
				}
			} catch (UnsolvedSymbolException e) {
				// 如果不是项目本身的类，则直接忽略，不需要添加进列表中
			} catch (UnsupportedOperationException e) {
				LOGGER.debug("Error occurred when resolving [{}]. {}", node, e.toString(), e);
			}
		}
		for (Node n : node.getChildNodes()) {
			fillRelevantClassToList(n, typeAsClassInfoList);
		}
	}

}
