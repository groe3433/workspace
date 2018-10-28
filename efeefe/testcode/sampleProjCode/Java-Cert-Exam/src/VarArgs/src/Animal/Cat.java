package Animal;

public class Cat {
	public static void main(String s []) {
		dostuff(5, 6, 7, 8);
	}
	public static void dostuff(int... x) {
		System.out.println(x.length);
		for(int i = 0; i < x.length; i++) {
			System.out.println(x[i]);
		}
	}
}
