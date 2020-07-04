package com.t4m.extractor.entity;

import com.t4m.extractor.metric.SLOCMetric;

import java.io.Serializable;
import java.util.*;

/**
 * 根据{@code absolutePath}来判断对象是否一致
 */
public class PackageInfo implements Serializable {

	private static final long serialVersionUID = 1661151854125377881L;
	public static String EMPTY_IDENTIFIER = "(null)";

	private String fullyQualifiedName; // = (null), if doesn't have package
	private String absolutePath;

	private ModuleInfo moduleInfo;

	private PackageInfo previousPackage;
	private List<PackageInfo> subPackageList = new ArrayList<>();
	private List<ClassInfo> classList = new ArrayList<>(); // 目前不包括内部类，只有外部类

	// private Map<PackageInfo, Integer> dependsOn;
	// private Map<PackageInfo, Integer> dependedBy;

	private int numberOfClasses;
	private int numberOfInnerClasses;

	public PackageInfo(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		PackageInfo that = (PackageInfo) o;
		return Objects.equals(absolutePath, that.absolutePath);
	}

	@Override
	public int hashCode() {
		return Objects.hash(absolutePath);
	}

	public String getFullyQualifiedName() {
		return fullyQualifiedName;
	}

	public void setFullyQualifiedName(String fullyQualifiedName) {
		this.fullyQualifiedName = fullyQualifiedName;
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	public ModuleInfo getModuleInfo() {
		return moduleInfo;
	}

	public void setModuleInfo(ModuleInfo moduleInfo) {
		this.moduleInfo = moduleInfo;
	}

	public PackageInfo getPreviousPackage() {
		return previousPackage;
	}

	public void setPreviousPackage(PackageInfo previousPackage) {
		this.previousPackage = previousPackage;
	}

	public boolean hasPreviousPackage() {
		return previousPackage != null ? true : false;
	}

	public List<PackageInfo> getSubPackageList() {
		return subPackageList;
	}

	public void setSubPackageList(List<PackageInfo> subPackageList) {
		this.subPackageList = subPackageList;
	}

	public List<ClassInfo> getClassList() {
		return classList;
	}

	public void setClassList(List<ClassInfo> classList) {
		this.classList = classList;
	}

	public int getNumberOfClasses() {
		if (numberOfClasses == 0) {
			numberOfClasses += this.getClassList().size();
		}
		return numberOfClasses;
	}

	public void setNumberOfClasses(int numberOfClasses) {
		this.numberOfClasses = numberOfClasses;
	}

	//TODO 如果确实没有内部类，那么每次都要重新计算显得多余
	public int getNumberOfInnerClasses() {
		if (numberOfInnerClasses == 0) {
			for (ClassInfo classInfo : this.getClassList()) {
				numberOfInnerClasses += classInfo.getInnerClassList().size();
			}
		}
		return numberOfInnerClasses;
	}

	/**
	 * 获取自身直接持有的外部类的SLOC（外部类的SLOC以及包括了内部类的SLOC），以数组形式返回。索引与对应的值，查看{@link SLOCMetric sumSLOC()}
	 */
	public int[] getSumOfSLOCForCurrentPkg() {
		int[] slocArray = new int[6];
		Arrays.fill(slocArray, 0);
		for (ClassInfo classInfo : classList) {
			SLOCMetric.sumSLOC(slocArray, classInfo.getSumOfSLOC());
		}
		return slocArray;
	}

	/**
	 * 获取自身直接持有的外部类的SLOC（外部类的SLOC以及包括了内部类的SLOC），以及子包的SLOC，以数组形式返回。索引与对应的值，查看{@link SLOCMetric sumSLOC()}
	 */
	public int[] getSumOfSLOCForCurrentAndSubPkg() {
		int[] slocArray = getSumOfSLOCForCurrentPkg();
		for (PackageInfo subPackageInfo : subPackageList){
			SLOCMetric.sumSLOC(slocArray, subPackageInfo.getSumOfSLOCForCurrentAndSubPkg());
		}
		return slocArray;
	}

	public void setNumberOfInnerClasses(int numberOfInnerClasses) {
		this.numberOfInnerClasses = numberOfInnerClasses;
	}

}
