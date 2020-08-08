package com.t4m.extractor;

import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.scanner.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Created by Yuxiang Liao on 2020-06-17 03:31.
 */
public class T4MExtractor {

	private static final Logger LOGGER = LoggerFactory.getLogger(T4MExtractor.class);
	// private ProjectInfo projectInfo;
	//
	// public T4MExtractor(ProjectInfo projectInfo) {
	// 	this.projectInfo = projectInfo;
	// }
	//
	// public ProjectInfo getProjectInfo() {
	// 	return projectInfo;
	// }

	private ScannerChain scannerChain = new ScannerChain();

	public ScannerChain getScannerChain() {
		return scannerChain;
	}

	// public void scanDirectory() {
	// 	No1_DirectoryFileScanner directoryFileScanner = new No1_DirectoryFileScanner(projectInfo);
	// 	rawJavaFileList = directoryFileScanner.scan();
	// }
	//
	// public void scanClass() {
	// 	scanDirectory();
	// 	No2_ClassScanner classScanner = new No2_ClassScanner(projectInfo);
	// 	classScanner.scan(rawJavaFileList);
	// }
	//
	// public void scanPackage() {
	// 	scanClass();
	// 	No3_PackageScanner packageScanner = new No3_PackageScanner(projectInfo);
	// 	packageScanner.scan();
	// }
	//
	// public void scanModule() {
	// 	scanPackage();
	// 	No4_ModuleScanner moduleScanner = new No4_ModuleScanner(projectInfo);
	// 	moduleScanner.scan();
	// }
	//
	// public void scanDependency() {
	// 	scanModule();
	// 	No5_DependencyScanner dependencyScanner = new No5_DependencyScanner(projectInfo);
	// 	dependencyScanner.scan();
	// }
	//
	// public void scanJavaParser() {
	// 	scanDependency();
	// 	No6_JavaParserScanner javaParserScanner = new No6_JavaParserScanner(projectInfo);
	// 	javaParserScanner.scan();
	// }
	//
	// public void scanMetricData() {
	// 	scanJavaParser();
	// 	No7_MetricsScanner metricsScanner = new No7_MetricsScanner(projectInfo);
	// 	metricsScanner.scan();
	// }

	public ProjectInfo extract(ProjectInfo projectInfo) {
		if (scannerChain.isEmpty()) {
			useDefaultScannerChain();
		}
		LOGGER.info("************************************* Start scanning **************************************");
		scannerChain.scan(projectInfo);
		LOGGER.info("************************************* Finish scanning *************************************");
		return projectInfo;
	}

	private void useDefaultScannerChain() {
		scannerChain.addScanner(new DirectoryFileScanner());
		scannerChain.addScanner(new ClassScanner());
		scannerChain.addScanner(new PackageScanner());
		scannerChain.addScanner(new ModuleScanner());
		scannerChain.addScanner(new DependencyScanner());
		scannerChain.addScanner(new JavaParserScanner());
		scannerChain.addScanner(new MetricsScanner());
	}

	public T4MExtractor setCustomScannerChain(T4MScanner... t4MScanners) {
		Arrays.asList(t4MScanners).forEach(scannerChain::addScanner);
		return this;
	}

}
