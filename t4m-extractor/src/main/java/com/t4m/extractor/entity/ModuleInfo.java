package com.t4m.extractor.entity;

import com.t4m.extractor.metric.SLOCMetric;

import java.io.Serializable;
import java.util.*;

/**
 * Created by Yuxiang Liao on 2020-06-11 09:29.
 */
public class ModuleInfo implements Serializable {

	private static final long serialVersionUID = 4453849096224682890L;
	//shortName和fullyQualifiedName都
	private String shortName;
	private String relativePath; // 从项目路径开始的相对路径
	private String absolutePath;

	private List<ModuleInfo> subModuleList = new ArrayList<>();
	private ModuleInfo previousModuleInfo;

	private List<PackageInfo> mainPackageList = new ArrayList<>();
	private List<PackageInfo> testPackageList = new ArrayList<>();
	private List<PackageInfo> otherPackageList = new ArrayList<>();

	private String mainScopePath;
	private String testScopePath;
	private String otherScopePath;

	private int numberOfClasses;
	private int numberOfInnerClasses;

	public ModuleInfo(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ModuleInfo that = (ModuleInfo) o;
		return Objects.equals(absolutePath, that.absolutePath);
	}

	@Override
	public int hashCode() {
		return Objects.hash(absolutePath);
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	public List<ModuleInfo> getSubModuleList() {
		return subModuleList;
	}

	public void setSubModuleList(List<ModuleInfo> subModuleList) {
		this.subModuleList = subModuleList;
	}

	public ModuleInfo getPreviousModuleInfo() {
		return previousModuleInfo;
	}

	public void setPreviousModuleInfo(ModuleInfo previousModuleInfo) {
		this.previousModuleInfo = previousModuleInfo;
	}

	public List<PackageInfo> getMainPackageList() {
		return mainPackageList;
	}

	public void setMainPackageList(List<PackageInfo> mainPackageList) {
		this.mainPackageList = mainPackageList;
	}

	public boolean hasMainPackageList() {
		return mainPackageList.size() > 0;
	}

	public List<PackageInfo> getTestPackageList() {
		return testPackageList;
	}

	public void setTestPackageList(List<PackageInfo> testPackageList) {
		this.testPackageList = testPackageList;
	}

	public boolean hasTestPackageList() {
		return testPackageList.size() > 0;
	}

	public List<PackageInfo> getOtherPackageList() {
		return otherPackageList;
	}

	public void setOtherPackageList(List<PackageInfo> otherPackageList) {
		this.otherPackageList = otherPackageList;
	}

	public boolean hasOtherPackageList() {
		return otherPackageList.size() > 0;
	}

	/**
	 * 优先返回mainPackageList，如果没有则返回otherPackageList。忽略testPackageList
	 */
	public List<PackageInfo> getPackageList() {
		if (hasMainPackageList()) {
			return mainPackageList;
		} else {
			return otherPackageList;
		}
	}

	public String getMainScopePath() {
		return mainScopePath;
	}

	public void setMainScopePath(String mainScopePath) {
		this.mainScopePath = mainScopePath;
	}

	public String getTestScopePath() {
		return testScopePath;
	}

	public void setTestScopePath(String testScopePath) {
		this.testScopePath = testScopePath;
	}

	public String getOtherScopePath() {
		return otherScopePath;
	}

	public void setOtherScopePath(String otherScopePath) {
		this.otherScopePath = otherScopePath;
	}

	public int getNumberOfClasses() {
		if (numberOfClasses == 0) {
			if (this.hasMainPackageList()) {
				for (PackageInfo packageInfo : this.getMainPackageList()) {
					numberOfClasses += packageInfo.getNumberOfClasses();
				}
			} else if (this.hasOtherPackageList()) {
				for (PackageInfo packageInfo : this.getOtherPackageList()) {
					numberOfClasses += packageInfo.getNumberOfClasses();
				}
			}
		}
		return numberOfClasses;
	}

	public void setNumberOfClasses(int numberOfClasses) {
		this.numberOfClasses = numberOfClasses;
	}

	public int getNumberOfInnerClasses() {
		if (numberOfInnerClasses == 0) {
			if (this.hasMainPackageList()) {
				for (PackageInfo packageInfo : this.getMainPackageList()) {
					numberOfInnerClasses += packageInfo.getNumberOfInnerClasses();
				}
			} else if (this.hasOtherPackageList()) {
				for (PackageInfo packageInfo : this.getOtherPackageList()) {
					numberOfInnerClasses += packageInfo.getNumberOfInnerClasses();
				}
			}
		}
		return numberOfInnerClasses;
	}

	public void setNumberOfInnerClasses(int numberOfInnerClasses) {
		this.numberOfInnerClasses = numberOfInnerClasses;
	}

	/**
	 * 获取自身直接持有的外部类的SLOC（外部类的SLOC以及包括了内部类的SLOC），以数组形式返回。索引与对应的值，查看{@link SLOCMetric.sumSLOC()}
	 */
	public int[] getSumOfSLOC() {
		int[] slocArray = new int[6];
		Arrays.fill(slocArray, 0);
		if (hasMainPackageList()) {
			for (PackageInfo packageInfo : mainPackageList) {
				SLOCMetric.sumSLOC(slocArray, packageInfo.getSumOfSLOCForCurrentPkg());
			}
		} else if (hasOtherPackageList()) {
			for (PackageInfo packageInfo : otherPackageList) {
				SLOCMetric.sumSLOC(slocArray, packageInfo.getSumOfSLOCForCurrentPkg());
			}
		}
		return slocArray;
	}
}
