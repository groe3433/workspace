package poly;

public class dog extends care implements pet {
	public void deworm() {
		System.out.println("deworm?");
	}

	@Override
	public void getPet() {
		System.out.println("Dogs!");
	}
}
