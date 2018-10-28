package LocalVariables;

public class localvar {
	int test = 90;
	public static void main(String s []) {
		int test = 80;
		new localvar().cat();
		new localvar().dostuff(test);
		new localvar().cat();
	}
	public void dostuff(int test) {
		System.out.println(test);
		test = test;
		System.out.println(test);
		this.test = test;
		System.out.println(this.test);		
	}
	public void cat() {
		System.out.println(test);
	}
}
