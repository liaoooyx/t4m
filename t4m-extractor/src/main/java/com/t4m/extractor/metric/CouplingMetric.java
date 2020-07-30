package com.t4m.extractor.metric;

import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.util.MathUtil;

/**
 * Created by Yuxiang Liao on 2020-07-17 04:45.
 */
public class CouplingMetric {

	/**
	 * 包括CouplingBetweenObjects, AfferentCoupling(Fanin), EfferentCoupling(Fanout),
	 * Instability(Fanout/Fanin+out), MessagePassingCoupling (类中的本地方法，调用其他类的方法的数量)
	 */
	public static void calculateCoupling(ClassInfo classInfo) {
		int fanIn = classInfo.getPassiveDependencyAkaFanInList().size();
		int fanOut = classInfo.getActiveDependencyAkaFanOutList().size();
		classInfo.setCouplingBetweenObjects(fanIn + fanOut);
		classInfo.setAfferentCoupling(fanIn);
		classInfo.setEfferentCoupling(fanOut);
		String instability = MathUtil.divide(fanOut, fanOut + fanIn);
		classInfo.setInstability(instability);
		classInfo.setMessagePassingCoupling(classInfo.getOutClassMethodCallQualifiedSignatureMap().keySet().size());
	}

	/**
	 * 包括AfferentCoupling(Fanin), EfferentCoupling(Fanout),
	 * Instability(Fanout/Fanin+out)
	 */
	public static void calculateCoupling(PackageInfo packageInfo) {
		int fanIn = packageInfo.getPassiveDependencyAkaFanInList().size();
		int fanOut = packageInfo.getActiveDependencyAkaFanOutList().size();
		packageInfo.setAfferentCoupling(fanIn);
		packageInfo.setEfferentCoupling(fanOut);
		String instability = MathUtil.divide(fanOut, fanOut + fanIn);
		packageInfo.setInstability(instability);
		int numOfAllClass = packageInfo.getAllClassList().size();
		int numOfAbstraction = 0; // interface or abstract
		for (ClassInfo classInfo : packageInfo.getAllClassList()) {
			if (classInfo.getClassModifier() == null){
				System.out.println();
			}
			switch (classInfo.getClassModifier()) {
				case INTERFACE:
				case ABSTRACT_CLASS:
					numOfAbstraction++;
					break;
			}
		}
		packageInfo.setAbstractness(MathUtil.divide(numOfAbstraction, numOfAllClass));
	}
}
