package com.t4m.extractor;

import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.scanner.ast.T4MVisitor;
import com.t4m.extractor.util.JavaFileUtil;
import org.eclipse.jdt.core.dom.*;

import javax.tools.*;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Yuxiang Liao on 2020-06-17 20:17.
 */
public class Test {


	public static CompilationUnit getCompilationUnit(String javaFilePath) {
		char[] charArray = JavaFileUtil.readCharArrayFromJavaSourceFile(javaFilePath);
		ASTParser astParser = ASTParser.newParser(AST.JLS14);
		astParser.setSource(charArray);
		astParser.setKind(ASTParser.K_COMPILATION_UNIT);

		CompilationUnit result = (CompilationUnit) (astParser.createAST(null));

		return result;
	}

	public static void main(String[] args) {
		String[] paths =
				{"/Users/liao/myProjects/IdeaProjects/comp5911m/refactor/refactor-module2/src/main/java/com/refactor/refactor3/TestClass.java",
				 // "/Users/liao/myProjects/IdeaProjects/comp5911m/refactor/refactor-module2/src/main/java/com/refactor/refactor3/TestInterface.java",
				 // "/Users/liao/myProjects/IdeaProjects/comp5911m/refactor/refactor-module2/src/main/java/com/refactor/refactor3/price/StandardPrice.java",
				 "/Users/liao/myProjects/IdeaProjects/comp5911m/refactor/refactor-module2/src/main/java/com/refactor/refactor3/price/Price.java",
				 // "/Users/liao/myProjects/IdeaProjects/comp5911m/refactor/refactor-module2/src/main/java/com/refactor/refactor3/Car.java",
				 // "/Users/liao/myProjects/IdeaProjects/comp5911m/refactor/refactor-module2/src/main/java/com/refactor/refactor3/Customer.java",
				 // "/Users/liao/myProjects/IdeaProjects/comp5911m/refactor/refactor-module2/src/main/java/com/refactor/refactor3/Rental.java",
				 "/Users/liao/myProjects/IdeaProjects/comp5911m/refactor/src/main/java/com/refactor2/StandardPrice.java"
				};
		Arrays.stream(paths).forEach(path -> {
			CompilationUnit compilationUnit = getCompilationUnit(path);
			compilationUnit.accept(new Test.T4MVisitor());
			System.out.println();
		});

	}

	public static class T4MVisitor extends ASTVisitor {

		@Override
		public boolean visit(TypeDeclaration node) {
			List<Modifier> modifiers = node.modifiers();
			modifiers.stream().forEach(modifier -> System.out.print(modifier.getKeyword().toString() + " "));
			System.out.println(node.getName());
			System.out.println("Num of Methods:\t" + node.getMethods().length);
			System.out.println("Num of Fields:\t" + node.getFields().length);
			System.out.println(node.getParent().toString());
			return true;
		}

		@Override
		public boolean visit(MethodDeclaration node) {
			ASTNode parentNode = node.getParent();
			if (parentNode.getNodeType() == ASTNode.TYPE_DECLARATION) {
				TypeDeclaration typeDec = (TypeDeclaration) parentNode;
				System.out.println(typeDec.getName() + "." + node.getName());
			} else {
				System.out.println(node.getName());
			}
			return super.visit(node);
		}
	}

}
