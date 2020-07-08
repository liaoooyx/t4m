package com.t4m.extractor.scanner.ast;

import com.t4m.extractor.entity.*;
import com.t4m.extractor.util.ASTVisitorUtil;
import com.t4m.extractor.util.EntityUtil;
import org.eclipse.jdt.core.dom.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yuxiang Liao on 2020-07-07 23:22.
 */
public class CreateMethodAndFieldInfoVisitor extends ASTVisitor {

	private static final Logger LOGGER = LoggerFactory.getLogger(CreateMethodAndFieldInfoVisitor.class);

	private ClassInfo outerClassInfo;
	private ProjectInfo projectInfo;
	private List<ClassInfo> extraClassInfoList = new ArrayList<>();

	// 由 package 和 import 声明的包和类，对应projectInfo中的包和类
	private List<ClassInfo> importedClassList = new ArrayList<>();
	private List<PackageInfo> importedPackageList = new ArrayList<>();

	public CreateMethodAndFieldInfoVisitor(ClassInfo outerClassInfo, ProjectInfo projectInfo) {
		this.outerClassInfo = outerClassInfo;
		this.projectInfo = projectInfo;
	}

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
		ClassInfo currentClassInfo = ASTVisitorUtil.resolveClassInfo(node, outerClassInfo, projectInfo);
		// 异常日志
		Type type = node.getType();
		if (node.fragments().size() > 1) {
			LOGGER.error("Unexpected FieldDeclaration fragment size [{}]", node.fragments().size());
			for (Object obj : node.fragments()) {
				LOGGER.error(obj.toString());
			}
		}
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
		}
		return true;
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		ClassInfo currentClassInfo = ASTVisitorUtil.resolveClassInfo(node, outerClassInfo, projectInfo);

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
		for (Object paramObj : node.parameters()) {
			SingleVariableDeclaration param = (SingleVariableDeclaration) paramObj;
			paramsNameTypeMap.put(param.getName().getIdentifier(), param.getType().toString());
			fillRelevantClassInfoToList(param.getType(), methodInfo.getParamsTypeAsClassInfoList());
		}
		methodInfo.setParamsNameTypeMap(paramsNameTypeMap);

		EntityUtil.safeAddEntityToList(methodInfo, currentClassInfo.getMethodInfoList());
		return true;
	}

	/**
	 * 将于字段的类型中出现的，与项目有关的类，加入到列表中。目前是直接累加在传入的列表中，并返回该列表，而不是创建一个新的列表。
	 */
	public List<ClassInfo> fillRelevantClassInfoToList(Type type, List<ClassInfo> qualifiedNameList) {
		if (type.isSimpleType()) { // 退出递归的条件
			ClassInfo fieldTypeClassInfo;
			SimpleType simpleType = (SimpleType) type;
			// 可能是简单类名，也可能是外部类+嵌套类名，也可能是自己的嵌套类名。外部类名可能是全限定类名。需要解析
			String unIdentifiedTypeName = simpleType.getName().getFullyQualifiedName();
			fieldTypeClassInfo = resolveUnidentifiedNameToClassInfo(unIdentifiedTypeName);
			if (fieldTypeClassInfo != null) {
				qualifiedNameList.add(fieldTypeClassInfo);
			}
			return qualifiedNameList;
		} else if (type.isArrayType()) {
			// 数组
			ArrayType arrayType = (ArrayType) type;
			Type newType = arrayType.getElementType();
			fillRelevantClassInfoToList(newType, qualifiedNameList);
		} else if (type.isParameterizedType()) {
			// List Map Set ...
			ParameterizedType parameterizedType = (ParameterizedType) type;
			for (Object newType : parameterizedType.typeArguments()) {
				fillRelevantClassInfoToList((Type) newType, qualifiedNameList);
			}
		} else if (type.isPrimitiveType()) {
			//原始类型 PrimitiveType of FieldType. Skip
		} else {
			System.err.println("Unexpected FieldType occur: " + type.toString());
			LOGGER.error("Unexpected FieldType occur: {}", type.toString());
		}
		return qualifiedNameList;
	}

	/**
	 * AST获得的字段类型为字符串格式，此方法将其转换为ClassInfo格式
	 */
	public ClassInfo resolveUnidentifiedNameToClassInfo(String unIdentifiedTypeName) {
		ClassInfo fieldTypeClassInfo;
		if (!unIdentifiedTypeName.contains(".")) {
			// shortName
			fieldTypeClassInfo = ASTVisitorUtil.findClassInfoFromImportedListByShortName(unIdentifiedTypeName,
			                                                                             importedClassList,
			                                                                             importedPackageList);
		} else {
			// 直接搜，是否为全限定外部类名
			fieldTypeClassInfo = ASTVisitorUtil.findClassInfoFromImportedListByQualifiedName(unIdentifiedTypeName,
			                                                                                 importedClassList,
			                                                                                 importedPackageList);
			// 如果不是，则
			if (fieldTypeClassInfo == null) {
				// 将最后一个.转为$，搜索是否为外部类$嵌套类名
				unIdentifiedTypeName = ASTVisitorUtil.transferQualifiedName(unIdentifiedTypeName);
				if (unIdentifiedTypeName.contains(".")) {
					//	转换后包括.，说明是qualifiedName，可能是合法的com.OuterClass$InnerClass，或不合法的com.foo$OuterClass
					fieldTypeClassInfo = ASTVisitorUtil.findClassInfoFromImportedListByQualifiedName(
							unIdentifiedTypeName, importedClassList, importedPackageList);
				} else {
					//	转换后不包括.，说明是shortName，可能是合法的OuterClass$InnerClass，或不合法的com$OuterClass
					fieldTypeClassInfo = ASTVisitorUtil.findClassInfoFromImportedListByShortName(unIdentifiedTypeName,
					                                                                             importedClassList,
					                                                                             importedPackageList);
				}
			}
		}
		return fieldTypeClassInfo;
	}


}
