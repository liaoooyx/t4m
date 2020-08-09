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

	private ScannerChain scannerChain = new ScannerChain();

	public ScannerChain getScannerChain() {
		return scannerChain;
	}


	public ProjectInfo extract(ProjectInfo projectInfo) {
		if (scannerChain.isEmpty()) {
			useDefaultScannerChain();
		}
		long start = System.currentTimeMillis();
		LOGGER.info("************************************* Start scanning **************************************");
		scannerChain.scan(projectInfo);
		LOGGER.info("************************************* Finish scanning *************************************");
		long end = System.currentTimeMillis();
		LOGGER.info("The scanning process finished in {}s",(end-start)/1000);
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
