package com.t4m.extractor.scanner;

import com.t4m.extractor.entity.ProjectInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Yuxiang Liao on 2020-06-17 02:43.
 */
public class DirectoryFileScanner implements T4MScanner {

	public static final Logger LOGGER = LoggerFactory.getLogger(DirectoryFileScanner.class);

	@Override
	public void scan(ProjectInfo projectInfo, ScannerChain scannerChain) {
		LOGGER.info("Scanning all .java file from [{}]", projectInfo.getAbsolutePath());
		LOGGER.info("Files that contain the following paths will be excluded: [{}]", projectInfo.getExcludedPath());
		List<File> rawJavaFileList = new ArrayList<>();
		File root = new File(projectInfo.getAbsolutePath());
		getAllJavaFiles(root, rawJavaFileList, projectInfo);
		scannerChain.setRawJavaFileList(rawJavaFileList);
		scannerChain.scan(projectInfo);
	}

	/**
	 * Start from the project root path，recursively search for all .java file.
	 * The {@link ProjectInfo#getExcludedPath()} will be excluded.
	 */
	private void getAllJavaFiles(File file, List<File> javaList, ProjectInfo projectInfo) {
		if (file.isDirectory()) {
			File[] fileArray = file.listFiles(pathname -> {
				String[] exclusions = projectInfo.getExcludedPath().split(";");
				for (String exclusion : exclusions) {
					if (!"".equals(exclusion) && pathname.getAbsolutePath().contains(exclusion))
						return false;
				}
				return true;
			});
			Arrays.stream(fileArray).forEach(f -> getAllJavaFiles(f, javaList, projectInfo));
		} else {
			if (file.getName().endsWith(".java")) {
				javaList.add(file);
			}
		}
	}


}
