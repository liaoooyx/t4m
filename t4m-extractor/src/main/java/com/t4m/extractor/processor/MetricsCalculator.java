package com.t4m.extractor.processor;

import com.t4m.extractor.ProcessChain;
import com.t4m.extractor.ProcessNode;
import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.ModuleInfo;
import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.metric.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yuxiang Liao on 2020-07-16 17:21.
 */
public class MetricsCalculator implements ProcessNode {

	private static final Logger LOGGER = LoggerFactory.getLogger(MetricsCalculator.class);

	@Override
	public void scan(ProjectInfo projectInfo, ProcessChain processChain) {
		LOGGER.info("Calculating the values of metrics based on the resolved metadata.");
		calculateMetrics(projectInfo);
		processChain.scan(projectInfo);
	}

	public void calculateMetrics(ProjectInfo projectInfo){
		List<ClassLevelMetric> classLevelMetricList = initClassLevelMetric();
		List<PackageLevelMetric> packageLevelMetricList = initPackageLevelMetric();
		List<ModuleLevelMetric> moduleLevelMetricList = initModuleLevelMetric();
		for (ClassInfo classInfo : projectInfo.getAllClassList()) {
			classLevelMetricList.forEach(classLevelMetric -> classLevelMetric.calculate(classInfo));
		}
		for (PackageInfo packageInfo : projectInfo.getPackageList()) {
			packageLevelMetricList.forEach(packageLevelMetric -> packageLevelMetric.calculate(packageInfo));
		}
		for (ModuleInfo moduleInfo : projectInfo.getModuleList()) {
			moduleLevelMetricList.forEach(moduleLevelMetric -> moduleLevelMetric.calculate(moduleInfo));
		}
	}

	private List<ClassLevelMetric> initClassLevelMetric() {
		List<ClassLevelMetric> classLevelMetricList = new ArrayList<>();
		classLevelMetricList.add(new ComplexityMetric());
		classLevelMetricList.add(new RFCMetric());
		classLevelMetricList.add(new InheritanceMetric());
		classLevelMetricList.add(new CouplingMetric());
		classLevelMetricList.add(new CohesionMetric());
		classLevelMetricList.add(new SLOCMetric());
		return classLevelMetricList;
	}

	private List<PackageLevelMetric> initPackageLevelMetric() {
		List<PackageLevelMetric> packageLevelMetricList = new ArrayList<>();
		packageLevelMetricList.add(new BasicMetric());
		packageLevelMetricList.add(new CouplingMetric());
		packageLevelMetricList.add(new SLOCMetric());
		return packageLevelMetricList;
	}

	private List<ModuleLevelMetric> initModuleLevelMetric() {
		List<ModuleLevelMetric> moduleLevelMetricList = new ArrayList<>();
		moduleLevelMetricList.add(new BasicMetric());
		moduleLevelMetricList.add(new SLOCMetric());
		return moduleLevelMetricList;
	}
}
