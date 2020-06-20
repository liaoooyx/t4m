package com.t4m.extractor.scanner.ast;

import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.metric.SLOCMetric;
import org.eclipse.jdt.core.dom.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Yuxiang Liao on 2020-06-18 13:31.
 */
public class T4MVisitor extends ASTVisitor {

	private ClassInfo outerClassInfo;

	public T4MVisitor(ClassInfo classInfo) {
		this.outerClassInfo = classInfo;
	}

	@Override
	public boolean visit(TypeDeclaration typeDec) {
		ClassInfo currentClassInfo;
		String[] sourceLines;
		if (isInnerClass(typeDec)) {
			// 内部类，则创建新的ClassInfo作为内部类，并与外部类关联
			String innerClassName =
					outerClassInfo.getShortName() + "$" + typeDec.getName().toString(); // Class$InnerClass
			currentClassInfo = outerClassInfo.safeAddInnerClassList(new ClassInfo(innerClassName, outerClassInfo));
			sourceLines = typeDec.toString().split(System.lineSeparator());

		} else {
			// 非内部类
			currentClassInfo = outerClassInfo;
			// 需要获取包括package和import关键字的行
			sourceLines = typeDec.getParent().toString().split(System.lineSeparator());
		}
		//提取关于SLOC的信息
		Map<ClassInfo.SLOCType, Integer> slocCounterMap = currentClassInfo.getSlocCounterMap();
		Arrays.stream(sourceLines).forEach(line -> SLOCMetric.SLOCCounterFromAST(line, slocCounterMap));
		currentClassInfo.setSlocCounterMap(slocCounterMap);
		// 类的类型
		if (typeDec.isInterface()) {
			currentClassInfo.setClassModifier(ClassInfo.ClassModifier.INTERFACE);
		} else if (isAbstractClass(typeDec.modifiers())) {
			currentClassInfo.setClassModifier(ClassInfo.ClassModifier.ABSTRACT_CLASS);
		} else {
			currentClassInfo.setClassModifier(ClassInfo.ClassModifier.CLASS);
		}
		// 方法数量
		currentClassInfo.setNumberOfMethods(typeDec.getMethods().length);
		// 字段数量
		currentClassInfo.setNumberOfFields(typeDec.getFields().length);
		return true;
	}

	/**
	 * 判断是内部类，还是Java源文件名对应的主类
	 */
	public boolean isInnerClass(TypeDeclaration typeDec) {
		return !outerClassInfo.getShortName().equals(typeDec.getName().toString());
	}

	public boolean isAbstractClass(List<Modifier> modifiers) {
		return modifiers.stream().anyMatch(modifier -> "abstract".equals(modifier.getKeyword().toString()));
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		if (isInnerClass((TypeDeclaration) node.getParent())) {

		}
		return true;
	}

	@Override
	public boolean visit(FieldDeclaration node) {
		for (Object obj : node.fragments()) {
			VariableDeclarationFragment v = (VariableDeclarationFragment) obj;
			System.out.println("Field:\t" + v.getName());
		}

		return true;
	}
}
