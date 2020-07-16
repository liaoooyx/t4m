package com.t4m.extractor.scanner.astparser;

import com.t4m.extractor.entity.*;
import com.t4m.extractor.util.ASTParserUtil;
import com.t4m.extractor.util.EntityUtil;
import org.eclipse.jdt.core.dom.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于构造MethodInfo和FieldInfo Created by Yuxiang Liao on 2020-07-07 23:22.
 */
@Deprecated
public class No2_MethodAndFieldInfoVisitor extends ASTVisitor {

	private static final Logger LOGGER = LoggerFactory.getLogger(No2_MethodAndFieldInfoVisitor.class);

	private ClassInfo outerClassInfo;
	private ProjectInfo projectInfo;

	// 由 package 和 import 声明的包和类，对应projectInfo中的包和类
	private List<ClassInfo> importedClassList = new ArrayList<>();
	private List<PackageInfo> importedPackageList = new ArrayList<>();

	public No2_MethodAndFieldInfoVisitor(ClassInfo outerClassInfo, ProjectInfo projectInfo) {
		this.outerClassInfo = outerClassInfo;
		this.projectInfo = projectInfo;
	}

	/**
	 * 将字段的类型中出现的与项目有关的类，加入到列表中。目前是直接累加在传入的列表中，并返回该列表，而不是创建一个新的列表。
	 */
	public List<ClassInfo> fillRelevantClassInfoToList(Type type, List<ClassInfo> relevantClassInfoList) {
		if (type.isSimpleType()) { // 退出递归的条件
			ClassInfo fieldTypeClassInfo;
			SimpleType simpleType = (SimpleType) type;
			// 可能是简单类名，也可能是外部类+嵌套类名，也可能是自己的嵌套类名。外部类名可能是全限定类名。需要解析
			String unIdentifiedTypeName = simpleType.getName().getFullyQualifiedName();
			fieldTypeClassInfo = resolveUnidentifiedNameToClassInfo(unIdentifiedTypeName);
			if (fieldTypeClassInfo != null) {
				relevantClassInfoList.add(fieldTypeClassInfo);
			}
			return relevantClassInfoList;
		} else if (type.isArrayType()) {
			// 数组
			ArrayType arrayType = (ArrayType) type;
			Type newType = arrayType.getElementType();
			fillRelevantClassInfoToList(newType, relevantClassInfoList);
		} else if (type.isParameterizedType()) {
			// List Map Set ...
			ParameterizedType parameterizedType = (ParameterizedType) type;
			for (Object newType : parameterizedType.typeArguments()) {
				fillRelevantClassInfoToList((Type) newType, relevantClassInfoList);
			}
		} else if (type.isPrimitiveType()) {
			//原始类型 PrimitiveType of FieldType. Skip
		} else {
			System.err.println("Unexpected Type occur: " + type.toString());
			LOGGER.error("Unexpected Type occur: {}", type.toString());
		}
		return relevantClassInfoList;
	}


