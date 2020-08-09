package com.t4m.web.service;

import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.ModuleInfo;
import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.util.EntityUtil;
import com.t4m.extractor.util.MathUtil;
import com.t4m.extractor.util.TimeUtil;
import com.t4m.web.util.ProjectRecordUtil;
import com.t4m.web.util.dataset.SLOCDatasetUtil;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by Yuxiang Liao on 2020-06-26 13:57.
 */
@Service("PackageService")
public class PackageService {

	public PackageInfo getPackageInfoByQualifiedName(ProjectInfo projectInfo, String packageQualifiedName) {
		return EntityUtil.getPackageByQualifiedName(projectInfo.getPackageList(), packageQualifiedName);
	}

	public List<String[]> getSLOCForTableChart(String pkgName, boolean includeSubPkgSLOC) {
		List<String[]> dataset = new ArrayList<>();
		dataset.add(new String[]{"time", "Logic Code Lines (Source File)", "Physical Code Lines (Source File)",
		                         "Comment Lines", "% of Comment Lines (Source File)", "Logic Code Lines (JavaParser)",
		                         "Physical Code Lines (JavaParser)", "Comment Lines (JavaParser)",
		                         "% of Comment Lines (JavaParser)"});
		for (ProjectInfo projectInfo : ProjectRecordUtil.getProjectInfoList()) {
			PackageInfo packageInfo = EntityUtil.getPackageByQualifiedName(projectInfo.getPackageList(), pkgName);
			String[] tempRow = new String[9];
			tempRow[0] = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			Arrays.fill(tempRow, 1, 9, null);
			if (packageInfo != null) {
				int[] slocArray;
				if (includeSubPkgSLOC) {
					slocArray = packageInfo.getSlocArrayForCurrentAndSubPkg();
				} else {
					slocArray = packageInfo.getSlocArrayForCurrentPkg();
				}
				SLOCDatasetUtil.insertCommonRowsForTableChart(tempRow,slocArray);
			}
			dataset.add(tempRow);
		}
		return dataset;
	}

	public List<Map<String, Object>> getBasicInfoForTable(ProjectInfo projectInfo) {
		List<Map<String, Object>> rows = new ArrayList<>();
		for (PackageInfo packageInfo : projectInfo.getPackageList()) {
			Map<String, Object> row = new LinkedHashMap<>();
			row.put("name", packageInfo.getFullyQualifiedName());
			row.put("classNum", packageInfo.getNumberOfJavaFile() + " / " + packageInfo.getNumberOfAllClass());
			row.put("module", packageInfo.getModuleInfo().getShortName());
			rows.add(row);
		}
		return rows;
	}

	public Map<String, List<List<Object>>> getInstabilityAbstractnessGraphForOverviewChart() {
		LinkedHashMap<String, List<List<Object>>> packageInstabilityAbstractnessGraphDataset = new LinkedHashMap<>();
		for (ProjectInfo projectInfo : ProjectRecordUtil.getProjectInfoList()) {
			String time = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			List<List<Object>> rows = new ArrayList<>();
			for (PackageInfo packageInfo : projectInfo.getPackageList()) {
				List<Object> cols = new ArrayList<>();
				float instability = Float.parseFloat(packageInfo.getInstability());
				float abstractness = Float.parseFloat(packageInfo.getAbstractness());
				cols.add(instability);
				cols.add(abstractness);
				cols.add(MathUtil.abs(instability + abstractness - 1));
				cols.add(packageInfo.getFullyQualifiedName());
				cols.add(packageInfo.getModuleInfo().getRelativePath());
				rows.add(cols);
			}
			packageInstabilityAbstractnessGraphDataset.put(time, rows);
		}
		return packageInstabilityAbstractnessGraphDataset;
	}

	public List<Map<String, Object>> getCouplingForTable(ProjectInfo projectInfo) {
		List<Map<String, Object>> rows = new ArrayList<>();
		for (PackageInfo packageInfo : projectInfo.getPackageList()) {
			Map<String, Object> row = new LinkedHashMap<>();
			row.put("name", packageInfo.getFullyQualifiedName());
			row.put("module", packageInfo.getModuleInfo().getShortName());
			row.put("afferentCoupling", packageInfo.getAfferentCoupling());
			row.put("efferentCoupling", packageInfo.getEfferentCoupling());
			row.put("instability", packageInfo.getInstability());
			row.put("abstractness", packageInfo.getAbstractness());
			float instability = Float.parseFloat(packageInfo.getInstability());
			float abstractness = Float.parseFloat(packageInfo.getAbstractness());
			row.put("distance", MathUtil.abs(instability + abstractness - 1));
			rows.add(row);
		}
		return rows;
	}

