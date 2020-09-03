package com.t4m.extractor.util;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.entity.ProjectInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NoSuchElementException;

/**
 * Created by Yuxiang Liao on 2020-07-12 01:40.
 */
public class JavaParserUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(JavaParserUtil.class);

	private JavaParserUtil() {
	}

	/**
	 * @param node The JavaParser node
	 * @param projectInfo The current {@code ProjectInfo} object that contains all of the {@code ClassInfo} objects
	 *
	 * @return the ClassInfo object to which the node belongs.
	 */
	public static ClassInfo resolveCurrentClassInfo(Node node, ProjectInfo projectInfo) {
		String qualifiedName = "null";
		try {
			if (node instanceof TypeDeclaration) {
				qualifiedName = ((TypeDeclaration<?>) node).getFullyQualifiedName().orElseThrow();
			} else {
				qualifiedName = String.valueOf(node.findAncestor(TypeDeclaration.class).orElseThrow()
				                                   .getFullyQualifiedName().orElseThrow());
			}
		} catch (NoSuchElementException e) {
			LOGGER.debug(
					"Error happened when resolving ClassInfo from node {}, please check the code. \nError info: [{}]",
					node, e);
		}

		ClassInfo target = EntityUtil.getClassByQualifiedName(projectInfo.getAllClassList(), qualifiedName);
		if (target == null) {
			target = EntityUtil.getClassByQualifiedName(projectInfo.getAllClassList(),
			                                            PackageInfo.EMPTY_IDENTIFIER + "." + qualifiedName);
		}
		return target;
	}

}
