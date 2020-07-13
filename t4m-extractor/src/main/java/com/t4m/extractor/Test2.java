package com.t4m.extractor;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.eclipse.jdt.core.dom.SwitchCase;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by Yuxiang Liao on 2020-07-11 23:42.
 */
public class Test2 {

	private static final String FILE_PATH =
			"/Users/liao/myProjects/IdeaProjects/JSimulationProject/src/main/java/com/simulation/core/xoo/XooClassA.java";

	private static final String SRC_PATH = "/Users/liao/myProjects/IdeaProjects/JSimulationProject/src/main/java/";

	public static void main(String[] args) throws FileNotFoundException {

		TypeSolver typeSolver = new CombinedTypeSolver(new JavaParserTypeSolver(new File(SRC_PATH)));
		JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
		StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);
		CompilationUnit cu = StaticJavaParser.parse(new File(FILE_PATH));
		VoidVisitor<?> methodNameVisitor = new TestVisitor();
		methodNameVisitor.visit(cu, null);
	}

	static class TestVisitor extends VoidVisitorAdapter<Void> {

		@Override
		public void visit(ClassOrInterfaceDeclaration n, Void arg) {
			super.visit(n, arg);
			System.out.println("Class or Interface: " + n.getNameAsString());
		}

		@Override
		public void visit(MethodDeclaration n, Void arg) {
			super.visit(n, arg);
			System.out.println("Method Name Printed: " + n.getNameAsString());
			BlockStmt body = n.getBody().orElse(null);
			if (body != null) {
				int complexityCount = resolveChildClass(body, 1);
				System.out.println();
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
				}
			}
			return complexityCount;
		}

		@Override
		public void visit(MethodCallExpr n, Void arg) {
			super.visit(n, arg);
			System.out.println(n.toString());
			try {
				ResolvedMethodDeclaration invokedMethod = n.resolve();
				System.out.println(n.resolve()
				                    .getQualifiedName()); // 方法全限定名：com.simulation.core.foo.ComplexClassC.initSimpleClassC
				// ResolvedType resolvedReturnType = n.calculateResolvedType();
				// if (resolvedReturnType.isReference()) {
				// 	System.out.println("是引用类型： " + resolvedReturnType.asReferenceType().getQualifiedName());
				// } else {
				// 	System.out.println("不是引用类型：");
				// }
			} catch (UnsolvedSymbolException e) {
				System.out.println("无法解析方法" + e);
			} catch (Exception e) {
				System.out.println(e);
			}

		}


	}

}
