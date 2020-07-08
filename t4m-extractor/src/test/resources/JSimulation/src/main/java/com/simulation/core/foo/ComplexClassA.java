package com.simulation.core.foo;

import java.util.List;
import java.util.Map;

import com.simulation.core.CoreClass;
import com.simulation.core.bar.SimpleAbstractClass;
import com.simulation.core.bar.SimpleClassA;
import com.simulation.core.bar.SimpleClassB;
import com.simulation.core.bar.SimpleClassC;
import com.simulation.core.bar.SimpleInterfaceA;
import com.simulation.core.xoo.XooClassA;

/**
 * Created by Yuxiang Liao on 2020-06-21 15:26.
 */
public class ComplexClassA extends ComplexAbstractClass {

	// 依赖SimpleInterfaceA，SimpleClassA
	SimpleInterfaceA simpleInterfaceA = new SimpleClassA();
	// 依赖SimpleClassB，但不依赖SimpleInterfaceB
	SimpleClassB simpleClassB;
	SimpleClassB simpleClassB2;
	static SimpleClassC simpleClassC;
	int a;
	com.simulation.core.xoo.XooClassA xooClassA;
	List<SimpleClassA> list;
	Map<String, SimpleClassC> map;
	SimpleClassA[] array;
	ComplexClassB.InnerClassOfB cB = new ComplexClassB().innerClassOfComplexClassB;


	static {
		simpleClassC = ComplexClassC.initSimpleClassC();
	}

	{
		ComplexClassB classB = new ComplexClassB();
		simpleClassB2 = classB.initSimpleClassB();

	}

	public ComplexClassA() {
		this.simpleClassB = new SimpleClassB();
		list.forEach(e->{});
	}

	/**
	 * 依赖SimpleClassC，XooClassA
	 */
	public XooClassA referSimpleClassCInParams(SimpleClassC simpleClassC) {
		return simpleClassC.getXooClassA();
	}

	/**
	 * 依赖ComplexClassB, ComplexClassB$InnerClassA，CoreClass
	 */
	public void invokeInnerClassC(CoreClass coreClass) {
		new ComplexClassB().new InnerClassOfB().method();
		ComplexClassB cbB = new ComplexClassB();
		ComplexClassB.InnerClassOfB cibB = cbB.innerClassOfComplexClassB;
		cbB.innerClassOfComplexClassB.method();
		cibB.method();
		cbB.innerClassOfComplexClassB.method();
		cbB.innerClassOfComplexClassB.myselfB.method();
	}

	/**
	 * 依赖ComplexClassC, 不依赖ComplexClassC$InnerClassC
	 */
	public void invokeInnerClassFromComplexClassC() {
		ComplexClassC.innerClassA.method();
	}

	/**
	 * 依赖ComplexClassD$InnerClassA，不依赖ComplexClassD
	 */
	public void invokeInnerClassA() {
		ComplexClassD.InnerClassOfD.method();
	}

	@Override
	public String toString() {
		return super.toString();
	}

	public static class InnnerClassOfComplexClassA {
		// test
		int field1;
		String field2;
		SimpleClassA classA;

		public static void method() {
		}

		@Override
		public String toString() {
			return "InnnerClassOfComplexClassA{" + "field1=" + field1 + ", field2='" + field2 + '\'' + ", classA=" +
					classA + '}';
		}
	}

}
