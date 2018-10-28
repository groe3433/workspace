package exam1;

public class test2 {
	public static void main(String [] s) {
		String [] table = {"aa", "bb", "cc"};
		for(String ss: table) {
			int ii = 0;
			while(ii < table.length) {
				System.out.println(ss + "" + ii);
				ii++;
			}
		}
	}
}
