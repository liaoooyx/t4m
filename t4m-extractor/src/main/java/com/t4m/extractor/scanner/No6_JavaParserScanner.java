package com.t4m.extractor.scanner;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.t4m.extractor.T4MExtractor;
import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.ModuleInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.scanner.javaparser.No1_ClassInfoVisitor;
import com.t4m.extractor.scanner.javaparser.No2_DeclarationVisitor;
import com.t4m.extractor.scanner.javaparser.No3_InMethodDependencyVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yuxiang Liao on 2020-07-11 11:00.
 */
public class No6_JavaParserScanner {
	public static final Logger LOGGER = LoggerFactory.getLogger(No6_ASTParserScanner.class);

	private final ProjectInfo projectInfo;

	public No6_JavaParserScanner(ProjectInfo projectInfo) {
		this.projectInfo = projectInfo;
	}

	public void scan() {
		initParser();
		scanVisitor(No1_ClassInfoVisitor.class);
		scanVisitor(No2_DeclarationVisitor.class);
		scanVisitor(No3_InMethodDependencyVisitor.class);
	}


	private void scanVisitor(Class<? extends VoidVisitor> visitorClass) {
		for (ClassInfo classInfo : projectInfo.getClassList()) {
			try {
				CompilationUnit cu = StaticJavaParser.parse(new File(classInfo.getAbsolutePath()));
				VoidVisitor<?> methodNameVisitor = visitorClass.getConstructor(ClassInfo.class, ProjectInfo.class)
				                                               .newInstance(classInfo, projectInfo);
				methodNameVisitor.visit(cu, null);
			} catch (FileNotFoundException e) {
				LOGGER.error("找不到文件", e);
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				LOGGER.error("无法实例化Visitor", e);
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	public void initParser() {
		List<TypeSolver> typeSolverList = new ArrayList<>();
		// ReflectionTypeSolver用于解析Java核心类
		typeSolverList.add(new ReflectionTypeSolver());
		for (ModuleInfo moduleInfo : projectInfo.getModuleList()) {
			//JavaParserTypeSolver要求是是根包所在文件夹位置
			typeSolverList.add(new JavaParserTypeSolver(new File(moduleInfo.getSourcePath())));
		}
		TypeSolver typeSolver = new CombinedTypeSolver(typeSolverList.toArray(new TypeSolver[0]));
		JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
		StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);
	}

	public static void main(String[] args) {
		// String rootPath = "/Users/liao/myProjects/IdeaProjects/jdepend";
		// String rootPath = "/Users/liao/myProjects/IdeaProjects/sonarqube";
		String rootPath = "/Users/liao/myProjects/IdeaProjects/JSimulationProject";
		ProjectInfo projectInfo = new ProjectInfo(rootPath);
		T4MExtractor t4MExtractor = new T4MExtractor(projectInfo);
		t4MExtractor.scanJavaParser();
		System.out.println();
	}
}
