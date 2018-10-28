package poly;

public class cat extends care implements pet {
	public void hairball() {
		System.out.println("Hairballs?");
	}

	@Override
	public void getPet() {
		System.out.println("Cats!");
	}
}
