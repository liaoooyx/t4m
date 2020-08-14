package com.t4m.extractor.metric;

import com.t4m.extractor.entity.ClassInfo;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Yuxiang Liao on 2020-07-14 23:14.
 */
public class RFCMetric implements ClassLevelMetric{

	public static final String UNSOLVED_METHOD_INVOCATION = "unsolved_method_invocation";

	/**
	 * Add 1 for the relevant method within countRFCMethodQualifiedSignatureMap.
	 * @param rfcMethodQualifiedSignatureMap a collection of methods with corresponding RFC values.
	 * @param methodQualifiedSignature to identify a specific method.
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

	/**
	 * Recursively calculate all parent classes
	 * @param currentClassInfo current class
	 * @param rfcSet a set of unique methods in terms of current class's RFC
	 */
	private void accumulateRFCFromExtendsClass(ClassInfo currentClassInfo, Set<String> rfcSet) {
		for (ClassInfo extendsClass : currentClassInfo.getExtendsClassList()) {
			rfcSet.addAll(extendsClass.getLocalMethodCallQualifiedSignatureMap().keySet());
			if (!extendsClass.getExtendsClassList().isEmpty()) {
				accumulateRFCFromExtendsClass(extendsClass, rfcSet);
			}
		}
	}

	/**
	 * Merge all the local methods of the parent classes and its local methods.
	 * Ideally (if JavaParaser can locate the path of dependencies to analyse),
	 * the methods are classified by their fully qualified signature,
	 * thus can be ensure that the overwritten method is unique.
	 *
	 * Need to recursively calculate all parent classes
	 * @param targetClass the class for which we are computing RFC
	 * @return a set of unique methods in terms of target class's RFC
	 */
	private Set<String> calculateAccumulatedRFCSet(ClassInfo targetClass) {
		Set<String> accumulatedRfcSet = new HashSet<>(targetClass.getOutClassMethodCallQualifiedSignatureMap().keySet());
		accumulatedRfcSet.addAll(targetClass.getLocalMethodCallQualifiedSignatureMap().keySet());
		accumulateRFCFromExtendsClass(targetClass, accumulatedRfcSet);
		return accumulatedRfcSet;
	}

	@Override
	public void calculate(ClassInfo targetClass) {
		Set<String> accumulatedRfcSet = calculateAccumulatedRFCSet(targetClass);
		targetClass.setResponseForClass(accumulatedRfcSet.size());
	}
}
