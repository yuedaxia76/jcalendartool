package org.ycalendar.test;

import java.util.Locale;

import org.ycalendar.dbp.dao.H2Dao;
 

public class TestDao {
	public static void main(String[] args) {
	 
		//ddssd();
		eee() ; 
	}
	
	public static void eee() {
		System.out.println(Locale.getDefault().toString());
	}
 	
	public static void ddssd() {
		H2Dao hd=H2Dao.getH2Dao();
		
		System.out.println(hd.getDbUrl());
	}
	
	public static void ddd() {
		StringBuilder one = new StringBuilder();
		one.append("aaab;");
		
		if (one.indexOf(";", one.length() - 1) > -1) {
	 
			one.deleteCharAt(one.length() - 1);
 
			System.out.println(one.toString());
			
		}
		
		one.setLength(0);
		
		one.append("aaab;");
		int s=one.indexOf("b;");
		one.delete(s, s+"b;".length());
		System.out.println(one.toString());
		
	}
}
