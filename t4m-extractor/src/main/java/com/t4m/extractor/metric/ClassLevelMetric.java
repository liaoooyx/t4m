package com.t4m.extractor.metric;

import com.t4m.extractor.entity.ClassInfo;

public interface ClassLevelMetric {
	void calculate(ClassInfo classInfo);
}
