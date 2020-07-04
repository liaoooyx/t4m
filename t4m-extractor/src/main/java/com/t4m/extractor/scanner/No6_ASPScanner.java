package com.t4m.extractor.scanner;

import com.t4m.extractor.T4MExtractor;
import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.scanner.ast.InnerClassVisitor;
import com.t4m.extractor.scanner.ast.T4MVisitor;
import com.t4m.extractor.util.FileUtil;
import com.t4m.extractor.util.PropertyUtil;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by Yuxiang Liao on 2020-06-18 01:12.
 */
public class No6_ASPScanner {

	public static final Logger LOGGER = LoggerFactory.getLogger(No6_ASPScanner.class);

	private static final String TEMP_COMPILE_OUTPUT_PATH = PropertyUtil.getProperty("TEMP_COMPILE_OUTPUT_PATH");

	private ProjectInfo projectInfo;

	public No6_ASPScanner(ProjectInfo projectInfo) {
		this.projectInfo = projectInfo;
	}

	public void scan() {
		scanInnerClass();
		scanMetrics();
	}

	private void scanInnerClass() {
		List<ClassInfo> classInfoList = projectInfo.getClassList();
		for (int i = 0; i < classInfoList.size(); i++) {
			ClassInfo classInfo = classInfoList.get(i);
			CompilationUnit compilationUnit = getCompilationUnit(classInfo.getAbsolutePath());
			InnerClassVisitor innerClassVisitor = new InnerClassVisitor(classInfo, projectInfo);
			compilationUnit.accept(innerClassVisitor);
		}
	}


	private void scanMetrics() {
		List<ClassInfo> classInfoList = projectInfo.getClassList();
		for (int i = 0; i < classInfoList.size(); i++) {
			ClassInfo classInfo = classInfoList.get(i);
			CompilationUnit compilationUnit = getCompilationUnit(classInfo.getAbsolutePath());
			T4MVisitor t4MVisitor = new T4MVisitor(classInfo, projectInfo);
			compilationUnit.accept(t4MVisitor);
		}
	}

	/**
	 * 获取一个类对应的AST（抽象语法树）的编译单元，该单元可视为AST的根节点。 用法1：通过传入一个ASTVisitor，完成对类的所有处理。 用法2：通过AST编译单元，获取类的各个部分。
	 */
	public static CompilationUnit getCompilationUnit(String javaFilePath) {
		char[] charArray = FileUtil.readCharArrayFromJavaSourceFile(javaFilePath);
		ASTParser astParser = ASTParser.newParser(AST.JLS14);
		astParser.setSource(charArray);
		astParser.setKind(ASTParser.K_COMPILATION_UNIT);
		Map<String, String> options = JavaCore.getOptions();
		options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
		options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_8);
		options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
		astParser.setCompilerOptions(options);
		return (CompilationUnit) astParser.createAST(null);
	}

	public static void main(String[] args) {
		String rootPath = "/Users/liao/myProjects/IdeaProjects/jdepend";
		// String rootPath = "/Users/liao/myProjects/IdeaProjects/sonarqube";
		ProjectInfo projectInfo = new ProjectInfo(rootPath);
		T4MExtractor t4MExtractor = new T4MExtractor(projectInfo);
		t4MExtractor.scanDependency();
		No6_ASPScanner aspScanner = new No6_ASPScanner(projectInfo);
		aspScanner.scan();

		System.out.println();
	}
}
