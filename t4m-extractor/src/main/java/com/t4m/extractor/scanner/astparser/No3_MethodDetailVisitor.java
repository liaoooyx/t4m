package com.t4m.extractor.scanner.astparser;

import com.t4m.extractor.entity.*;
import com.t4m.extractor.util.ASTParserUtil;
import com.t4m.extractor.util.EntityUtil;
import org.eclipse.jdt.core.dom.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yuxiang Liao on 2020-07-09 01:52.
 */
@Deprecated
public class No3_MethodDetailVisitor extends ASTVisitor {

	private static final Logger LOGGER = LoggerFactory.getLogger(No2_MethodAndFieldInfoVisitor.class);

	private ClassInfo outerClassInfo;
	private ProjectInfo projectInfo;

	// 由 package 和 import 声明的包和类，对应projectInfo中的包和类
	private List<ClassInfo> importedClassList = new ArrayList<>();
	private List<PackageInfo> importedPackageList = new ArrayList<>();

	public No3_MethodDetailVisitor(ClassInfo outerClassInfo, ProjectInfo projectInfo) {
		this.outerClassInfo = outerClassInfo;
		this.projectInfo = projectInfo;
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		ClassInfo currentClassInfo = ASTParserUtil.resolveClassInfo(node, outerClassInfo, projectInfo);
		MethodInfo currentMethodInfo = ASTParserUtil.resolveMethodInfo(node, currentClassInfo);

		List properties = node.structuralPropertiesForType();

		System.out.println(properties);
		return super.visit(node);
	}

