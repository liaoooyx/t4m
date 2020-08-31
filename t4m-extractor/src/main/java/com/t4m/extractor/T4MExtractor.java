package com.t4m.extractor;

import com.t4m.conf.GlobalProperties;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.processor.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Created by Yuxiang Liao on 2020-06-17 03:31.
 */
public class T4MExtractor {

	private static final Logger LOGGER = LoggerFactory.getLogger(T4MExtractor.class);

	private final ProcessChain processChain = new ProcessChain();

	public ProcessChain getProcessChain() {
		return processChain;
	}

	public ProjectInfo extract(ProjectInfo projectInfo) {
		if (processChain.isEmpty()) {
			useDefaultScannerChain();
		}
		long start = System.currentTimeMillis();
		LOGGER.info("************************************* Start scanning **************************************");
		processChain.scan(projectInfo);
		LOGGER.info("************************************* Finish scanning *************************************");
		long end = System.currentTimeMillis();
		LOGGER.info("The scanning process finished in {}s", (end - start) / 1000);
		return projectInfo;
	}

	private void useDefaultScannerChain() {
		processChain.addScanner(new DirectoryFileScanner());
		processChain.addScanner(new ClassScanner());
		processChain.addScanner(new PackageScanner());
		processChain.addScanner(new ModuleScanner());
		processChain.addScanner(new BelongingnessScanner());
		processChain.addScanner(new SourceCodeResolver());
		processChain.addScanner(new MetricsCalculator());
	}

	public T4MExtractor setCustomScannerChain(ProcessNode... t4MProcesses) {
		Arrays.asList(t4MProcesses).forEach(processChain::addScanner);
		return this;
	}

	public static void main(String[] args) {
		ProjectInfo projectInfo = new ProjectInfo("/Users/liao/myProjects/IdeaProjects/t4m", GlobalProperties.DEFAULT_EXCLUDED_PATH,
		                                          GlobalProperties.DEFAULT_DEPENDENCY_PATH);
		T4MExtractor t4MExtractor = new T4MExtractor();
		t4MExtractor.extract(projectInfo);
		System.out.println();
	}

}
