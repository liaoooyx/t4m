package com.t4m.extractor.metric;

import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.util.MathUtil;

/**
 * Created by Yuxiang Liao on 2020-07-17 04:45.
 */
public class CouplingMetric {

	/**
	 * 包括CouplingBetweenObjects, AfferentCoupling(Fanin), EfferentCoupling(Fanout),
	 * Instability(Fanout/Fanin+out), MessagePassingCoupling (类中的本地方法，调用其他类的方法的数量)
	 */
	public static void calculateCouplingForClass(ClassInfo classInfo) {
		int fanin = classInfo.getPassiveDependencyAkaFanInList().size();
		if (classInfo.getPassiveDependencyAkaFanInList().contains(classInfo)) {
			fanin--;
		}
		int fanout = classInfo.getActiveDependencyAkaFanOutList().size();
		if (classInfo.getActiveDependencyAkaFanOutList().contains(classInfo)) {
			fanout--;
		}
		classInfo.setCouplingBetweenObjects(fanin + fanout);
		classInfo.setAfferentCoupling(fanin);
		classInfo.setEfferentCoupling(fanout);
		float instability = MathUtil.divide(fanout, fanout + fanin);
		classInfo.setInstability(instability);
		classInfo.setMessagePassingCoupling(classInfo.getOutClassMethodCallQualifiedSignatureMap().keySet().size());
	}
}
