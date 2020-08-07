package com.t4m.extractor;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.t4m.extractor.metric.ComplexityMetric;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by Yuxiang Liao on 2020-07-11 23:42.
 */
public class Test2 {

	private static final String FILE_PATH =
			"/Users/liao/myProjects/IdeaProjects/JSimulationProject/src/main/java/com/simulation/core/xoo/CyclomaticComplexityClass.java";

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
			ComplexityMetric complexityMetric = new ComplexityMetric();
			int i = complexityMetric.resolveComplexity(n,0);
			System.out.println();
		}

	}
}