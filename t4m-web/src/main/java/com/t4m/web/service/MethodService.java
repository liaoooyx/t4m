package com.t4m.web.service;

import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.MethodInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.util.TimeUtil;
import com.t4m.web.util.ProjectRecordUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yuxiang Liao on 2020-08-08 22:24.
 */
@Service("MethodService")
public class MethodService {

	public List<Object[]> getMethodComplexityForOverviewChart() {
		List<Object[]> methodComplexityDataset = new ArrayList<>();
		for (ProjectInfo projectInfo : ProjectRecordUtil.getProjectInfoList()) {
			String time = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			for (MethodInfo methodInfo : projectInfo.getMethodList()) {
				Object[] row =
						new Object[]{time, methodInfo.getCyclomaticComplexity(), methodInfo.getFullyQualifiedName(),
						             methodInfo.getMethodDeclarationString()};
				methodComplexityDataset.add(row);
			}
		}
		return methodComplexityDataset;
	}

	public List<Object[]> getNumberOfMethodForOverviewChart() {
		List<Object[]> methodNumDataset = new ArrayList<>();
		for (ProjectInfo projectInfo : ProjectRecordUtil.getProjectInfoList()) {
			String time = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			Object[] row = new Object[]{time, projectInfo.getMethodList().size()};
			methodNumDataset.add(row);
		}
		return methodNumDataset;
	}

	public List<Map<String, Object>> getComplexityForTable(ClassInfo classInfo) {
		List<Map<String, Object>> rows = new ArrayList<>();
		for (MethodInfo methodInfo : classInfo.getMethodInfoList()) {
			Map<String, Object> row = new LinkedHashMap<>();
			row.put("name", methodInfo.getShortName());
			row.put("declaration", methodInfo.getMethodDeclarationString());
			row.put("class", methodInfo.getClassInfo().getFullyQualifiedName());
			row.put("module", methodInfo.getClassInfo().getPackageInfo().getModuleInfo().getShortName());
			row.put("cyclomaticComplexity", methodInfo.getCyclomaticComplexity());
			row.put("qualifiedName", methodInfo.getFullyQualifiedName());
			rows.add(row);
		}
		return rows;
	}
}
