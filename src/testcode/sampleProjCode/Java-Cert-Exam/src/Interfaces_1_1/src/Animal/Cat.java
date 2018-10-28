package Animal;

public class Cat implements Animal {

	public String strName;
	
	@Override
	public void setName(String str) {
		strName = str;
	}

	@Override
	public String getName() {
		return strName;
	}
	
	public static void main(String [] s) {
		Cat c = new Cat();
		c.setName("Fluffy");
		System.out.println(c.getName() + " " + c.TYPE_A);
	}

}
