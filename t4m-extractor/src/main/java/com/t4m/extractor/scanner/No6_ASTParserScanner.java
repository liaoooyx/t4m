package com.t4m.extractor.scanner;

import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.scanner.astparser.No1_ClassInfoVisitor;
import com.t4m.extractor.scanner.astparser.No2_MethodAndFieldInfoVisitor;
import com.t4m.extractor.scanner.astparser.No3_MethodDetailVisitor;
import com.t4m.extractor.scanner.astparser.NoX_SLOCVisitor;
import com.t4m.extractor.util.FileUtil;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.IProblem;
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
@Deprecated
public class No6_ASTParserScanner {

	public static final Logger LOGGER = LoggerFactory.getLogger(No6_ASTParserScanner.class);

	private ProjectInfo projectInfo;

	public No6_ASTParserScanner(ProjectInfo projectInfo) {
		this.projectInfo = projectInfo;
	}

	public void scan() {
		scanNestedAndExtraClass();
		// scanMethodAndField();
		// scanMethodDetail();
		// scanSLOC();
	}

	private void scanNestedAndExtraClass() {
		List<ClassInfo> classInfoList = projectInfo.getClassList();
		for (int i = 0; i < classInfoList.size(); i++) {
			ClassInfo classInfo = classInfoList.get(i);
			CompilationUnit compilationUnit = getCompilationUnit(classInfo.getAbsolutePath());
			No1_ClassInfoVisitor innerClassVisitor = new No1_ClassInfoVisitor(classInfo, projectInfo);
			compilationUnit.accept(innerClassVisitor);
		}
	}

	private void scanMethodAndField() {
		List<ClassInfo> classInfoList = projectInfo.getClassList();
		for (int i = 0; i < classInfoList.size(); i++) {
			ClassInfo classInfo = classInfoList.get(i);
			CompilationUnit compilationUnit = getCompilationUnit(classInfo.getAbsolutePath());
			No2_MethodAndFieldInfoVisitor methodAndFieldInfoVisitor = new No2_MethodAndFieldInfoVisitor(classInfo,
			                                                                                            projectInfo);
			compilationUnit.accept(methodAndFieldInfoVisitor);
		}
	}

	private void scanMethodDetail() {
		List<ClassInfo> classInfoList = projectInfo.getClassList();
		for (int i = 0; i < classInfoList.size(); i++) {
			ClassInfo classInfo = classInfoList.get(i);
			CompilationUnit compilationUnit = getCompilationUnit(classInfo.getAbsolutePath());
			No3_MethodDetailVisitor methodDetailVisitor = new No3_MethodDetailVisitor(classInfo, projectInfo);
			compilationUnit.accept(methodDetailVisitor);
		}
	}

	private void scanSLOC() {
		List<ClassInfo> classInfoList = projectInfo.getClassList();
		for (int i = 0; i < classInfoList.size(); i++) {
			ClassInfo classInfo = classInfoList.get(i);
			CompilationUnit compilationUnit = getCompilationUnit(classInfo.getAbsolutePath());
			NoX_SLOCVisitor SLOCVisitor = new NoX_SLOCVisitor(classInfo, projectInfo);
			compilationUnit.accept(SLOCVisitor);
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
		CompilationUnit compilationUnit = (CompilationUnit) astParser.createAST(null);
		for (IProblem problem : compilationUnit.getProblems()) {
			LOGGER.error("CompilationUnit [{}]'s problem" + problem.getMessage(), javaFilePath);
			System.out.println("problem message:" + problem.getMessage());
		}
		return compilationUnit;
	}
}
