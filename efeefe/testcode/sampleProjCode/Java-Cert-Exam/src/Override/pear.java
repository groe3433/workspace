package Override;

public class pear extends fruit {


	
	public void expiration() throws NullPointerException {
		try {
			super.expiration();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Pears expire in 3 days!");
	}

}
