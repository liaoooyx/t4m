package com.t4m.extractor.metric;

import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.util.MathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Yuxiang Liao on 2020-07-17 04:45.
 */
public class CouplingMetric implements ClassLevelMetric, PackageLevelMetric {

	private static final Logger LOGGER = LoggerFactory.getLogger(CouplingMetric.class);

	@Override
	public void calculate(ClassInfo classInfo) {
		int fanIn = classInfo.getPassiveDependencyAkaFanInList().size();
		int fanOut = classInfo.getActiveDependencyAkaFanOutList().size();
		Set<ClassInfo> cboSet = new HashSet<>();
		cboSet.addAll(classInfo.getPassiveDependencyAkaFanInList());
		cboSet.addAll(classInfo.getActiveDependencyAkaFanOutList());
		classInfo.setCouplingBetweenObjects(cboSet.size());
		classInfo.setAfferentCoupling(fanIn);
		classInfo.setEfferentCoupling(fanOut);
		String instability = MathUtil.divide(fanOut, (float) fanOut + fanIn);
		classInfo.setInstability(instability);
		int mpc = classInfo.getOutClassMethodCallQualifiedSignatureMap().keySet().stream().filter(s -> {
			String[] array = s.replaceAll("\\(.*\\)", "").split("\\.");
			if (array.length == 1) {
				return false;
			}
			return !classInfo.getShortName().equals(array[array.length - 2]);
		}).mapToInt(e -> 1).sum();
		classInfo.setMessagePassingCoupling(mpc);
	}

	@Override
	public void calculate(PackageInfo packageInfo) {
		Set<ClassInfo> faninSet = new HashSet<>();
		Set<ClassInfo> fanoutSet = new HashSet<>();
		for (ClassInfo classInfo : packageInfo.getAllClassList()) {
			faninSet.addAll(classInfo.getPassiveDependencyAkaFanInList().stream().filter(e -> {
				if (e == null || packageInfo.equals(e.getPackageInfo())) {
					return false;
				} else {
					return true;
				}
			}).collect(Collectors.toSet()));
			fanoutSet.addAll(classInfo.getActiveDependencyAkaFanOutList().stream().filter(e -> {
				if (e == null || packageInfo.equals(e.getPackageInfo())) {
					return false;
				} else {
					return true;
				}
			}).collect(Collectors.toSet()));
		}
		int fanIn = faninSet.size();
		int fanOut = fanoutSet.size();
		packageInfo.setAfferentCoupling(fanIn);
		packageInfo.setEfferentCoupling(fanOut);
		String instability = MathUtil.divide(fanOut, (float) fanOut + fanIn);
		packageInfo.setInstability(instability);
		int numOfAllClass = packageInfo.getAllClassList().size();
		int numOfAbstraction = 0; // interface or abstract
		for (ClassInfo classInfo : packageInfo.getAllClassList()) {
			if (classInfo.getClassModifier() == null) {
				LOGGER.error("Cannot identify the ClassModifier of [{}]", classInfo.getAbsolutePath());
			} else {
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
