package exam1;

public class Animal {
	
	private int id;
	
	public static void main (String... a) {
		System.out.println(a[0] + " " + a[1]);
		
		StringBuilder sb = new StringBuilder("c");
		System.out.println(sb + "t");
		
		int x = 1;
		if ((4 > x) ^ ((++x + 2) > 3)) x++;
		if ((4 > ++x) ^ !(++x == 5)) x++;
		System.out.println(x);
		
		Animal az = new Animal(1);
		Animal b = new Animal(2);
		Animal c = b;
		String result = "-";
		if(az.equals(b)) result += "3";
		if(b.equals(c)) result += "4";	
		System.out.println(result);
		
		final StringBuilder str = new StringBuilder("I good!");
		str.insert(2,  "look ");
		System.out.println(str);
	}
	
	public boolean equals(Object o) {
		Animal other = (Animal) o;
		return id == other.id;
	}
	
	public Animal(int id) {
		this.id = id;
	}
}
