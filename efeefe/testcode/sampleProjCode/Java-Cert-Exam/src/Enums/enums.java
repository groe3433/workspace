package Enums;

enum pet { Reptile, Dog, Cat }
enum tag { Red, Blue };

class myPet {
	pet Type;
	tag Color;
}

public class enums {
	public static void main(String s []) {
		myPet p = new myPet();
		p.Type = pet.Cat;
		p.Color = tag.Blue;
		
		System.out.println(p.Type + " " + p.Color);
	}
}
