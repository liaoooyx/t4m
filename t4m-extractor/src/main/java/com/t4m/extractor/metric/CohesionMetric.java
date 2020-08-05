package com.t4m.extractor.metric;

import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.FieldInfo;
import com.t4m.extractor.entity.MethodInfo;
import com.t4m.extractor.util.MathUtil;

import java.util.*;

/**
 * Created by Yuxiang Liao on 2020-07-17 05:34.
 */
public class CohesionMetric {


	public static void calculateCohesionMetric(ClassInfo classInfo) {
		if (classInfo.getShortName().equals("CohesionClass")) {
			System.out.println();
		}
		for (MethodInfo methodInfo : classInfo.getMethodInfoList()) {
			initDirectCallingConnection(methodInfo);
		}

		List<Set<FieldInfo>> isolatedScopeFieldsList = new ArrayList<>(); // LOCM4
		List<Set<MethodInfo>> isolatedScopeMethodsList = new ArrayList<>(); // LOCM4
		Set<MethodPair> ndcMethodPairSet = new HashSet<>(); // NDC：直接连接的（访问同一个字段）方法对
		Set<MethodPair> nacMethodPairSet = new HashSet<>(); // NDC：直接和间接连接的（访问同一个字段）方法对

		for (FieldInfo fieldInfo : classInfo.getFieldInfoList()) {
			//TODO 如果需要调整变量是否为static 或 final，在这里加上判断

			// 以字段为起始，向上搜索被依赖关系，储存进list中，得到 direct connections set
			for (MethodInfo afferentMethod : fieldInfo.getBeingAccessedDirectlyByLocalMethodSet()) {
				initMethodPassingAccessToField(fieldInfo, afferentMethod, new ArrayList<>());
			}
		}
		for (FieldInfo fieldInfo : classInfo.getFieldInfoList()) {
			//TODO 如果需要调整变量是否为static 或 final，在这里加上判断

			// 以字段为起始，找到相关方法指向的字段，加入到同一个scope中，递归找到所有相关方法。再将继续下一个不在域中的字段
			if (!isFieldExistInAnyScope(fieldInfo, isolatedScopeFieldsList)) {
				Set<FieldInfo> scopeFieldSet = new HashSet<>();
				Set<MethodInfo> scopeMethodSet = new HashSet<>();
				initScopeField(fieldInfo, scopeFieldSet, scopeMethodSet, new ArrayList<>());
				if (!scopeMethodSet.isEmpty()) {
					isolatedScopeFieldsList.add(scopeFieldSet);
					isolatedScopeMethodsList.add(scopeMethodSet);
				}
			}

			// NDC：直接连接的（访问同一个字段）方法对
			fillMethodPairFromSet(ndcMethodPairSet, fieldInfo.getBeingPassingAccessedByLocalMethodSet());
		}

		// NAC：NDC+NIC
		for (Set<MethodInfo> scopeMethodSet : isolatedScopeMethodsList) {
			fillMethodPairFromSet(nacMethodPairSet, scopeMethodSet);
		}

		int locm = isolatedScopeFieldsList.size(); // LCOM定义下的隔离区域的数量
		int numberOfDirectConnections = ndcMethodPairSet.size(); //NDC：直接连接的（访问同一个字段）方法对的数量
		int numberOfAllConnections = nacMethodPairSet.size(); // NDC：直接和间接连接的（访问同一个字段）方法对数量
		int numberOfMethods = classInfo.getNumberOfMethods();
		int numberOfPossibleConnections = numberOfMethods * (numberOfMethods - 1) / 2;
		String tcc = MathUtil.divide(numberOfDirectConnections, numberOfPossibleConnections);
		String lcc = MathUtil.divide(numberOfAllConnections, numberOfPossibleConnections);
		classInfo.setLackOfCohesionOfMethods4(locm);
		classInfo.setTightClassCohesion(tcc);
		classInfo.setLooseClassCohesion(lcc);
		if (classInfo.getShortName().equals("CohesionClass")) {
			System.out.println();
		}

	}

