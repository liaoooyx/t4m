package com.simulation.core.bar;

import com.simulation.core.CyclomaticComplexityClass;

/**
 * Created by Yuxiang Liao on 2020-06-21 15:35.
 */
public class SimpleClassC extends SimpleClassB implements SimpleInterfaceC, SimpleInterfaceA {

	public CyclomaticComplexityClass getXooClassA() {
		return new CyclomaticComplexityClass();
	}
}
