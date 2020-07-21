package com.simulation.core;

public class CohesionClass {

	int a,b,c,d,e,f,g;
	
	void a() {
		System.out.println(a);
		System.out.println(b);
	}
	
	void b() {
		System.out.println(a);
		System.out.println(b);
	}
	
	void c() {
		b();
		System.out.println(c);
		c();
	}
	
	void d() {
		System.out.println(c);
	}
	
	void e() {
		System.out.println(d);
	}
	
	void f() {
		System.out.println(e);
	}
	
	void g() {
		System.out.println(g);
	}
	
	void h() {
		System.out.println(e);
		System.out.println(g);
	}
	
}
