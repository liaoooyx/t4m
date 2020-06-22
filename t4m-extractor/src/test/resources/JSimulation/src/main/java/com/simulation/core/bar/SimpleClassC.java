package com.simulation.core.bar;

import com.simulation.core.xoo.XooClassA;

/**
 * Created by Yuxiang Liao on 2020-06-21 15:35.
 */
public class SimpleClassC extends SimpleAbstractClass implements SimpleInterfaceC, SimpleInterfaceA {

	public XooClassA getXooClassA() {
		return new XooClassA();
	}
}
