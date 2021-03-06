package com.t4m.web.service;

import com.t4m.extractor.entity.ModuleInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.util.EntityUtil;
import com.t4m.web.util.ProjectRecordUtil;
import com.t4m.web.util.dataset.SLOCDatasetUtil;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by Yuxiang Liao on 2020-06-26 13:48.
 */
@Service("ModuleService")
public class ModuleService {

	public ModuleInfo getModuleInfoByPath(ProjectInfo projectInfo, String moduleRelativePath) {
		return EntityUtil.getModuleByRelativeName(projectInfo.getModuleList(), moduleRelativePath);
	}

	public List<String[]> getSLOCForTableChart(String moduleName) {
		List<String[]> dataset = new ArrayList<>();
		dataset.add(new String[]{"time", "Source Code Lines (Source File)", "Comment Lines",
		                         "% of Comment Lines (Source File)", "Source Code Lines (JavaParser)",
		                         "Comment Lines (JavaParser)", "% of Comment Lines (JavaParser)"});
		for (ProjectInfo projectInfo : ProjectRecordUtil.getProjectInfoList()) {
			ModuleInfo moduleInfo = EntityUtil.getModuleByRelativeName(projectInfo.getModuleList(), moduleName);
			String[] tempRow;
			if (moduleInfo == null) {
				tempRow = SLOCDatasetUtil.formatRowForTableChart(projectInfo.getCreateDate());
			} else {
				tempRow = SLOCDatasetUtil.formatRowForTableChart(projectInfo.getCreateDate(),
				                                                 moduleInfo.getSlocArray());
			}
			dataset.add(tempRow);
		}
		return dataset;
	}

	public List<Map<String, Object>> getBasicInfoForTable(ProjectInfo projectInfo) {
		List<Map<String, Object>> rows = new ArrayList<>();
		for (ModuleInfo moduleInfo : projectInfo.getModuleList()) {
			Map<String, Object> row = new LinkedHashMap<>();
			row.put("name", moduleInfo.getRelativePath());
			row.put("packageNum", moduleInfo.getPackageList().size());
			row.put("classNum", moduleInfo.getNumberOfJavaFile() + " / " + moduleInfo.getNumberOfAllClass());
			rows.add(row);
		}
		return rows;
	}

	public List<Map<String, Object>> getSLOCForTable(ProjectInfo projectInfo) {
		List<Map<String, Object>> rows = new ArrayList<>();
		int[] sumArray = new int[8];
		for (ModuleInfo moduleInfo : projectInfo.getModuleList()) {
			Map<String, Object> row = new LinkedHashMap<>();
			row.put("name", moduleInfo.getRelativePath());
			row.put("level", "module");
			int[] slocArray = moduleInfo.getSlocArray();
			SLOCDatasetUtil.insertCommonRowsForTable(row, slocArray);
			rows.add(row);
			SLOCDatasetUtil.sumSLOC(sumArray, moduleInfo.getSlocArray());
		}
		Map<String, Object> sumRow = new LinkedHashMap<>();
		sumRow.put("name", "TOTAL");
		sumRow.put("level", "project");
		SLOCDatasetUtil.insertCommonRowsForTable(sumRow, sumArray);
		rows.add(sumRow);
		return rows;
	}

	private int sumColumnToInt(List<Map<String, Object>> rows, String colName) {
		return rows.stream().mapToInt(ele -> (int) ele.get(colName)).sum();
	}

	private String sumColumnToDecimal(List<Map<String, Object>> rows, String colName) {
		double sum = rows.stream().mapToDouble(ele -> {
			String val = String.valueOf(ele.get(colName));
			return Double.parseDouble(val);
		}).sum();
		return new DecimalFormat("0.00").format(sum);
	}

}
