package com.t4m.extractor.processor;

import com.t4m.extractor.entity.ProjectInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yuxiang Liao on 2020-08-08 01:02.
 */
public class ProcessChain {

	List<ProcessNode> scannerList = new ArrayList<>();
	List<File> rawJavaFileList;

	private int index = 0;

	public void addScanner(ProcessNode scanner) {
		scannerList.add(scanner);
	}

	public ProcessNode getNextScanner() {
		return scannerList.get(index++);
	}

	public List<File> getRawJavaFileList() {
		return rawJavaFileList;
	}

	public boolean isEmpty() {
		return scannerList.isEmpty();
	}

	public void setRawJavaFileList(List<File> rawJavaFileList) {
		this.rawJavaFileList = rawJavaFileList;
	}

	public void scan(ProjectInfo projectInfo) {
		if (index == scannerList.size()) {
			return;
		}
		ProcessNode scanner = getNextScanner();
		scanner.scan(projectInfo, this);

	}


}