	/**
	 * 将Set集合中的方法两两组合，形成不重复的方法对
	 */
	private static void fillMethodPairFromSet(Set<MethodPair> methodPairSet, Set<MethodInfo> methodSet) {
		MethodInfo[] methodInfos = methodSet.toArray(new MethodInfo[]{});
		if (methodInfos.length > 1) {
			for (int i = 0; i < methodInfos.length - 1; i++) {
				for (int j = i + 1; j < methodInfos.length; j++) {
					methodPairSet.add(new MethodPair(methodInfos[i], methodInfos[j]));
				}
			}
		}
	}

	/**
	 * 判断字段是否已经被包含在任何scope中
	 */
	private static boolean isFieldExistInAnyScope(FieldInfo fieldInfo, List<Set<FieldInfo>> isolatedScopeList) {
		for (Set<FieldInfo> fieldInfoSet : isolatedScopeList) {
			if (fieldInfoSet.contains(fieldInfo)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 将LOCM定义的同一个component的所有字段放入同一个scopeFieldSet中，字段涉及的方法放入同一个scopeMethodSet中
	 * 返回false说明该字段没有关联的方法。
	 */
	private static void initScopeField(
			FieldInfo fieldInfo, Set<FieldInfo> scopeFieldSet, Set<MethodInfo> scopeMethodSet,
			List<FieldInfo> interruptList) {
		interruptList.add(fieldInfo);
		scopeFieldSet.add(fieldInfo);
		scopeMethodSet.addAll(fieldInfo.getBeingPassingAccessedByLocalMethodSet());
		for (MethodInfo methodInfo : fieldInfo.getBeingPassingAccessedByLocalMethodSet()) {
			for (FieldInfo efferentField : methodInfo.getFieldAccessSet()) {
				if (!interruptList.contains(efferentField)) {
					initScopeField(efferentField, scopeFieldSet, scopeMethodSet, interruptList);
				}
			}
		}
	}

	/**
	 * 找到从（以方法作为根节点的）继承树往下遍历到同一个字段的所有方法，并将这些方法记录到FieldInfo的元数据中
	 */
	private static void initMethodPassingAccessToField(
			FieldInfo targetField, MethodInfo currentMethod, List<MethodInfo> interruptList) {
		interruptList.add(currentMethod);
		targetField.getBeingPassingAccessedByLocalMethodSet().add(currentMethod);
		for (MethodInfo afferentMethod : currentMethod.getBeingAccessedByLocalMethodSet()) {
			if (!interruptList.contains(afferentMethod)) { // 避免递归或依赖循环导致死循环 A->...->A
				initMethodPassingAccessToField(targetField, afferentMethod, interruptList);
			}
		}
	}

	/**
	 * 补充LOCM4所需的meta data: 方法被哪些本地方法直接访问，字段被哪些本地方法直接访问
	 */
	private static void initDirectCallingConnection(MethodInfo methodInfo) {
		// 方法被哪些本地方法直接访问
		for (MethodInfo callingMethod : methodInfo.getLocalMethodAccessSet()) {
			callingMethod.getBeingAccessedByLocalMethodSet().add(methodInfo);
		}
		// 字段被哪些本地方法直接访问
		for (FieldInfo fieldInfo : methodInfo.getFieldAccessSet()) {
			fieldInfo.getBeingAccessedDirectlyByLocalMethodSet().add(methodInfo);
		}
	}

	static class MethodPair {
		MethodInfo m1;
		MethodInfo m2;

		public MethodPair(MethodInfo m1, MethodInfo m2) {
			this.m1 = m1;
			this.m2 = m2;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			MethodPair methodPair = (MethodPair) o;
			return (Objects.equals(m1, methodPair.m1) && Objects.equals(m2, methodPair.m2)) || (Objects.equals(m1,
			                                                                                                   methodPair.m2) &&
					Objects.equals(m2, methodPair.m1));
		}

		@Override
		public int hashCode() {
			return Objects.hash(m1, m2);
		}
	}

}
