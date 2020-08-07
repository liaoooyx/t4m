package com.simulation.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yuxiang Liao on 2020-06-21 15:41.
 */
public class CyclomaticComplexityClass {//+1
	public void name() {
		List list = new ArrayList<>();
		for (Object object : list) { //+1
			
		}
		int b=2;
		int i= b==1?1:2; //+1
		while(i<10) { //+1
			i++;
		}
		for (int k =0;k<10;k++) {//+1
			if (false) {//+1
				
			}else if (true) {//+1
				
			} else{
				
			}
		}
		boolean b1 = false;
		boolean b2 = true;
		if (i==2 && b!=5 &&b1 ||b2 ) { // if+1, &&+1, &&+1, ||+1
			
		}
		do {} while (i<10); //+1
		try {
			
		} catch (RuntimeException e) {//+1
			// TODO: handle exception
		} catch (Exception e) {//+1
			// TODO: handle exception
		}
		switch (i) {
		case 1://+1
			break;
		case 2: break;//+1
		default://+1
			throw new IllegalArgumentException("Unexpected value: " + i);
		}
	}
	
	private void mian() {
		// TODO Auto-generated method stub
	
	}
}
