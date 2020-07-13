package com.t4m.extractor.util;

import java.util.regex.Matcher;

/**
 * Created by Yuxiang Liao on 2020-07-12 04:36.
 */
public class CommonParserUtil {

	/**
	 * Class.InnerClass -> Class$InnerClass
	 */
	@Deprecated
	public static String transferShortNameAsInnerClassFormat(String shortName) {
		return shortName.replaceAll("\\.", Matcher.quoteReplacement("$"));
	}

	/**
	 * Class.InnerClass -> Class$InnerClass; com.a.b.c.Class.InnerClass -> com.a.b.c.Class$InnerClass
	 */
	@Deprecated
	public static String transferQualifiedNameAsInnerClassFormat(String name) {
		if (name.contains(".")) {
			int index = name.lastIndexOf(".");
			return name.substring(0, index) + "$" + name.substring(index + 1);
		} else {
			return name;
		}
	}

}