	/**
	 * ASTNode获得的字段类型为字符串格式，可能是简单类名，也可能是外部类+嵌套类名，也可能是自己的嵌套类名。外部类名可能是全限定类名。 此方法将其转换为ClassInfo格式
	 */
	public ClassInfo resolveUnidentifiedNameToClassInfo(String unIdentifiedTypeName) {
		ClassInfo fieldTypeClassInfo;
		if (!unIdentifiedTypeName.contains(".")) {
			// shortName
			fieldTypeClassInfo = ASTParserUtil.findClassInfoFromImportedListByShortName(unIdentifiedTypeName,
			                                                                            importedClassList,
			                                                                            importedPackageList);
		} else {
			// 直接搜，是否为全限定类名
			fieldTypeClassInfo = ASTParserUtil.findClassInfoFromImportedListByQualifiedName(unIdentifiedTypeName,
			                                                                                importedClassList,
			                                                                                importedPackageList);
			// 如果不是，则为：外部类.内部类
			if (fieldTypeClassInfo == null) {
				String[] names = unIdentifiedTypeName.split(".");
				String shortName = names[names.length - 1];
				fieldTypeClassInfo = ASTParserUtil.findClassInfoFromImportedListByShortName(shortName,
				                                                                            importedClassList,
				                                                                            importedPackageList);
			}
		}
		return fieldTypeClassInfo;
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
	public boolean visit(FieldDeclaration node) {
		ClassInfo currentClassInfo = ASTParserUtil.resolveClassInfo(node, outerClassInfo, projectInfo);

		Type type = node.getType();
		// 构造FieldInfo
		for (Object fragemtObj : node.fragments()) {
			VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragemtObj;
			String varName = fragment.getName().getIdentifier();
			String typeString = type.toString();
			FieldInfo fieldInfo = new FieldInfo(varName, typeString);
			fillRelevantClassInfoToList(type, fieldInfo.getTypeAsClassInfoList());
			for (Object modifierObj : node.modifiers()) {
				IExtendedModifier ieModifier = (IExtendedModifier) modifierObj;
				if (ieModifier.isModifier()) {
					Modifier modifier = (Modifier) ieModifier;
					if (modifier.getKeyword().toFlagValue() == Modifier.STATIC) {
						fieldInfo.setStatic(true);
					} else if (modifier.getKeyword().toFlagValue() == Modifier.FINAL) {
						fieldInfo.setFinal(true);
					} else if (modifier.getKeyword().toFlagValue() == Modifier.PUBLIC) {
						fieldInfo.setAccessModifierEnum(AccessModifierEnum.PUBLIC);
					} else if (modifier.getKeyword().toFlagValue() == Modifier.PRIVATE) {
						fieldInfo.setAccessModifierEnum(AccessModifierEnum.PRIVATE);
					} else if (modifier.getKeyword().toFlagValue() == Modifier.PROTECTED) {
						fieldInfo.setAccessModifierEnum(AccessModifierEnum.PROTECTED);
					}
				}
			}
			EntityUtil.safeAddEntityToList(fieldInfo, currentClassInfo.getFieldInfoList());

			// 添加依赖关系
			for (ClassInfo depedency : fieldInfo.getTypeAsClassInfoList()) {
				EntityUtil.safeAddEntityToList(depedency, currentClassInfo.getActiveDependencyAkaFanOutList());
				EntityUtil.safeAddEntityToList(currentClassInfo, depedency.getPassiveDependencyAkaFanInList());
			}
		}


		return true;
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		ClassInfo currentClassInfo = ASTParserUtil.resolveClassInfo(node, outerClassInfo, projectInfo);

		String methodName = node.getName().getIdentifier();
		MethodInfo methodInfo = new MethodInfo(methodName);
		methodInfo.setFullyQualifiedName(currentClassInfo.getFullyQualifiedName() + "#" + methodName);

		for (Object modifierObj : node.modifiers()) {
			IExtendedModifier ieModifier = (IExtendedModifier) modifierObj;
			if (ieModifier.isModifier()) {
				Modifier modifier = (Modifier) ieModifier;
				if (modifier.getKeyword().toFlagValue() == Modifier.STATIC) {
					methodInfo.setStaticMethod(true);
				} else if (modifier.getKeyword().toFlagValue() == Modifier.ABSTRACT) {
					methodInfo.setAbstractMethod(true);
				} else if (modifier.getKeyword().toFlagValue() == Modifier.PUBLIC) {
					methodInfo.setAccessModifierEnum(AccessModifierEnum.PUBLIC);
				} else if (modifier.getKeyword().toFlagValue() == Modifier.PRIVATE) {
					methodInfo.setAccessModifierEnum(AccessModifierEnum.PRIVATE);
				} else if (modifier.getKeyword().toFlagValue() == Modifier.PROTECTED) {
					methodInfo.setAccessModifierEnum(AccessModifierEnum.PROTECTED);
				}
			}
		}

		Type returnType = node.getReturnType2();
		if (returnType != null) {
			methodInfo.setReturnTypeString(returnType.toString());
			fillRelevantClassInfoToList(returnType, methodInfo.getReturnTypeAsClassInfoList());
		}

		Map<String, String> paramsNameTypeMap = new LinkedHashMap<>();
		Map<String, List<ClassInfo>> paramsAsClassInfoMap = methodInfo.getParamsTypeAsClassInfoListMap();
		for (Object paramObj : node.parameters()) {
			SingleVariableDeclaration param = (SingleVariableDeclaration) paramObj;
			String paramName = param.getName().getIdentifier();
			paramsNameTypeMap.put(paramName, param.getType().toString());
			paramsAsClassInfoMap.put(paramName, new ArrayList<>());
			fillRelevantClassInfoToList(param.getType(), paramsAsClassInfoMap.get(paramName));
		}
		methodInfo.setParamsNameTypeMap(paramsNameTypeMap);
		methodInfo.setParamsTypeAsClassInfoListMap(paramsAsClassInfoMap);

		methodInfo.setClassInfo(currentClassInfo);
		EntityUtil.safeAddEntityToList(methodInfo, projectInfo.getMethodList());

		EntityUtil.safeAddEntityToList(methodInfo, currentClassInfo.getMethodInfoList());

		// 添加依赖关系
		for (ClassInfo depedency : methodInfo.getReturnTypeAsClassInfoList()) {
			EntityUtil.safeAddEntityToList(depedency, currentClassInfo.getActiveDependencyAkaFanOutList());
			EntityUtil.safeAddEntityToList(currentClassInfo, depedency.getPassiveDependencyAkaFanInList());
		}
		// 添加依赖关系
		for (List<ClassInfo> tempList : methodInfo.getParamsTypeAsClassInfoListMap().values()) {
			for (ClassInfo depedency : tempList) {
				EntityUtil.safeAddEntityToList(depedency, currentClassInfo.getActiveDependencyAkaFanOutList());
				EntityUtil.safeAddEntityToList(currentClassInfo, depedency.getPassiveDependencyAkaFanInList());
			}
		}
		return true;
	}

	// @Override
	// public boolean visit(VariableDeclarationStatement node) {
	// 	ClassInfo currentClassInfo = ASTVisitorUtil.resolveClassInfo(node, outerClassInfo, projectInfo);
	// 	// 任何地方出现了VariableDeclarationStatement，只要声明的变量是项目的类，都表示耦合
	// 	// 注意 new ComplexClassB().new InnerClassC();会出现单独出现内部类名的情况。
	// 	List<ClassInfo> relevantClassInfoList = new ArrayList<>();
	// 	fillRelevantClassInfoToList(node.getType(), relevantClassInfoList);
	// 	// 添加依赖关系
	// 	for (ClassInfo typeClassInfo : relevantClassInfoList) {
	// 		EntityUtil.safeAddEntityToList(typeClassInfo, currentClassInfo.getActiveDependencyAkaFanOutList());
	// 		EntityUtil.safeAddEntityToList(currentClassInfo, typeClassInfo.getPassiveDependencyAkaFanInList());
	// 	}
	// 	return true;
	// }

	// @Override
	// public boolean visit(MethodInvocation node) {
	// 	// 静态方法调用需要想办法解决识别类
	// 	// 普通方法调用无所谓，因为需要操作依赖时，必定要声明对象，否则无法操作，如果是通过方法操作的，那也只是依赖于方法所属的类
	// 	// Class.InnerClass, com.foo.Class, com.foo.Class.InnnerClass
	// 	// 下面步骤只适用于通过类之间调用静态方法；如果通过对象调用静态方法，那么默认该对象之前已经被声明过
	// 	if (node.getExpression() != null) {
	// 		String qualifiedNameAheadMethodName = node.getExpression().toString();
	// 		ClassInfo staticInvokingClassInfo = retriveClassInfoInQualifiedName(qualifiedNameAheadMethodName);
	// 		// 判断内部类或外部类
	// 		ClassInfo currentClassInfo = resolveClassInfo(node);
	// 		// 添加依赖关系
	// 		if (staticInvokingClassInfo != null) {
	// 			EntityUtil.safeAddEntityToList(staticInvokingClassInfo,
	// 			                               currentClassInfo.getActiveDependencyAkaFanOutList());
	// 			EntityUtil.safeAddEntityToList(currentClassInfo,
	// 			                               staticInvokingClassInfo.getPassiveDependencyAkaFanInList());
	// 		}
	// 	}
	// 	return true;
	// }
}
