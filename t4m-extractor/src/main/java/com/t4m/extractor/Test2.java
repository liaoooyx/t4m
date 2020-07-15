package com.t4m.extractor;

import com.github.javaparser.Range;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Yuxiang Liao on 2020-07-11 23:42.
 */
public class Test2 {

	private static final String FILE_PATH =
			"/Users/liao/myProjects/IdeaProjects/JSimulationProject/src/main/java/com/simulation/core/foo/ComplexClassA.java";

	private static final String SRC_PATH = "/Users/liao/myProjects/IdeaProjects/JSimulationProject/src/main/java/";

	public static void main(String[] args) throws FileNotFoundException {

		TypeSolver typeSolver = new CombinedTypeSolver(new ReflectionTypeSolver(),
		                                               new JavaParserTypeSolver(new File(SRC_PATH)));
		JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
		StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);
		CompilationUnit cu = StaticJavaParser.parse(new File(FILE_PATH));
		VoidVisitor<?> methodNameVisitor = new TestVisitor();
		methodNameVisitor.visit(cu, null);
	}

	static class TestVisitor extends VoidVisitorAdapter<Void> {

		@Override
		public void visit(CompilationUnit n, Void arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(MethodDeclaration n, Void arg) {
			System.out.println("Method Name Printed: " + n.getNameAsString());
			BlockStmt body = n.getBody().orElse(null);
			if (body != null) {
				Set<String> dependencySet = new HashSet<>();
				List<String> exceptionList = new ArrayList<>();
				// 所有方法调用的shortName，以及，需要补充currentClassInfo全限定名，再查找是否为类内方法
				List<String> localMethodCallNameList = new ArrayList<>();
				List<Range> localMethodCallRangeList = new ArrayList<>();
				List<String> fieldNameList = new ArrayList<>(); //Range可能为null
				resolveDependency(body, dependencySet, exceptionList, localMethodCallNameList, localMethodCallRangeList,
				                  fieldNameList);
				System.out.println(dependencySet);
			}
		}

		private void resolveDependency(
				Node n, Set<String> dependencySet, List<String> exceptionList, List<String> localMethodCallNameList,
				List<Range> localMethodCallRangeList, List<String> fieldNameList) {
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
					try {
						MethodCallExpr methodCallExpr = (MethodCallExpr) node;

						// 依赖
						String declaringClassName = methodCallExpr.resolve().declaringType().getQualifiedName();
						dependencySet.add(declaringClassName);

						// 本地方法调用
						methodCallExpr.resolve().toAst().ifPresent(methodDeclaration -> {
							//todo 直接解析出MethodInfo，存入列表
							localMethodCallNameList.add(methodDeclaration.getNameAsString());
							localMethodCallRangeList.add(methodDeclaration.getRange().orElse(null));
						});

						System.out.println("\tMethodCallExpr-declaring: " + declaringClassName);
					} catch (UnsolvedSymbolException e) {
						exceptionList.add("When resolving MethodCallExpr-declaring: " + e.toString());
						System.out.println("\t" + " -- " + e.getMessage());
						((MethodCallExpr) node).getTypeArguments().get();
						((MethodCallExpr) node).getArguments();
						((MethodCallExpr) node).getName().getIdentifier();
						((MethodCallExpr) node).getScope().get();
					}
					try {
						ResolvedType resolvedType = ((MethodCallExpr) node).resolve().getReturnType();
						if (resolvedType.isReferenceType()) {
							// 这里会解析项目内的类。获取返回类型
							String returnClassName = resolvedType.asReferenceType().getQualifiedName();
							System.out.println("\tMethodCallExpr-return: " + returnClassName);
							dependencySet.add(returnClassName);
						}
					} catch (UnsolvedSymbolException e) {
						exceptionList.add("When resolving MethodCallExpr-return: " + e.toString());
						System.out.println("\t" + " -- " + e.getMessage());
					}
				}
				if (node instanceof ObjectCreationExpr) {
					// ObjectCreationExpr，它的子节点ClassOrInterfaceType是无法解析的
					try {
						String declaringClassName =
								((ObjectCreationExpr) node).resolve().declaringType().getQualifiedName();
						System.out.println("\tObjectCreationExpr: " + declaringClassName);
						dependencySet.add(declaringClassName);
					} catch (UnsolvedSymbolException e) {
						exceptionList.add("When resolving ObjectCreationExpr: " + e.toString());
						System.out.println("\t" + " -- " + e.getMessage());
					}
				} else if (node instanceof MethodReferenceExpr) {
					// System.out::println
					// 只能在这里解析调用的方法所属的类，子类无法解析包装类
					try {
						String declaringClassName =
								((MethodReferenceExpr) node).resolve().declaringType().getQualifiedName();
						System.out.println("\tFieldAccessExpr: " + declaringClassName);
						dependencySet.add(declaringClassName);
					} catch (UnsolvedSymbolException e) {
						exceptionList.add("When resolving FieldAccessExpr: " + e.toString());
						System.out.println("\t" + " -- " + e.getMessage());
					}

				} else if (node instanceof FieldAccessExpr) {
					// 访问类的字段，因此可以获取字段的类型，如果它的父类是方法调用，那么间接的可以定位方法所属的类
					try {
						ResolvedValueDeclaration valueDeclaration = ((FieldAccessExpr) node).resolve();
						ResolvedType resolvedType = valueDeclaration.getType();
						// 依赖
						if (resolvedType.isReferenceType()) {
							// 这里会解析项目内的类。变量则获取声明类型
							String declaredClassName = resolvedType.asReferenceType().getQualifiedName();
							System.out.println("\tFieldAccessExpr: " + declaredClassName);
							dependencySet.add(declaredClassName);
						}
						// 字段访问
						fieldNameList.add(valueDeclaration.getName());

					} catch (UnsolvedSymbolException e) {
						exceptionList.add("When resolving FieldAccessExpr: " + e.toString());
						System.out.println("\t" + " -- " + e.getMessage());
					}

				} else if (node instanceof NameExpr) {
					// 可以解析变量对应的类型，如果它的父类是方法调用，那么间接的可以定位方法所属的类
					try {
						ResolvedValueDeclaration valueDeclaration = ((NameExpr) node).resolve();
						//依赖
						ResolvedType resolvedType = valueDeclaration.getType();
						if (resolvedType.isReferenceType()) {
							// 这里会解析项目内的类。变量则获取声明类型
							String declaredClassName = resolvedType.asReferenceType().getQualifiedName();
							System.out.println("\tNameExpr: " + declaredClassName);
							dependencySet.add(declaredClassName);
						}
						// 字段访问
						if (valueDeclaration.isField()) {
							fieldNameList.add(valueDeclaration.asField().getName());
						}
					} catch (UnsolvedSymbolException e) {
						exceptionList.add("When resolving NameExpr: " + e.toString());
						System.out.println("\t" + " -- " + e.getMessage());
					}
				} else if (node instanceof ClassOrInterfaceType) {
					// 出现了类型引用，即直接声明了某个类，比如 ClassA a = ..., ClassA.class
					// MethodReferenceExpr的子节点TypeExpr中出现的类最终还是会在这里出现
					try {
						String declaredClassName = ((ClassOrInterfaceType) node).resolve().getQualifiedName();
						System.out.println("\tClassOrInterfaceType: " + declaredClassName);
						dependencySet.add(declaredClassName);
					} catch (UnsolvedSymbolException e) {
						exceptionList.add("When resolving ClassOrInterfaceType: " + e.toString());
						System.out.println("\t" + " -- " + e.getMessage());
					}
				}
				resolveDependency(node, dependencySet, exceptionList, localMethodCallNameList, localMethodCallRangeList,
				                  fieldNameList);
			}
		}

		private int resolveChildClass(Node n, int complexityCount) {
			for (Node node : n.getChildNodes()) {
				complexityCount += resolveChildClass(node, 0);
				if (node instanceof DoStmt) {
					complexityCount += 1;
				} else if (node instanceof WhileStmt) {
					complexityCount += 1;
				} else if (node instanceof ForEachStmt) {
					complexityCount += 1;
				} else if (node instanceof ForStmt) {
					complexityCount += 1;
				} else if (node instanceof IfStmt) {
					complexityCount += 1;
				} else if (node instanceof SwitchEntry) {
					complexityCount +=
							((SwitchEntry) node).getLabels().isEmpty() ? 1 : ((SwitchEntry) node).getLabels().size();
				} else if (node instanceof CatchClause) {
					complexityCount += 1;
				} else if (node instanceof BinaryExpr) {
					BinaryExpr.Operator operator = ((BinaryExpr) node).getOperator();
					if (operator.equals(BinaryExpr.Operator.AND) || operator.equals(BinaryExpr.Operator.OR)) {
						complexityCount += 1;
					}
				} else if (node instanceof ConditionalExpr) {
					//The ternary conditional expression: b==0?x:y
					complexityCount += 1;
				}
			}
			return complexityCount;
		}

		@Override
		public void visit(MethodCallExpr n, Void arg) {
			super.visit(n, arg);
		}
	}
}
