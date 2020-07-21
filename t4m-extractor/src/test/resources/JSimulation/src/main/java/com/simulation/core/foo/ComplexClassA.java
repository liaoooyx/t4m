package com.simulation.core.foo;
// haha
import java.util.ArrayList;
import java.util.HashMap; // haha
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.simulation.core.CoreClass;
import com.simulation.core.bar.SimpleAbstractClass;
import com.simulation.core.bar.SimpleClassA;
import com.simulation.core.bar.SimpleClassB;
import com.simulation.core.bar.SimpleClassC;
import com.simulation.core.bar.SimpleInterfaceA;
import com.simulation.core.bar.SimpleInterfaceB;

/**
 * Created by Yuxiang Liao on 2020-06-21 15:26.
 */
public class ComplexClassA extends ComplexAbstractClass implements SimpleInterfaceA,SimpleInterfaceB {

	SimpleClassA[] array1,array2;
	Map<String, SimpleClassC> map;
	// 依赖SimpleInterfaceA，SimpleClassA
	SimpleInterfaceA simpleInterfaceA = new SimpleClassA();//haha
	// 依赖SimpleClassB，但不依赖SimpleInterfaceB
	SimpleClassB simpleClassB;
	static SimpleClassC simpleClassC;
	int a;
	com.simulation.core.bar.SimpleInterfaceD simpB;
	List<SimpleClassA> list;
	Map<SimpleClassC, SimpleClassB> map22;
	ComplexClassB.InnerClassOfB cB = new ComplexClassB().innerClassOfComplexClassB;
	private static final int aaa = 10;
	InnnerClassOfComplexClassA innnerCA;
	
	static {
		simpleClassC = ComplexClassC.initSimpleClassC();
	}
	
	{
		ComplexClassB classB = new ComplexClassB();
	}

	public ComplexClassA() {
		super();
		this.simpleClassB = new SimpleClassB();list.forEach(e->{System.out.println("hah");});/*asd"asd
		"as"dasd
		//asda"sd*///System.out.println();
		invokeInnerClassC(new CoreClass());
	}
	
	public ComplexClassA(int a) {
		this();
		String string = "/*asdasd\n" + 
				"		asdasd\n" + 
				"		asdasd*/";
	}

	/**
	 * 依赖SimpleClassC，XooClassA
	 */
	public CoreClass referSimpleClassCInParams(Object simpleClassB) {
		SimpleClassC sC = (SimpleClassC) simpleClassC;
		this.simpleClassB = null;
		simpB = null;
		Class class1 = ComplexClassA.class;
		Consumer<ComplexClassD> f = ComplexClassA.InnnerClassOfComplexClassA::method;
		return new CoreClass();
	}

	/**
	 * 依赖ComplexClassB, ComplexClassB$InnerClassA，CoreClass
	 */
	public void invokeInnerClassC(CoreClass xooClassA) {
		new ComplexClassB().new InnerClassOfB().method(1,2);
		ComplexClassB cbB = new ComplexClassB();
		ComplexClassB.InnerClassOfB cibB = cbB.innerClassOfComplexClassB;
		cbB.innerClassOfComplexClassB.method(1,2).invokeInnerClassA().contains("");
		cibB.method(1,2);
		cbB.innerClassOfComplexClassB.myselfB.method(1,2);
	}

	/**
	 * 依赖ComplexClassC, 依赖ComplexClassC$InnerClassC
	 */
	public void invokeInnerClassFromComplexClassC() {
		ComplexClassC.innerClassA.method("c");
	}

	/**
	 * 依赖ComplexClassD$InnerClassA，依赖ComplexClassD
	 */
	public ComplexClassC invokeInnerClassA() {
		ComplexClassD.InnerClassOfD.method(new SimpleClassA());
		return new ComplexClassC();
	}
	
	public SimpleClassA[] callArray() {
		callList(new ArrayList<SimpleClassA>(), new HashMap<String, SimpleClassC>());
		new ComplexClassB().new InnerClassOfB().method(1,2);
		SimpleClassA[] arrayA = new SimpleClassA[10],arryB=null;
		return arrayA;
	}
	
	public SimpleClassA[] callArray(SimpleClassA...arrA) {
		return arrA;
	}
	
	public List<SimpleClassA> callList(List<SimpleClassA> listA, Map<String, SimpleClassC> mapC) {
		return listA;
	}
	
	public void multiParams(InnnerClassOfComplexClassA classA,ComplexClassC.InnerClassOfC classA2) {
		
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

		public static void method(ComplexClassD d) {
		}

		@Override
		public String toString() {
			return "InnnerClassOfComplexClassA{" + "field1=" + field1 + ", field2='" + field2 + '\'' + ", classA=" +
					classA + '}';
		}
	}

}

class ExtraClass{
	class InnerClassOfExtraClass{
		class NestedInnerClass{
			
		}
	}
	class OtherInnerClassOfExtraClass{
		
	}
}
