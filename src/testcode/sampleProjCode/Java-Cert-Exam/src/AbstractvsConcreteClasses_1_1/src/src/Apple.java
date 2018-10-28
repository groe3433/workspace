import Food.Fruit;

class Apple extends Fruit {

	public static void main(String[] args) {
		Apple a = new Apple();
		String myFruitName = a.getName();
		System.out.println(myFruitName);
		String myFruitColor = a.color();
		System.out.println(myFruitColor);
	}

	@Override
	public String color() {
		String str = "Red";
		return str;
	}

	@Override
	public String getName() {
		String str = Fruit.setDefaultName();
		return str;
	}
}