	@Override
	public boolean visit(MethodInvocation node) {
		ClassInfo currentClassInfo = ASTParserUtil.resolveClassInfo(node, outerClassInfo, projectInfo);

		// Under MethodDeclaration
		MethodDeclaration methodDeclaration = ASTParserUtil.getParentMethodDeclaration(node);
		if (methodDeclaration != null) {

			// 出现方法调用时的currentMethodInfo
			MethodInfo currentMethodInfo = ASTParserUtil.resolveMethodInfo(methodDeclaration, currentClassInfo);

			// 调用的方法名
			String invockedMethodName = node.getName().getIdentifier();

			// 根据方法名，找到项目中所有匹配的方法列表
			// 方法调用时，1，返回类型不容易推断。2，参数类型不容易推断。3，所属的类，不容易推断。因此按、先获取所有方法列表，提前缩小推断范围
			List<MethodInfo> methodInfoList = EntityUtil.getMethodByShortName(projectInfo.getMethodList(),
			                                                                  invockedMethodName);

			// 再根据方法列表中的方法与节点中获得的信息进行匹配
			if (methodInfoList.size() > 1) {
				//	多个匹配，需要一个个排除

				// 定位方法：判断是调用类本身，还是外部类。
				if (node.getExpression() == null) {
					// node.getExpression()是null，那么说明是调用自身方法
					// 则接下来在判断是哪个类内方法

				} else {
					// node.getExpression()不是null，那么调用 resolveExpressionType()方法，获取表达式中方法对应的类型
					String expressionType = resolveExpressionType(node.getExpression());
					// 如果该方法返回""，说明类是非项目内部的类，因此可以忽略此方法调用。如果有类型，那么则从对应的类型中获取方法
					// EntityUtil.getClassByShortName();
				}


				// 至此已经定位了方法的位置，接下来是定位方法重载。
				// 首先排除参数数量不一致的方法
				// 如果还有多个方法，那么继续解析参数类型
				// 如果参数类型无法解析，那么判断此方法是否属于VariableDeclarationFragment中的INITIALIZER，如果是，则可以找到返回类型

				//	如果还是无法判断，则记录该方法调用的完整语句，并跳过

			} else if (methodInfoList.size() == 1) {
				// 唯一匹配，说明直接找到了目标
			} else {
				//	说明该方法调用不属于调用项目中的类的方法，而是java自带的类，或外部依赖的类。因此可以忽略
			}

			// 如果为paramNameArray[i]=""，则表示paramTypeArray[i]已经提前存入类型，不需要识别
			String[] paramNameArray = new String[node.arguments().size()];// 调用的参数名数组，有序。
			String[] paramTypeArray = new String[node.arguments().size()];
			for (int i = 0; i < paramNameArray.length; i++) {
				if (node.arguments().get(i) instanceof SimpleName) {
					paramNameArray[i] = ((SimpleName) node.arguments().get(i)).getIdentifier();
				} else if (node.arguments().get(i) instanceof StringLiteral) {
					paramNameArray[i] = "";
					paramTypeArray[i] = "String";
				} else if (node.arguments().get(i) instanceof BooleanLiteral) {
					paramNameArray[i] = "";
					paramTypeArray[i] = "boolean";
				} else if (node.arguments().get(i) instanceof NullLiteral) {
					paramNameArray[i] = "";
					paramTypeArray[i] = "null";
				} else if (node.arguments().get(i) instanceof NumberLiteral) {
					paramNameArray[i] = "";
					NumberLiteral numberLiteral = (NumberLiteral) node.arguments().get(i);
					String numStr = numberLiteral.getToken();
					if (numStr.contains(".")) {
						if (numStr.endsWith("f")) {
							paramTypeArray[i] = "float";
						} else {
							paramTypeArray[i] = "double";
						}
					} else {
						if (numStr.endsWith("l")) {
							paramTypeArray[i] = "long";
						} else {
							paramTypeArray[i] = "int";
						}
					}
				} else if (node.arguments().get(i) instanceof CastExpression) {
					paramNameArray[i] = "";
					paramTypeArray[i] = ((CastExpression) node.arguments().get(i)).getType().toString();
				} else if (node.arguments().get(i) instanceof CharacterLiteral) {
					paramNameArray[i] = "";
					paramTypeArray[i] = "char";
				} else if (node.arguments().get(i) instanceof TypeLiteral) {
					paramNameArray[i] = "";
					paramTypeArray[i] = ((TypeLiteral) node.arguments().get(i)).getType().toString();
				} else if (node.arguments().get(i) instanceof LambdaExpression) {
					paramNameArray[i] = "";
					paramTypeArray[i] = "null";
				} else if (node.arguments().get(i) instanceof MethodInvocation) {
					// 嵌套的方法调用的返回类型
				} else if (node.arguments().get(i) instanceof InfixExpression) {
					//	1==1?"":""
				} else {
					LOGGER.error("Unexpected node argument type {}", node.arguments().get(i).toString());
				}
			}

			// 向上检索参数名对应的类型
			for (int i = 0; i < paramNameArray.length; i++) {
				if (!"".equals(paramNameArray[i])) {
					// 查找局部变量
					String typeName = retriveLocalVariableUpwardsByName(paramNameArray[i], node.getParent());

					if (typeName == null) { // null表示局部变量由lambda定义，目前无法解析，因此忽略。
						paramTypeArray[i] = "null";
					} else if ("".equals(typeName)) { //局部变量没找到，从方法参数和全局变量中查找
						typeName = currentMethodInfo.getParamsNameTypeMap().get(paramNameArray[i]);
						if (typeName == null) {
							for (FieldInfo fieldInfo : currentClassInfo.getFieldInfoList()) {
								if (paramNameArray[i].equals(fieldInfo.getShortName())) {
									typeName = fieldInfo.getTypeString();
								}
							}
						}
						if (typeName == null) {
							LOGGER.debug(
									"Cannot resolve the type of local variable [{}] in method [{}], which is not reasonable. Should check the code again.",
									paramNameArray[i], currentMethodInfo.getShortName());
							paramTypeArray[i] = "null";
						}
						paramTypeArray[i] = typeName;
					} else {
						paramTypeArray[i] = typeName;
					}
				}
			}

			// 调用该方法的全限定名

			// 找出方法对应的类

			// 解析出对应的MethodInfo

		}

		return super.visit(node);
	}

	/**
	 * node.getExpression()是null，那么说明是调用自身方法，返回null 如果 node.getExpression()是SimpleName，那么判断该名称是类，还是变量。如果是类，那就找到类下的方法。如果是变量，需要先解析变量为对应的类
	 * 如果node.getExpression()是表达式，如果还是MethodInvocation，那么就进行递归：return methodInvocation.name对应的方法名的返回值
	 */
	private String resolveExpressionType(Expression expression) {

		if (expression == null) {
			return null;
		} else if (expression instanceof SimpleName) {
			//	可能是局部变量，或类名
			SimpleName simpleName = (SimpleName) expression;
			return retriveLocalVariableUpwardsByName(simpleName.getIdentifier(), expression);
		} else if (expression instanceof MethodInvocation) {
			String expressionType = resolveExpressionType(((MethodInvocation) expression).getExpression());
			if (expressionType != null) {
				ClassInfo expressionTypeClass = EntityUtil.getClassByShortName(projectInfo.getAllClassList(),
				                                                          expressionType);
				String methodName = ((MethodInvocation) expression).getName().getIdentifier();
				// MethodInfo methodInfo = EntityUtil.getMethodByShortName(expressionTypeClass.getMethodInfoList(),methodName);
				// 没办法解析，上面这步骤就出现了方法无法定位的问题
				// return methodName;
			} else { // 说明解析不了变量的类型
				//如果不是项目中的类，则返回null
				return null;
			}
		} else {
			LOGGER.error("出现了忽略的情况，需要进行补充");
		}
		return "";
	}

