package com.t4m.extractor;

import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.util.FileUtil;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.*;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Yuxiang Liao on 2020-06-17 20:17.
 */
public class Test {


	public static CompilationUnit getCompilationUnit(String javaFilePath) {
		char[] charArray = FileUtil.readCharArrayFromJavaSourceFile(javaFilePath);
		ASTParser astParser = ASTParser.newParser(AST.JLS14);
		astParser.setSource(charArray);
		astParser.setKind(ASTParser.K_COMPILATION_UNIT);
		String[] classPaths = System.getProperty("java.class.path").split(":");
		Arrays.stream(classPaths).forEach(System.out::println);
		astParser.setEnvironment(classPaths, new String[]{javaFilePath}, null, false);
		javaFilePath.split(File.separator);
		astParser.setUnitName("/JSimulation/src/main/java/com/simulation/core/foo/ComplexClassA.java");
		astParser.setBindingsRecovery(true);
		astParser.setResolveBindings(true);
		Map<String, String> options = JavaCore.getOptions();
		options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
		options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_8);
		options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
		astParser.setCompilerOptions(options);
		CompilationUnit compilationUnit = (CompilationUnit) astParser.createAST(null);
		for (IProblem problem : compilationUnit.getProblems()) {
			System.out.println("problem message:" + problem.getMessage());
		}
		return compilationUnit;
	}

	public static void main(String[] args) {
		// String[] paths =
		// 		{"/Users/liao/myProjects/IdeaProjects/t4m/t4m-extractor/src/test/resources/JSimulation/src/main/java/com/simulation/core/foo/ComplexClassA.java"
		// 		};
		// Arrays.stream(paths).forEach(path -> {
		// 	CompilationUnit compilationUnit = getCompilationUnit(path);
		// 	compilationUnit.accept(new Test.T4MVisitor());
		// 	System.out.println();
		// });
		// Arrays.stream(currentLine.split("/\\*")).forEach(System.out::println);
		// System.out.println(currentLine.replaceAll("/\\*.*?\\*/",""));
		String string = "asd:asd:";
		System.out.println(string.split(":").length);
		Arrays.stream(string.split(":")).forEach(System.out::println);
		System.out.println("end");
	}


	public static class T4MVisitor extends ASTVisitor {

		// 可能包含类名，或不包含类名：
		// sun.reflect.generics.tree.VoidDescriptor
		// com.refactor.refactor3.price
		private List<ClassInfo> importedClass = new ArrayList<>();
		private List<PackageInfo> importedPackage = new ArrayList<>();


		@Override
		public boolean visit(TypeDeclaration node) {
			List<Modifier> modifiers = node.modifiers();
			System.out.print("Class:\t");
			modifiers.stream().forEach(modifier -> System.out.print(modifier.getKeyword().toString() + " "));
			System.out.println(node.getName() + " ");
			System.out.println("SuperClassType: " + node.getSuperclassType());
			node.superInterfaceTypes().forEach(interf -> {
				System.out.println("Interface: " + interf.toString());
			});

			System.out.println("Num of Fields:\t" + node.getFields().length);
			System.out.println("Num of Methods:\t" + node.getMethods().length);

			System.out.println("Binding" + node.resolveBinding());
			return true;
		}

		@Override
		public boolean visit(SimpleType node) {
			if (node.getParent() instanceof MethodDeclaration) {
				MethodDeclaration methodNode = (MethodDeclaration) node.getParent();
				System.out.println("Return Modifier:\t" + methodNode.getReturnType2().toString() + " <-> " +
						                   node.getName().toString());
			} else {
				// 判断是否存在于importedList列表中
				// 如果在类列表中
				System.out.print("SimpleType:\t" + node.getName().toString());
				System.out.println(",   Parent:\t" + getParentTypeDeclaration(node));
				if (node.getName().toString() == "Car" && getParentTypeDeclaration(node).equals("InnerTestClass")) {
					System.out.println();
				}
			}
			System.out.println("Binding" + node.resolveBinding());
			return super.visit(node);
		}

		/**
		 * 以递归的方式，向上查找所属的类的类名（内部类或外部类）
		 */
		public String getParentTypeDeclaration(ASTNode node) {
			ASTNode parentNode = node.getParent();
			if (parentNode instanceof TypeDeclaration) {
				return ((TypeDeclaration) parentNode).getName().toString();
			} else {
				return getParentTypeDeclaration(parentNode);
			}
		}


		@Override
		public boolean visit(FieldDeclaration fieldDec) {
			//判断是否为全限定名或内部类名

			System.out.print("Field:\t");
			System.out.print(fieldDec.getType() + " ");
			for (Object obj : fieldDec.fragments()) {
				VariableDeclarationFragment v = (VariableDeclarationFragment) obj;
				System.out.print(v.toString());
			}
			System.out.println();
			return true;
		}

		@Override
		public boolean visit(MethodDeclaration node) {
			ASTNode parentNode = node.getParent();
			if (parentNode.getNodeType() == ASTNode.TYPE_DECLARATION) {
				TypeDeclaration typeDec = (TypeDeclaration) parentNode;
				System.out.println("Method:\t" + typeDec.getName() + "." + node.getName());
			} else {
				System.out.println("Method:\t" + node.getName());
			}
			System.out.println("Binding" + node.resolveBinding());
			return super.visit(node);
		}

		@Override
		public boolean visit(MethodInvocation node) {
			System.out.println("MethodInvocation:\t" + node.getName().toString());
			System.out.println("MethodInovcation--expression:\t" + node.getExpression().toString());
			System.out.println("Binding" + node.resolveMethodBinding());
			return true;
		}

	}

}
