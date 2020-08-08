package com.t4m.extractor.metric;

import com.t4m.extractor.entity.PackageInfo;

public interface PackageLevelMetric {
	void calculate(PackageInfo packageInfo);
}
