package com.t4m.extractor.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
	private List<ClassInfo> nestedClassList = new ArrayList<>();
	private List<ClassInfo> extraClassList = new ArrayList<>();

	// metric meta data
	private List<PackageInfo> activeDependencyAkaFanOutList = new ArrayList<>();//依赖
	private List<PackageInfo> passiveDependencyAkaFanInList = new ArrayList<>();//被依赖

	//basic
	private int numberOfJavaFile;
	private int numberOfAllClass;
	//sloc
	private int[] slocArrayForCurrentPkg; //自身直接持有的外部类的SLOC（外部类的SLOC以及包括了内部类的SLOC）
	private int[] slocArrayForCurrentAndSubPkg; //自身直接持有的外部类的SLOC（外部类的SLOC以及包括了内部类的SLOC），以及子包的SLOC，

	// Coupling
	private int afferentCoupling; // fanin
	private int efferentCoupling; // fanout
	private String instability; // fanout/fanin+out
	private String abstractness; //一个组件中抽象类和接口的数量与所有类的数量的比例

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

	public List<ClassInfo> getNestedClassList() {
		return nestedClassList;
	}

	public void setNestedClassList(List<ClassInfo> nestedClassList) {
		this.nestedClassList = nestedClassList;
	}

	public List<ClassInfo> getExtraClassList() {
		return extraClassList;
	}

	public void setExtraClassList(List<ClassInfo> extraClassList) {
		this.extraClassList = extraClassList;
	}

	public List<PackageInfo> getActiveDependencyAkaFanOutList() {
		return activeDependencyAkaFanOutList;
	}

	public void setActiveDependencyAkaFanOutList(
			List<PackageInfo> activeDependencyAkaFanOutList) {
		this.activeDependencyAkaFanOutList = activeDependencyAkaFanOutList;
	}

	public List<PackageInfo> getPassiveDependencyAkaFanInList() {
		return passiveDependencyAkaFanInList;
	}

	public void setPassiveDependencyAkaFanInList(
			List<PackageInfo> passiveDependencyAkaFanInList) {
		this.passiveDependencyAkaFanInList = passiveDependencyAkaFanInList;
	}

	public String getAbstractness() {
		return abstractness;
	}

	public void setAbstractness(String abstractness) {
		this.abstractness = abstractness;
	}

	public int getAfferentCoupling() {
		return afferentCoupling;
	}

	public void setAfferentCoupling(int afferentCoupling) {
		this.afferentCoupling = afferentCoupling;
	}

	public int getEfferentCoupling() {
		return efferentCoupling;
	}

	public void setEfferentCoupling(int efferentCoupling) {
		this.efferentCoupling = efferentCoupling;
	}

	public String getInstability() {
		return instability;
	}

	public void setInstability(String instability) {
		this.instability = instability;
	}

	public int getNumberOfJavaFile() {
		return numberOfJavaFile;
	}

	public void setNumberOfJavaFile(int numberOfJavaFile) {
		this.numberOfJavaFile = numberOfJavaFile;
	}

	public int getNumberOfAllClass() {
		return numberOfAllClass;
	}

	public void setNumberOfAllClass(int numberOfAllClass) {
		this.numberOfAllClass = numberOfAllClass;
	}

	public int[] getSlocArrayForCurrentPkg() {
		return slocArrayForCurrentPkg;
	}

	public void setSlocArrayForCurrentPkg(int[] slocArrayForCurrentPkg) {
		this.slocArrayForCurrentPkg = slocArrayForCurrentPkg;
	}

	public int[] getSlocArrayForCurrentAndSubPkg() {
		return slocArrayForCurrentAndSubPkg;
	}

	public void setSlocArrayForCurrentAndSubPkg(int[] slocArrayForCurrentAndSubPkg) {
		this.slocArrayForCurrentAndSubPkg = slocArrayForCurrentAndSubPkg;
	}

	/**
	 * 返回所有类，包括唯一公共类，非公共类，嵌套类
	 */
	public List<ClassInfo> getAllClassList() {
		List<ClassInfo> all = new ArrayList<>();
		all.addAll(classList);
		all.addAll(nestedClassList);
		all.addAll(extraClassList);
		return all;
	}
}
