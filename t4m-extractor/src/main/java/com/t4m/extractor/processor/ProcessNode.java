package com.t4m.extractor.processor;

import com.t4m.extractor.entity.ProjectInfo;

public interface ProcessNode {
	void scan(ProjectInfo projectInfo, ProcessChain processChain);
}
