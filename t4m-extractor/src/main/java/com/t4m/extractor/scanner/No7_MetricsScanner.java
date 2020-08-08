package com.t4m.extractor.scanner;

import com.t4m.conf.GlobalProperties;
import com.t4m.extractor.T4MExtractor;
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
public class No7_MetricsScanner implements T4MScanner {

	private static final Logger LOGGER = LoggerFactory.getLogger(No7_MetricsScanner.class);


	// public No7_MetricsScanner(ProjectInfo projectInfo) {
	// 	this.projectInfo = projectInfo;
	// }

	@Override
	public void scan(ProjectInfo projectInfo, ScannerChain scannerChain) {
		LOGGER.info("Calculating the values of metrics based on the resolved metadata.");
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

		scannerChain.scan(projectInfo);
	}

	// public void scan() {
	// 	LOGGER.info("Calculating the values of metrics based on the resolved metadata.");
	// 	List<ClassLevelMetric> classLevelMetricList = initClassLevelMetric();
	// 	List<PackageLevelMetric> packageLevelMetricList = initPackageLevelMetric();
	// 	List<ModuleLevelMetric> moduleLevelMetricList = initModuleLevelMetric();
	// 	for (ClassInfo classInfo : projectInfo.getAllClassList()) {
	// 		classLevelMetricList.forEach(classLevelMetric -> classLevelMetric.calculate(classInfo));
	// 	}
	// 	for (PackageInfo packageInfo : projectInfo.getPackageList()) {
	// 		packageLevelMetricList.forEach(packageLevelMetric -> packageLevelMetric.calculate(packageInfo));
	// 	}
	// 	for (ModuleInfo moduleInfo : projectInfo.getModuleList()) {
	// 		moduleLevelMetricList.forEach(moduleLevelMetric -> moduleLevelMetric.calculate(moduleInfo));
	// 	}
	// 	LOGGER.info("************************************* Finished *************************************");
	// }

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

	public static void main(String[] args) {
		String rootPath = "/Users/liao/myProjects/IdeaProjects/JSimulationProject";
		// String rootPath = "/Users/liao/myProjects/IdeaProjects/t4m";
		ProjectInfo projectInfo = new ProjectInfo(rootPath, GlobalProperties.DEFAULT_EXCLUDED_PATH,
		                                          GlobalProperties.DEFAULT_DEPENDENCY_PATH);
		new T4MExtractor().extract(projectInfo);
		System.out.println();
	}
}
