package com.t4m.extractor.scanner;

import com.t4m.extractor.T4MExtractor;
import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.metric.*;

/**
 * 扫描meta数据，转化为metric数据
 * Created by Yuxiang Liao on 2020-07-16 17:21.
 */
public class No7_MetricsScanner {

	private final ProjectInfo projectInfo;

	public No7_MetricsScanner(ProjectInfo projectInfo) {
		this.projectInfo = projectInfo;
	}

	public void scan() {
		for (ClassInfo classInfo : projectInfo.getAllClassList()) {
			ComplexityMetric.calculateComplexityForClass(classInfo);
			RFCMetric.calculateRfcForClass(classInfo);
			InheritanceMetric.calculateInheritanceForClass(classInfo);
			CouplingMetric.calculateCouplingForClass(classInfo);
			CohesionMetric.calculateCohesionMetricForClass(classInfo);
		}
		for (PackageInfo packageInfo: projectInfo.getPackageList()){

		}
	}

	public static void main(String[] args) {
		String rootPath = "/Users/liao/myProjects/IdeaProjects/JSimulationProject";
		ProjectInfo projectInfo = new ProjectInfo(rootPath);
		T4MExtractor t4MExtractor = new T4MExtractor(projectInfo);
		t4MExtractor.scanMetricData();
		System.out.println();
	}
}