	public List<Object[]> getCouplingForTableChart(String qualifiedName) {
		//	第一行是系列名，从第二行开始，每一行是一条记录的数据，其中第一列是时间
		List<Object[]> dataset = new ArrayList<>();
		dataset.add(new String[]{"time", "Afferent Coupling", "Efferent Coupling", "Instability", "Abstractness",
		                         "Distance from Main Sequence"});
		for (ProjectInfo projectInfo : ProjectRecordUtil.getProjectInfoList()) {
			PackageInfo packageInfo = EntityUtil.getPackageByQualifiedName(projectInfo.getPackageList(), qualifiedName);
			Object[] tempRow = new Object[6];
			tempRow[0] = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			Arrays.fill(tempRow, 1, 4, null);
			if (packageInfo != null) { //类可能还未创建或已经删除
				tempRow[1] = packageInfo.getAfferentCoupling();
				tempRow[2] = packageInfo.getEfferentCoupling();
				tempRow[3] = packageInfo.getInstability();
				tempRow[4] = packageInfo.getAbstractness();
				float instability = Float.parseFloat(packageInfo.getInstability());
				float abstractness = Float.parseFloat(packageInfo.getAbstractness());
				tempRow[5] = MathUtil.abs(instability + abstractness - 1);
			}
			dataset.add(tempRow);
		}
		return dataset;
	}

	public List<Map<String, Object>> getSLOCForTable(ModuleInfo moduleInfo) {
		List<Map<String, Object>> rows = new ArrayList<>();
		for (PackageInfo packageInfo : moduleInfo.getPackageList()) {
			if (!packageInfo.hasPreviousPackage()) {
				Map<String, Object> row = new LinkedHashMap<>();
				row.put("name", packageInfo.getFullyQualifiedName());
				row.put("module", moduleInfo.getRelativePath());
				row.put("level", "package");
				int[] slocArray = packageInfo.getSlocArrayForCurrentAndSubPkg();
				SLOCDatasetUtil.insertCommonRowsForTable(row, slocArray);
				rows.add(row);
			}
		}
		return rows;
	}

	public List<Map<String, Object>> getSLOCForTable(PackageInfo packageInfo) {
		List<Map<String, Object>> rows = new ArrayList<>();
		int[] slocArray;
		for (PackageInfo subPkgInfo : packageInfo.getSubPackageList()) {
			Map<String, Object> subPkgRow = new LinkedHashMap<>();
			subPkgRow.put("name", subPkgInfo.getFullyQualifiedName());
			subPkgRow.put("module", subPkgInfo.getModuleInfo().getRelativePath());
			subPkgRow.put("level", "package");
			slocArray = subPkgInfo.getSlocArrayForCurrentAndSubPkg();
			SLOCDatasetUtil.insertCommonRowsForTable(subPkgRow, slocArray);
			rows.add(subPkgRow);
		}
		Map<String, Object> currentPkgRow = new LinkedHashMap<>();
		currentPkgRow.put("name", packageInfo.getFullyQualifiedName());
		currentPkgRow.put("module", packageInfo.getModuleInfo().getRelativePath());
		currentPkgRow.put("level", "current package");
		slocArray = packageInfo.getSlocArrayForCurrentPkg();
		SLOCDatasetUtil.insertCommonRowsForTable(currentPkgRow, slocArray);
		rows.add(currentPkgRow);
		for (ClassInfo classInfo : packageInfo.getClassList()) {
			Map<String, Object> classRow = new LinkedHashMap<>();
			classRow.put("name", classInfo.getShortName());
			classRow.put("module", classInfo.getPackageInfo().getModuleInfo().getRelativePath());
			classRow.put("qualifiedName", classInfo.getFullyQualifiedName());
			classRow.put("level", "class");
			slocArray = classInfo.getSlocArray();
			SLOCDatasetUtil.insertCommonRowsForTable(classRow, slocArray);
			rows.add(classRow);
		}
		return rows;
	}
}
