package com.t4m.extractor.scanner;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.t4m.conf.GlobalProperties;
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
import java.io.IOException;
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
			//JavaParserTypeSolver要求是根包所在文件夹位置
			typeSolverList.add(new JavaParserTypeSolver(new File(moduleInfo.getSourcePath())));
		}
		String dependencyPath = GlobalProperties.getDependencyPath(projectInfo.getProjectDirName());
		if (!"".equals(dependencyPath)){
			String[] jars = dependencyPath.split("[;:]");
			for (String jarPath : jars) {
				try {
					typeSolverList.add(new JarTypeSolver(jarPath));
				} catch (IOException e) {
					LOGGER.error("Error when adding jar path to initial JavaParser. [{}]", jarPath, e);
				}
			}
		}
		TypeSolver typeSolver = new CombinedTypeSolver(typeSolverList.toArray(new TypeSolver[0]));
		JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
		StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);
	}
}
