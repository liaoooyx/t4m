package com.simulation.core.foo;

import com.simulation.core.bar.SimpleClassB;
import com.simulation.core.bar.SimpleClassC;

/**
 * Created by Yuxiang Liao on 2020-06-21 16:34.
 */
public class ComplexClassB {

	public InnerClassOfB innerClassOfComplexClassB = new InnerClassOfB();

	public SimpleClassB initSimpleClassB() {
		return new SimpleClassB();
	}

	public class InnerClassOfB {
		
		public InnerClassOfB myselfB = new InnerClassOfB();

		public ComplexClassA method(int a,int b) {
			return new ComplexClassA();
		}
	}
}
