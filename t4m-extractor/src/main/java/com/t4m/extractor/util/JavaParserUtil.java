package com.t4m.extractor.util;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.entity.ProjectInfo;

/**
 * Created by Yuxiang Liao on 2020-07-12 01:40.
 */
public class JavaParserUtil {

	private JavaParserUtil() {
	}

	/**
	 * @param node The JavaParser node
	 * @param projectInfo The current {@code ProjectInfo} object that contains all of the {@code ClassInfo} objects
	 * @return the ClassInfo object to which the node belongs.
	 */
	public static ClassInfo resolveCurrentClassInfo(Node node, ProjectInfo projectInfo) {
		String qualifiedName;
		if (node instanceof TypeDeclaration) {
			qualifiedName = String.valueOf(((TypeDeclaration) node).getFullyQualifiedName().orElse(null));
		} else {
			qualifiedName = String.valueOf(node.findAncestor(TypeDeclaration.class).orElse(null).getFullyQualifiedName()
			                                   .orElse(null));
		}
		ClassInfo target = EntityUtil.getClassByQualifiedName(projectInfo.getAllClassList(), qualifiedName);
		if (target == null) {
			target = EntityUtil.getClassByQualifiedName(projectInfo.getAllClassList(),
			                                            PackageInfo.EMPTY_IDENTIFIER + "." + qualifiedName);
		}
		return target;
	}

}
