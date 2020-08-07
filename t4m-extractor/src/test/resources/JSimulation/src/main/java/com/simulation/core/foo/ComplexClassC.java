package com.simulation.core.foo;

import com.simulation.core.bar.SimpleClassC;

import java.util.LinkedList;

/**
 * Created by Yuxiang Liao on 2020-06-21 19:46.
 */
public class ComplexClassC extends LinkedList<String> {

	public static InnerClassOfC innerClassA;
	
	public String methString()	{
		return "";
	}
	
	public static SimpleClassC initSimpleClassC() {
		return new SimpleClassC();
	}

	public static class InnerClassOfC {

		public static void method(String str) {

		}
	}
}
