package com.t4m.extractor.scanner;

import com.t4m.extractor.entity.ProjectInfo;

public interface T4MScanner {
	void scan(ProjectInfo projectInfo, ScannerChain scannerChain);
}
