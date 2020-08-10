package com.t4m.extractor.metric;

import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.util.MathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Yuxiang Liao on 2020-07-17 04:45.
 */
public class CouplingMetric implements ClassLevelMetric, PackageLevelMetric {

	private static final Logger LOGGER = LoggerFactory.getLogger(CouplingMetric.class);

	@Override
	public void calculate(ClassInfo classInfo) {
		int fanIn = classInfo.getPassiveDependencyAkaFanInList().size();
		int fanOut = classInfo.getActiveDependencyAkaFanOutList().size();
		classInfo.setCouplingBetweenObjects(fanIn + fanOut);
		classInfo.setAfferentCoupling(fanIn);
		classInfo.setEfferentCoupling(fanOut);
		String instability = MathUtil.divide(fanOut, fanOut + fanIn);
		classInfo.setInstability(instability);
		classInfo.setMessagePassingCoupling(classInfo.getOutClassMethodCallQualifiedSignatureMap().keySet().size());
	}

	@Override
	public void calculate(PackageInfo packageInfo) {
		int fanIn = packageInfo.getPassiveDependencyAkaFanInList().size();
		int fanOut = packageInfo.getActiveDependencyAkaFanOutList().size();
		packageInfo.setAfferentCoupling(fanIn);
		packageInfo.setEfferentCoupling(fanOut);
		String instability = MathUtil.divide(fanOut, (float) fanOut + fanIn);
		packageInfo.setInstability(instability);
		int numOfAllClass = packageInfo.getAllClassList().size();
		int numOfAbstraction = 0; // interface or abstract
		for (ClassInfo classInfo : packageInfo.getAllClassList()) {
			if (classInfo.getClassModifier() == null) {
				LOGGER.error("Cannot identify the ClassModifier of [{}]", classInfo.getAbsolutePath());
			}else {
				switch (classInfo.getClassModifier()) {
					case INTERFACE:
					case ABSTRACT_CLASS:
						numOfAbstraction++;
						break;
				}
			}
		}
		packageInfo.setAbstractness(MathUtil.divide(numOfAbstraction, numOfAllClass));
	}
}
