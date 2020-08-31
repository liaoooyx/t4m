package com.t4m.extractor.metric;

import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.FieldInfo;
import com.t4m.extractor.entity.MethodInfo;
import com.t4m.extractor.util.MathUtil;

import java.util.*;

/**
 * Created by Yuxiang Liao on 2020-07-17 05:34.
 */
public class CohesionMetric implements ClassLevelMetric {

	@Override
	public void calculate(ClassInfo classInfo) {
		for (MethodInfo methodInfo : classInfo.getMethodInfoList()) {
			initDirectCallingConnection(methodInfo);
		}

		initMethodPassingAccessToField(classInfo);

		List<Set<FieldInfo>> isolatedScopeFieldsList = new ArrayList<>();
		List<Set<MethodInfo>> isolatedScopeMethodsList = new ArrayList<>();
		Set<MethodPair> directlyConnectedMethodPairSet = new HashSet<>();
		Set<MethodPair> allConnectedMethodPairSet = new HashSet<>();
		// Start from fields, put all methods that accessing the same field into the same scope.
		// Recursively for all methods.
		for (FieldInfo fieldInfo : classInfo.getFieldInfoList()) {
			// Do conditional judgement here if need to filter the static or final keyword.
			if (fieldInfo.isInstanceVariable()) {
				if (!isFieldExistInAnyScope(fieldInfo, isolatedScopeFieldsList)) {
					Set<FieldInfo> scopeFieldSet = new HashSet<>();
					Set<MethodInfo> scopeMethodSet = new HashSet<>();
					initScopeField(fieldInfo, scopeFieldSet, scopeMethodSet, new ArrayList<>());
					if (!scopeMethodSet.isEmpty()) {
						isolatedScopeFieldsList.add(scopeFieldSet);
						isolatedScopeMethodsList.add(scopeMethodSet);
					}
				}
				fillMethodPairFromSet(directlyConnectedMethodPairSet, fieldInfo.getBeingPassingAccessedByLocalMethodSet());
			}
		}
		for (Set<MethodInfo> scopeMethodSet : isolatedScopeMethodsList) {
			fillMethodPairFromSet(allConnectedMethodPairSet, scopeMethodSet);
		}

		int lcom = isolatedScopeFieldsList.size();
		int numberOfDirectConnections = directlyConnectedMethodPairSet.size();
		int numberOfAllConnections = allConnectedMethodPairSet.size();
		int numberOfMethods = classInfo.getNumberOfMethods();
		float numberOfPossibleConnections = (float) numberOfMethods * (numberOfMethods - 1) / 2;
		String tcc = MathUtil.divide(numberOfDirectConnections, numberOfPossibleConnections);
		String lcc = MathUtil.divide(numberOfAllConnections, numberOfPossibleConnections);
		classInfo.setLackOfCohesionOfMethods4(lcom);
		classInfo.setTightClassCohesion(tcc);
		classInfo.setLooseClassCohesion(lcc);
	}

	/**
	 * Form a non-repetitive method pair.
	 */
	private void fillMethodPairFromSet(Set<MethodPair> methodPairSet, Set<MethodInfo> methodSet) {
		MethodInfo[] methodInfos = methodSet.toArray(new MethodInfo[]{});
		if (methodInfos.length > 1) {
			for (int i = 0; i < methodInfos.length - 1; i++) {
				for (int j = i + 1; j < methodInfos.length; j++) {
					MethodPair methodPair = new MethodPair(methodInfos[i], methodInfos[j]);
					if (!methodPairSet.contains(methodPair)) {
						methodPairSet.add(methodPair);
					}
				}
			}
		}
	}

	/**
	 * Check whether the field is contained within any scope or not.
	 */
	private boolean isFieldExistInAnyScope(FieldInfo fieldInfo, List<Set<FieldInfo>> isolatedScopeList) {
		for (Set<FieldInfo> fieldInfoSet : isolatedScopeList) {
			if (fieldInfoSet.contains(fieldInfo)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Put all methods that accessing the target field into the same scope. Recursively for all methods.
	 *
	 * @param fieldInfo The target field.
	 * @param scopeFieldSet Add all fields that exists in the same scope to this set.
	 * @param scopeMethodSet Add all methods that related to the input param {@code fieldInfo} object to this set.
	 * @param interruptList Avoid cycle dependency.
	 */
	private void initScopeField(
			FieldInfo fieldInfo, Set<FieldInfo> scopeFieldSet, Set<MethodInfo> scopeMethodSet, List<FieldInfo> interruptList) {
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
	 * Start from fields, scan the dependencies upward, to get the direct connections set
	 *
	 * @param classInfo the current ClassInfo object
	 */
	private void initMethodPassingAccessToField(ClassInfo classInfo) {
		for (FieldInfo fieldInfo : classInfo.getFieldInfoList()) {
			// Do conditional judgement here if need to filter the static or final keyword.
			if (fieldInfo.isInstanceVariable()) {
				for (MethodInfo afferentMethod : fieldInfo.getBeingAccessedDirectlyByLocalMethodSet()) {
					initMethodPassingAccessToField(fieldInfo, afferentMethod, new ArrayList<>());
				}
			}
		}
	}

	/**
	 * Find all methods that accessing the same fields through the invocation tree.
	 * Add the result to the metadata variable that declared in {@code FieldInfo} object.
	 *
	 * @param targetField current instant variable
	 * @param currentMethod the method that access to the {@code targetField}
	 * @param interruptList A list to avoid recursion or dependency circle that leading to infinite loops
	 */
	private void initMethodPassingAccessToField(
			FieldInfo targetField, MethodInfo currentMethod, List<MethodInfo> interruptList) {
		interruptList.add(currentMethod);
		targetField.getBeingPassingAccessedByLocalMethodSet().add(currentMethod);
		for (MethodInfo afferentMethod : currentMethod.getBeingAccessedByLocalMethodSet()) {
			if (!interruptList.contains(afferentMethod)) {// Avoid recursion or dependency circle that leading to infinite loops A->...->A
				initMethodPassingAccessToField(targetField, afferentMethod, interruptList);
			}
		}
	}

	/**
	 * Complement the metadata that required by LCOM4 metric:
	 * Methods and fields are invoked by which local methods.
	 */
	private void initDirectCallingConnection(MethodInfo methodInfo) {
		// Methods are invoked by which local methods.
		for (MethodInfo callingMethod : methodInfo.getLocalMethodAccessSet()) {
			callingMethod.getBeingAccessedByLocalMethodSet().add(methodInfo);
		}
		// Fields are invoked by which local methods.
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
			return (Objects.equals(m1, methodPair.m1) && Objects.equals(m2, methodPair.m2)) || (Objects.equals(m1, methodPair.m2) && Objects.equals(
					m2, methodPair.m1));
		}

		@Override
		public int hashCode() {
			return m1.hashCode() + m2.hashCode();
		}
	}

}