	/**
	 * 获取局部变量对应的类型的字符串，null表示搜索不到
	 */
	private String retriveLocalVariableUpwardsByName(String varName, ASTNode astNode) {
		if (astNode.getNodeType() == ASTNode.BLOCK) {
			Block block = (Block) astNode;
			for (Statement statement : (List<Statement>) block.statements()) {
				if (statement instanceof VariableDeclarationStatement) {
					VariableDeclarationStatement varDeclStatement = (VariableDeclarationStatement) statement;
					for (VariableDeclarationFragment varDeclFragment : (List<VariableDeclarationFragment>) varDeclStatement
							.fragments()) {
						if (varName.equals(varDeclFragment.getName().getIdentifier())) {
							return varDeclStatement.getType().toString();
						}
					}
				}
			}
		} else if (astNode.getNodeType() == ASTNode.ENHANCED_FOR_STATEMENT) {
			SingleVariableDeclaration singleVariableDeclaration = ((EnhancedForStatement) astNode).getParameter();
			if (varName.equals(singleVariableDeclaration.getName().getIdentifier())) {
				return singleVariableDeclaration.getType().toString();
			}
		} else if (astNode.getNodeType() == ASTNode.FOR_STATEMENT) {
			ForStatement forStatement = (ForStatement) astNode;
			for (Object initObj : forStatement.initializers()) {
				if (initObj instanceof VariableDeclarationExpression) {
					VariableDeclarationExpression varDeclExpression = (VariableDeclarationExpression) initObj;
					for (VariableDeclarationFragment varDeclFragment : (List<VariableDeclarationFragment>) varDeclExpression
							.fragments()) {
						if (varName.equals(varDeclFragment.getName().getIdentifier())) {
							return varDeclExpression.getType().toString();
						}
					}
				}
			}
		} else if (astNode.getNodeType() == ASTNode.LAMBDA_EXPRESSION) {
			LambdaExpression lambdaExpression = (LambdaExpression) astNode;
			for (VariableDeclaration variableDeclaration : (List<VariableDeclaration>) lambdaExpression.parameters()) {
				if (variableDeclaration instanceof SingleVariableDeclaration) {
					//	explicit type
					SingleVariableDeclaration singleVariableDeclaration =
							(SingleVariableDeclaration) variableDeclaration;
					if (varName.equals(singleVariableDeclaration.getName().getIdentifier())) {
						return singleVariableDeclaration.getType().toString();
					}
				} else if (variableDeclaration instanceof VariableDeclarationFragment) {
					//	inferred type
					VariableDeclarationFragment variableDeclarationFragment =
							(VariableDeclarationFragment) variableDeclaration;
					if (varName.equals(variableDeclarationFragment.getName().getIdentifier())) {
						// TODO Collection对象的泛型推断的参数，目前没有好的办法获取，因此之间忽略，返回null
						return null;
					}

				}
			}
		} else if (astNode instanceof MethodDeclaration) {
			ClassInfo currentClassInfo = ASTParserUtil.resolveClassInfo(astNode, outerClassInfo, projectInfo);
			for (SingleVariableDeclaration param : (List<SingleVariableDeclaration>) ((MethodDeclaration) astNode)
					.parameters()) {
				String paramName = param.getName().getIdentifier();
				if (varName.equals(paramName)) {
					return param.getType().toString();
				}
			}
			for (FieldInfo fieldInfo : currentClassInfo.getFieldInfoList()) {
				if (varName.equals(fieldInfo.getShortName())) {
					return fieldInfo.getTypeString();
				}
			}
			return null;
		}
		return retriveLocalVariableUpwardsByName(varName, astNode.getParent());
	}
}
