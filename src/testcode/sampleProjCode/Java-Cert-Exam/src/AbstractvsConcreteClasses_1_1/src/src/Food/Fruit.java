package Food;

public abstract class Fruit {
	private static String s;
	public abstract String color();
	public abstract String getName();
	public static String setDefaultName() {
		return s = "MyFruit";
	}
}
