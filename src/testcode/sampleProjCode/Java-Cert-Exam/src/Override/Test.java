package Override;

public class Test {

	public static void main(String s []) {
		fruit f = new fruit();
		pear p = new pear();
		fruit f1 = new pear();

		try {
			f.expiration();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		p.expiration();
		
		try {
			f1.expiration();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

}
