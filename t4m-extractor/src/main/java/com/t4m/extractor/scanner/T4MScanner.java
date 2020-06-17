package com.t4m.extractor.scanner;

import com.t4m.extractor.entity.ProjectInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yuxiang Liao on 2020-06-17 03:31.
 */
public class T4MScanner {
	private ProjectInfo projectInfo;
	private List<File> rawJavaFileList;

	public T4MScanner(ProjectInfo projectInfo) {
		this.projectInfo = projectInfo;
	}

	public ProjectInfo getProjectInfo() {
		return projectInfo;
	}

	public List<File> getRawJavaFileList() {
		return rawJavaFileList;
	}

	public void scanDirectory(){
		DirectoryScanner directoryScanner = new DirectoryScanner(projectInfo);
		rawJavaFileList = directoryScanner.scan();
	}

	public void scanClassAndDirectory(){
		scanDirectory();
		ClassScanner classScanner = new ClassScanner(projectInfo);
		classScanner.scan(rawJavaFileList);
	}

	public void scanPackageAndClassAndDirectory(){
		scanClassAndDirectory();
		PackageScanner packageScanner = new PackageScanner(projectInfo);
		packageScanner.scan();
	}

	public void scanModuleAndPackageAndClassAndDirectory(){
		scanPackageAndClassAndDirectory();
		ModuleScanner moduleScanner = new ModuleScanner(projectInfo);
		moduleScanner.scan();
	}
}
