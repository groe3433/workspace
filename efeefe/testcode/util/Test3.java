package com;

public class Test3 {

	public String myTest(String s, String x) {
		String tmp = "";
		assert(s.equals("Test"));
		
		switch(x) {
		case "1": tmp = s; break;
		case "2": tmp = "cat"; break;
		default: assert false;
		}

		return tmp;
	}

}
