package com.simulation.core.foo;

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

	public ComplexClassA() {
		this.simpleClassB = new SimpleClassB();
	}

	/**
	 * 依赖SimpleClassC，但不依赖XooClassA
	 */
	public XooClassA referSimpleClassCInParams(SimpleClassC simpleClassC) {
		return simpleClassC.getXooClassA();
	}

	/**
	 * 依赖ComplexClassB, 和ComplexClassB$InnerClassA
	 */
	public void invokeInnerClassC() {
		new ComplexClassB().new InnerClassOfB().method();
	}

	/**
	 * 依赖ComplexClassC, 不依赖ComplexClassC$InnerClassA
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
		//test
		int field1;
		int field2;
		String field2; //test
		SimpleClassA classA;

		//test
		public static void method() {
		}

		@Override
		public String toString() {
			return "InnnerClassOfComplexClassA{" + "field1=" + field1 + ", field2='" + field2 + '\'' + ", classA=" +
					classA + '}';
		}
	}

}
