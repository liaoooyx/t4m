package com.t4m.extractor.scanner.javaparser;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithExtends;
import com.github.javaparser.ast.nodeTypes.NodeWithImplements;
import com.github.javaparser.ast.stmt.*;
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
 * 补充基本信息，包括补全类信息，添加MethodInfo，FieldInfo Created by Yuxiang Liao on 2020-07-12 13:38.
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
	 * 补全类信息，包括类标识符：interface, abstract, class，还有继承、实现关系。
	 */
	@Override
	public void visit(ClassOrInterfaceDeclaration n, Void arg) {
		super.visit(n, arg);
		ClassInfo currentClassInfo = JavaParserUtil.resolveCurrentClassInfo(n, projectInfo);
		if (currentClassInfo == null) {
			LOGGER.error("Cannot resolve current class declaration. It may be declared within a method.");
			return;
		}
		if (n.isInterface()) {
			currentClassInfo.setClassModifier(ClassInfo.ClassModifier.INTERFACE);
		} else if (n.isAbstract()) {
			currentClassInfo.setClassModifier(ClassInfo.ClassModifier.ABSTRACT_CLASS);
		} else {
			currentClassInfo.setClassModifier(ClassInfo.ClassModifier.CLASS);
		}
		// 添加继承关系，接口可以多继承
		addExtendsRelationship(n, currentClassInfo);
		// 添加实现关系
		addImplementationRelationship(n, currentClassInfo);

		// SLOC
		countAstSLOCMetaAndAddToClassInfo(n, currentClassInfo);
	}

	/**
	 * 补全类信息，包括类标识符：enum，以及numberOfEnumConstants
	 */
	@Override
	public void visit(EnumDeclaration n, Void arg) {
		super.visit(n, arg);
		ClassInfo currentClassInfo = JavaParserUtil.resolveCurrentClassInfo(n, projectInfo);
		if (currentClassInfo == null) {
			LOGGER.error("Cannot resolve current enum declaration. It may be declared within a method.");
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
		// 实现关系
		addImplementationRelationship(n, currentClassInfo);
		// SLOC
		countAstSLOCMetaAndAddToClassInfo(n, currentClassInfo);
	}

	/**
	 * 补全类信息，包括类标识符：annotation，以及numberOfAnnotationMembers。
	 */
	@Override
	public void visit(AnnotationDeclaration n, Void arg) {
		super.visit(n, arg);
		ClassInfo currentClassInfo = JavaParserUtil.resolveCurrentClassInfo(n, projectInfo);
		if (currentClassInfo == null) {
			LOGGER.error("Cannot resolve current annotation declaration. It may be declared within a method.");
			return;
		}
		currentClassInfo.setClassModifier(ClassInfo.ClassModifier.ANNOTATION);
		currentClassInfo.setNumberOfAnnotationMembers(n.getMembers().size());
		// SLOC
		countAstSLOCMetaAndAddToClassInfo(n, currentClassInfo);
	}

	/**
	 * 构造FieldInfo，并补充ClassInfo中的numberOfFields
	 */
	@Override
	public void visit(FieldDeclaration n, Void arg) {
		super.visit(n, arg);
		ClassInfo currentClassInfo = JavaParserUtil.resolveCurrentClassInfo(n, projectInfo);
		if (currentClassInfo == null) {
			LOGGER.error("Cannot resolve current field declaration. It may be declared within a method.");
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
		JavaParserUtil.addDependency(currentClassInfo, typeAsClassInfoList);
	}

	/**
	 * 构造器与方法声明几乎一样，除了构造器没有返回值，和必然有方法体。
	 */
	@Override
	public void visit(ConstructorDeclaration n, Void arg) {
		ClassInfo currentClassInfo = JavaParserUtil.resolveCurrentClassInfo(n, projectInfo);
		if (currentClassInfo == null) {
			LOGGER.error("Cannot resolve current method declaration. It may be declared in the class within a method.");
			return;
		}
		//构造MethodInfo
		MethodInfo methodInfo = new MethodInfo(n.getNameAsString());
		methodInfo.setReturnTypeString("");
		commonMethodInitOperation(n, methodInfo, currentClassInfo);

		// 添加方法声明出现的依赖关系
		JavaParserUtil.addDependency(currentClassInfo, methodInfo.getReturnTypeAsClassInfoList());
		JavaParserUtil.addDependency(currentClassInfo, methodInfo.getParamsTypeAsClassInfoList());
		JavaParserUtil.addDependency(currentClassInfo, methodInfo.getThrownExceptionClassList());

		BlockStmt body = n.getBody();
		//	解析方法复杂度
		if (body != null) {
			int complexityCount = ComplexityMetric.resolveComplexity(body, 1);
			methodInfo.setCyclomaticComplexity(complexityCount);
			currentClassInfo.getCyclomaticComplexityList().add(complexityCount);
		}
	}

	/**
	 * 构造MethodInfo，并补充ClassInfo中的numberOfMethods，以及添加依赖
	 */
	@Override
	public void visit(MethodDeclaration n, Void arg) {
		ClassInfo currentClassInfo = JavaParserUtil.resolveCurrentClassInfo(n, projectInfo);
		if (currentClassInfo == null) {
			LOGGER.error("Cannot resolve current method declaration. It may be declared in the class within a method.");
			return;
		}
		//构造MethodInfo
		MethodInfo methodInfo = new MethodInfo(n.getNameAsString());
		methodInfo.setReturnTypeString(n.getTypeAsString());
		fillRelevantClassToList(n.getType(), methodInfo.getReturnTypeAsClassInfoList());
		commonMethodInitOperation(n, methodInfo, currentClassInfo);

		// 添加方法声明出现的依赖关系
		JavaParserUtil.addDependency(currentClassInfo, methodInfo.getReturnTypeAsClassInfoList());
		JavaParserUtil.addDependency(currentClassInfo, methodInfo.getParamsTypeAsClassInfoList());
		JavaParserUtil.addDependency(currentClassInfo, methodInfo.getThrownExceptionClassList());

		BlockStmt body = n.getBody().orElse(null);

		//	解析方法复杂度
		if (body != null) {
			int complexityCount = ComplexityMetric.resolveComplexity(body, 1);
			methodInfo.setCyclomaticComplexity(complexityCount);
			currentClassInfo.getCyclomaticComplexityList().add(complexityCount);
		}

	}

	/* -------------------------------------------------------------------------------------------------*/


	/**
	 * 计算并添加类级别的SLOC计数，AST后缀对应的3项。
	 * 通常代码行会变少（因为stmt会合并成一行）
	 * 而注释行可能变多（因为混合行的注释和代码会被差分为多行）
	 * 注意，类外的注释语句可能被忽略（比如在类结束符的的后面的注释）（类的JavaDoc会被保留）
	 */
	private void countAstSLOCMetaAndAddToClassInfo(Node n, ClassInfo currentClassInfo) {
		// LexicalPreservingPrinter.setup(n);
		// LexicalPreservingPrinter.print(n);
		String[] sourceLines = n.toString().split(System.lineSeparator());
		SLOCMetric slocMetric = new SLOCMetric();
		Arrays.stream(sourceLines).forEach(slocMetric::countSLOCByLine);
		slocMetric.setASTSLOCToCounterMap(currentClassInfo.getSlocCounterMap());
	}

	/**
	 * 用于构造器和方法声明visitor中，创建MethodInfo的公共部分
	 */
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

	/**
	 * 添加实现关系
	 */
	private void addImplementationRelationship(NodeWithImplements n, ClassInfo currentClassInfo) {
		// 实现关系
		List<ClassOrInterfaceType> implementedTypes = n.getImplementedTypes();
		for (ClassOrInterfaceType implementedType : implementedTypes) {
			try {
				ClassInfo implementedClass = EntityUtil.getClassByQualifiedName(projectInfo.getAllClassList(),
				                                                                implementedType.resolve()
				                                                                               .getQualifiedName());
				EntityUtil.safeAddEntityToList(implementedClass, currentClassInfo.getImplementedClassList());
			} catch (UnsolvedSymbolException e) {
				//无法解析，说明不是项目内定义的类，使用类名创建单独的的ClassInfo
				EntityUtil.safeAddEntityToList(new ClassInfo(implementedType.getNameAsString(), ""),
				                               currentClassInfo.getImplementedClassList());
			}
		}
	}

	/**
	 * 添加继承关系，接口可以多继承
	 */
	private void addExtendsRelationship(NodeWithExtends n, ClassInfo currentClassInfo) {
		// 实现关系
		List<ClassOrInterfaceType> extendedTypes = n.getExtendedTypes();
		for (ClassOrInterfaceType extendedType : extendedTypes) {
			try {
				ClassInfo extendedClass = EntityUtil.getClassByQualifiedName(projectInfo.getAllClassList(),
				                                                             extendedType.resolve().getQualifiedName());
				EntityUtil.safeAddEntityToList(extendedClass, currentClassInfo.getExtendedClassList());
			} catch (UnsolvedSymbolException e) {
				//无法解析，说明不是项目内定义的类，使用类名创建单独的的ClassInfo
				EntityUtil.safeAddEntityToList(new ClassInfo(extendedType.getNameAsString(), ""),
				                               currentClassInfo.getExtendedClassList());
			}
		}
	}

	/**
	 * 找到该字段涉及到的所有全限定类名（只包括跟项目有关的类）
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
				LOGGER.error("Error occurred when resolving [{}].", node, e.toString());
			}
		}
		for (Node n : node.getChildNodes()) {
			fillRelevantClassToList(n, typeAsClassInfoList);
		}
	}

}
