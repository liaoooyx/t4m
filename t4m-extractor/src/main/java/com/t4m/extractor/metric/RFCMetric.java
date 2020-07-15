package com.t4m.extractor.metric;

import java.util.Map;

/**
 * Created by Yuxiang Liao on 2020-07-14 23:14.
 */
public class RFCMetric {

	public static final String UNSOLVED_METHOD_INVOCATION = "unsolved_method_invocation";

	/**
	 * countRFCMethodQualifiedSignatureMap中对应的方法methodQualifiedSignature计数+1
	 */
	public static void countRFCMethodQualifiedSignatureMap(
			Map<String, Integer> rfcMethodQualifiedSignatureMap, String methodQualifiedSignature) {
		if (rfcMethodQualifiedSignatureMap.containsKey(methodQualifiedSignature)) {
			int count = rfcMethodQualifiedSignatureMap.get(methodQualifiedSignature);
			rfcMethodQualifiedSignatureMap.put(methodQualifiedSignature, ++count);
		} else {
			rfcMethodQualifiedSignatureMap.put(methodQualifiedSignature, 1);
		}
	}
}
