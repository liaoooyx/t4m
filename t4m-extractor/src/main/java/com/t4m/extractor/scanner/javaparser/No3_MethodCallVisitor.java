package com.t4m.extractor.scanner.javaparser;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.types.ResolvedType;
import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.util.EntityUtil;
import com.t4m.extractor.util.JavaParserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Yuxiang Liao on 2020-07-11 11:16.
 */
public class No3_MethodCallVisitor extends VoidVisitorAdapter<Void> {

	private static final Logger LOGGER = LoggerFactory.getLogger(No3_MethodCallVisitor.class);

	private ClassInfo outerClassInfo;
	private ProjectInfo projectInfo;

	public No3_MethodCallVisitor(ClassInfo outerClassInfo, ProjectInfo projectInfo) {
		this.outerClassInfo = outerClassInfo;
		this.projectInfo = projectInfo;
	}


	@Override
	public void visit(MethodCallExpr n, Void arg) {
		super.visit(n, arg);
		ClassInfo currentClassInfo = JavaParserUtil.resolveCurrentClassInfo(n, projectInfo);
		// 如果出现本地方法调用，则记录，用于LOCM4

		// 如果出现方法调用，则记录，用于依赖（耦合）
		// 对于方法调用本身，应该先发现它所属的类，然后才是返回的类
		resolveWrappedClassDependencyFromMethodCall(n, currentClassInfo);
		resolveInternalDependencyFromMethodCall(n, currentClassInfo);

	}

	/* -------------------------------------------------------------------------------------------------*/

	/**
	 * 查找方法调用的包装类，也就是该方法在哪个类中声明，同时添加依赖
	 */
	private void resolveWrappedClassDependencyFromMethodCall(MethodCallExpr n, ClassInfo currentClassInfo) {
		try {
			String warppedClassName = n.resolve().declaringType().getQualifiedName();
			ClassInfo referenceClassInfo = EntityUtil.getClassByQualifiedName(projectInfo.getAllClassList(),
			                                                                  warppedClassName);
			System.out.println(n.toString() + " --wrapped class-- " + warppedClassName);
			if (referenceClassInfo != null) {
				// 添加依赖关系（耦合）
				JavaParserUtil.addDependency(currentClassInfo, referenceClassInfo);
			}
		} catch (UnsolvedSymbolException e) {
			LOGGER.info("Skip: [{}]. {}", n, e.toString());
			currentClassInfo.getUnresolvedNodeDescriptionList().add(
					"Node: " + n.toString() + ", Exception: " + e.toString());
		} catch (RuntimeException e) {
			LOGGER.info("Skip: [{}]. {}", n, e.toString());
			currentClassInfo.getUnresolvedNodeDescriptionList().add(
					"Node: " + n.toString() + ", Exception: " + e.toString());
		}
	}

	/**
	 * 查找方法调用中涉及的所有依赖，即方法调用语句中的所有节点，并添加依赖关系（耦合）。
	 * 解析时，如果是变量则获取声明类型，如果是方法调用则获取返回类型。（因此最后一个方法调用应该提前处理，获取它所属的类，而不是返回类型）
	 * 理论上，所有项目类的变量都能够解析成功。解析不成功的就是项目外的类，不考虑依赖。
	 * 但方法调用很可能解析失败，比如当方法的参数是Jar包中的类时，将无法解析。
	 */
	private void resolveInternalDependencyFromMethodCall(Node node, ClassInfo currentClassInfo) {
		for (Node n : node.getChildNodes()) {
			if (n instanceof Expression) {
				try {
					ResolvedType resolvedType = ((Expression) n).calculateResolvedType();
					if (resolvedType.isReferenceType()) {
						// 这里会解析项目内的类。变量则获取声明类型，方法则获取返回类型
						String qualifiedName = resolvedType.asReferenceType().getQualifiedName();
						ClassInfo referenceClassInfo = EntityUtil.getClassByQualifiedName(projectInfo.getAllClassList(),
						                                                                  qualifiedName);
						System.out.println(n.toString() + " --reference class-- " + qualifiedName);
						if (referenceClassInfo != null) {
							// 添加依赖关系（耦合）
							JavaParserUtil.addDependency(currentClassInfo, referenceClassInfo);
						}
					}
				} catch (UnsolvedSymbolException e) {
					LOGGER.info("Skip: [{}]. {}", n, e.toString());
					currentClassInfo.getUnresolvedNodeDescriptionList().add(
							"Node: " + n.toString() + ", Exception: " + e.toString());
				} catch (RuntimeException e) {
					LOGGER.error("Cannot solve:\n{}", n);
					LOGGER.error("Details: {}", e.toString());
					currentClassInfo.getUnresolvedNodeDescriptionList().add(
							"Node: " + n.toString() + ", Exception: " + e.toString());

				}

			}
			if (n.getChildNodes().size() > 0) {
				resolveInternalDependencyFromMethodCall(n, currentClassInfo);
			}
		}
	}
}
