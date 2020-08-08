package com.t4m.extractor.metric;

import com.t4m.extractor.entity.ModuleInfo;

public interface ModuleLevelMetric {

	void calculate(ModuleInfo moduleInfo);
}
