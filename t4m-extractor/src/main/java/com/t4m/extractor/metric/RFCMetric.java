package com.t4m.extractor.metric;

import com.t4m.extractor.entity.ClassInfo;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

	/**
	 * 递归遍历所有父类。将父类的所有本地方法集合合并到列表中
	 */
	private static void accumulateRFCFromExtendsClass(ClassInfo classInfo, Set<String> rfcSet) {
		for (ClassInfo extendsClass : classInfo.getExtendedClassList()) {
			rfcSet.addAll(extendsClass.getLocalMethodCallQualifiedSignatureMap().keySet());
			if (!extendsClass.getExtendedClassList().isEmpty()) {
				accumulateRFCFromExtendsClass(extendsClass, rfcSet);
			}
		}
	}

	/**
	 * 将父类的所有本地方法集合和自身的调用方法集合合并。
	 * 理想情况下（有Jar包路径进行AST分析），该集合以方法的全限定路径签名作为记录，因此能够确保重载的方法唯一
	 * 以类为单位计算相关的response for class度量。需要递归遍历所有父类。
	 *
	 * RFC：所有可以对一个类的消息做出响应的方法个数: 类中的所有方法集合，
	 * 包括从父类继承的方法（但不包括重写的方法，因为方法签名应该唯一），和
	 * 类中所有方法所调用的方法集合（所有方法，但不可重复）
	 */
	private static Set<String> calculateAccumulatedRFCSet(ClassInfo targetClass) {
		Set<String> accumulatedRfcSet = new HashSet<>(targetClass.getOutClassMethodCallQualifiedSignatureMap().keySet());
		accumulatedRfcSet.addAll(targetClass.getLocalMethodCallQualifiedSignatureMap().keySet());
		accumulateRFCFromExtendsClass(targetClass, accumulatedRfcSet);
		return accumulatedRfcSet;
	}

	/**
	 * 将父类的所有方法集合和自身的调用方法集合合并。
	 */
	public static void calculateRfcForClass(ClassInfo targetClass) {
		// todo 需要处理RFC不精确的情况
		Set<String> accumulatedRfcSet = calculateAccumulatedRFCSet(targetClass);
		targetClass.setResponseForClass(accumulatedRfcSet.size());
	}

}
