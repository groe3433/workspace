package exam1;

class Student {
	public String name = "";
}

public class test {
	public static void main(String [] s) {
		Student s1 = new Student();
		Student s2 = new Student();
		s1.name = "Bob";
		s2 = s1;
		s2.name = "Jian";
		System.out.println(s1.name);
	}
}
