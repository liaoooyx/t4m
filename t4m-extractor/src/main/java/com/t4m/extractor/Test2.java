package com.t4m.extractor;

import com.github.javaparser.Range;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
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
import com.t4m.extractor.metric.ComplexityMetric;
import org.checkerframework.checker.units.qual.C;

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