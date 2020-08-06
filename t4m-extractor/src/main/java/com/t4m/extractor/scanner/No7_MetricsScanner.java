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

/**
 * 扫描meta数据，转化为metric数据
 * Created by Yuxiang Liao on 2020-07-16 17:21.
 */
public class No7_MetricsScanner {

	private static final Logger LOGGER = LoggerFactory.getLogger(No7_MetricsScanner.class);

	private final ProjectInfo projectInfo;

	public No7_MetricsScanner(ProjectInfo projectInfo) {
		this.projectInfo = projectInfo;
	}

	public void scan() {
		LOGGER.info("Calculating the values of metrics based on the resolved metadata.");
		for (ClassInfo classInfo : projectInfo.getAllClassList()) {
			ComplexityMetric.calculateComplexity(classInfo);
			RFCMetric.calculateRfc(classInfo);
			InheritanceMetric.calculateInheritance(classInfo);
			CouplingMetric.calculateCoupling(classInfo);
			CohesionMetric.calculateCohesionMetric(classInfo);
			SLOCMetric.calculateSloc(classInfo);
		}
		for (PackageInfo packageInfo : projectInfo.getPackageList()) {
			BasicMetric.calculateBasic(packageInfo);
			CouplingMetric.calculateCoupling(packageInfo);
			SLOCMetric.calculateSloc(packageInfo);
		}
		for (ModuleInfo moduleInfo : projectInfo.getModuleList()) {
			BasicMetric.calculateBasic(moduleInfo);
			SLOCMetric.calculateSloc(moduleInfo);
		}
		LOGGER.info(
				"************************************* Finished *************************************");
	}

	public static void main(String[] args) {
		String rootPath = "/Users/liao/myProjects/IdeaProjects/JSimulationProject";
		// String rootPath = "/Users/liao/myProjects/IdeaProjects/t4m";
		ProjectInfo projectInfo = new ProjectInfo(rootPath, GlobalProperties.DEFAULT_EXCLUDED_PATH,
		                                          GlobalProperties.DEFAULT_DEPENDENCY_PATH);
		T4MExtractor t4MExtractor = new T4MExtractor(projectInfo);
		t4MExtractor.scanMetricData();
		System.out.println();
	}
}
