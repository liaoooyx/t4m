package com.t4m.extractor;

import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.util.JavaFileUtil;
import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
		astParser.setEnvironment(null, new String[]{
				                         "/Users/liao/myProjects/IdeaProjects/comp5911m/refactor/refactor-module2/src/main/java/com/refactor/refactor3/"},
		                         new String[]{"UTF-8"}, true);
		astParser.setKind(ASTParser.K_COMPILATION_UNIT);

		CompilationUnit result = (CompilationUnit) (astParser.createAST(null));

		return result;
	}

	public static void main(String[] args) {
		String[] paths =
				{"/Users/liao/myProjects/IdeaProjects/JSimulationProject/src/main/java/com/simulation/foo/ComplexClassA.java",
				// "/Users/liao/myProjects/IdeaProjects/comp5911m/refactor/refactor-module2/src/main/java/com/refactor/refactor3/TestClass.java",
				 // "/Users/liao/myProjects/IdeaProjects/comp5911m/refactor/refactor-module2/src/main/java/com/refactor/refactor3/TestInterface.java",
				 // "/Users/liao/myProjects/IdeaProjects/comp5911m/refactor/refactor-module2/src/main/java/com/refactor/refactor3/price/StandardPrice.java",
				 // "/Users/liao/myProjects/IdeaProjects/comp5911m/refactor/refactor-module2/src/main/java/com/refactor/refactor3/price/Price.java",
				 // "/Users/liao/myProjects/IdeaProjects/comp5911m/refactor/refactor-module2/src/main/java/com/refactor/refactor3/Car.java",
				 // "/Users/liao/myProjects/IdeaProjects/comp5911m/refactor/refactor-module2/src/main/java/com/refactor/refactor3/Customer.java",
				 // "/Users/liao/myProjects/IdeaProjects/comp5911m/refactor/refactor-module2/src/main/java/com/refactor/refactor3/Rental.java",
				 // "/Users/liao/myProjects/IdeaProjects/comp5911m/refactor/src/main/java/com/refactor2/StandardPrice.java"
				};
		Arrays.stream(paths).forEach(path -> {
			CompilationUnit compilationUnit = getCompilationUnit(path);
			compilationUnit.accept(new Test.T4MVisitor());
			System.out.println();
		});

	}

	public static class T4MVisitor extends ASTVisitor {

		// 可能包含类名，或不包含类名：
		// sun.reflect.generics.tree.VoidDescriptor
		// com.refactor.refactor3.price
		private List<ClassInfo> importedClass = new ArrayList<>();
		private List<PackageInfo> importedPackage = new ArrayList<>();

		@Override
		public boolean visit(PackageDeclaration node) {
			System.out.println("current package:\t" + node.getName().getFullyQualifiedName());
			return true;
		}

		@Override
		public boolean visit(ImportDeclaration node) {
			// import 直接表明了不同包之间的依赖关系，但包内类的依赖关系需要用其他方法。
			// 但引入的类可能属于项目外的Jar包，因此需要过滤方式。

			//判断引入的是包还是类
			// 先检索是否为包
			//如果不是在检索是否为类
			//当出现多个同名时，打印日志
			System.out.println("imported package:\t" + node.getName().getFullyQualifiedName());
			return true;
		}

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

			System.out.println("Num of Fields:\t"+node.getFields().length);
			System.out.println("Num of Methods:\t"+node.getMethods().length);

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

			return super.visit(node);
		}

		@Override
		public boolean visit(QualifiedName node) {
			System.out.println(
					"QualifiedName:\t" + node.getFullyQualifiedName() + ",\tgetQualifier:\t" + node.getQualifier() +
							",\tgetName:\t" + node.getName());
			return super.visit(node);
		}

		@Override
		public boolean visit(MethodInvocation node) {
			System.out.println("MethodInvocation:\t" + node.getName().toString());
			System.out.println("MethodInovcation--expression:\t" + node.getExpression().toString());
			return true;
		}

	}

}
