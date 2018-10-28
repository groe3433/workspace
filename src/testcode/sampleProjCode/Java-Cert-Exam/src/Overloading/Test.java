package Overloading;

import java.io.IOException;

public class Test {

	public static void main(String s []) {
		
		fruit f = new fruit();
		try {
			System.out.println(f.expiration());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		pear p = new pear();
		try {
			System.out.println(p.expiration((float)13.5));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		fruit f1 = new pear();
		try {
			System.out.println(f1.expiration());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

}
