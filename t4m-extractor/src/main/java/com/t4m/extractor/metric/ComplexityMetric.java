package com.t4m.extractor.metric;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.stmt.*;
import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.util.MathUtil;

/**
 * Created by Yuxiang Liao on 2020-07-16 18:39.
 */
public class ComplexityMetric implements ClassLevelMetric {

	/**
	 * Recursively calculate the cyclomatic complexity of a method.
	 * Including stmt: {@code if}, {@code while}, {@code for}, {@code &&}, {@code ||}, {@code ?:},
	 * {@code cases and default of switch}, {@code catches of try}.
	 * @param n nodes of block stmt ant its child stmt.
	 * @param cyclomaticComplexityCount value of current complexity counted
	 * @return value of complexity counted at the end of this recursion.
	 */
	public static int resolveComplexity(Node n, int cyclomaticComplexityCount) {
		for (Node childNode : n.getChildNodes()) {
			cyclomaticComplexityCount += resolveComplexity(childNode, 0);
			if (childNode instanceof DoStmt) {
				cyclomaticComplexityCount += 1;
			} else if (childNode instanceof WhileStmt) {
				cyclomaticComplexityCount += 1;
			} else if (childNode instanceof ForEachStmt) {
				cyclomaticComplexityCount += 1;
			} else if (childNode instanceof ForStmt) {
				cyclomaticComplexityCount += 1;
			} else if (childNode instanceof IfStmt) {
				cyclomaticComplexityCount += 1;
			} else if (childNode instanceof SwitchEntry) {
				// hidden default case does not have label.
				cyclomaticComplexityCount += ((SwitchEntry) childNode).getLabels().isEmpty() ? 1 : ((SwitchEntry) childNode).getLabels().size();
			} else if (childNode instanceof CatchClause) {
				cyclomaticComplexityCount += 1;
			} else if (childNode instanceof BinaryExpr) {
				BinaryExpr.Operator operator = ((BinaryExpr) childNode).getOperator();
				if (operator.equals(BinaryExpr.Operator.AND) || operator.equals(BinaryExpr.Operator.OR)) {
					cyclomaticComplexityCount += 1;
				}
			} else if (childNode instanceof ConditionalExpr) {
				//The ternary conditional expression: b==0?x:y
				cyclomaticComplexityCount += 1;
			}
		}
		return cyclomaticComplexityCount;
	}

	@Override
	public void calculate(ClassInfo classInfo) {
		int sum = 0;
		int max = 0;
		for (int i : classInfo.getCyclomaticComplexityList()) {
			sum += i;
			max = Math.max(i, max);
		}
		classInfo.setWeightedMethodsCount(sum);
		classInfo.setMaxCyclomaticComplexity(max);
		String avg = MathUtil.divide(max, classInfo.getCyclomaticComplexityList().size());
		classInfo.setAvgCyclomaticComplexity(avg);
	}
}
