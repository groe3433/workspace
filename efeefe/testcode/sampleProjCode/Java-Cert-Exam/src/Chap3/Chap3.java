package Chap3;

public class Chap3 {
	public static void main(String s []) {
		int t1 = 9_0_0;
		System.out.println(t1);
		
		int t2 = 0b01;
		System.out.println(t2);
		
		int t3 = 012;
		System.out.println(t3);
		
		int t4 = 0x0F;
		System.out.println(t4);
		
		long t5 = 0x0Fl;
		System.out.println(t5);
		
		float t6 = 122.23333333f;
		System.out.println(t6);
		
		boolean t7 = true;
		System.out.println(t7);
		
		float t8 = (float) (12.2 * 12.2);
		System.out.println(t8);
		
		byte t9 = (byte) (12555 + 125555);
		System.out.println(t9);
		
		byte t10 = 3;
		byte t11 = (byte) (t10 + 12);
		System.out.println(t11);
		
		byte t12 = 3;
		t12 += 12;
		System.out.println(t12);
		
		double t13 = 3.3D;
		System.out.println(t13);
		
		int t14 = 0b1_1;
		System.out.println(t14);
		
		int t15 = 0Xf_f_f;
		System.out.println(t15);
		
		String s1 = "java";
		String t = s1;
		System.out.println(t.toUpperCase());
		t = t.toUpperCase();
		System.out.println(s1 + " " + t);
		String tq = t;
		if(tq == t) {
			System.out.println("Test!");
		} 
		if (t.equals(tq)) {
			System.out.println("Cats!");
		}
		String sq = s1;
		sq = "Test Java String " + s1;
		System.out.println(sq + " " + s1);
		
	}
}
