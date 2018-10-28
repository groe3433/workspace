package casting;

class Being {
	void makeNoise() {
		System.out.println("Something");
	}
	void action() {
		System.out.println("Optional Claws");
	}	
}

class Animal extends Being {
	void makeNoise() {
		System.out.println("Loud Noise");
	}
	//void action() {
		//System.out.println("Razor Sharp Claws");
	//}	
}

class Cat extends Animal {
	void makeNoise() {
		System.out.println("Meow");
	}
	//void action() {
		//System.out.println("Claw");
	//}
}

public class casting {
	public static void main (String [] s) {
		Being b = new Being();
		Animal a = new Animal();
		Cat c = new Cat();
		
		// a2 holds a Cat
		Animal a2 = new Cat();
		
		// Cast Cat to an Animal
		Animal a1 = (Animal)c;
		a1.makeNoise();
		
		// Cast Animal to a Being
		Being b1 = (Being)a;
		b1.makeNoise();
		
		// Cast Cat to a Being
		Being b2 = (Being)c;
		b2.makeNoise();
		
		// a1 has been cast to a Cat
		if(a1 instanceof Cat) {
			Cat cc1 = (Cat) a1;
			cc1.action();
			a1.action();
		} 
		
		// a2 holds a Cat, so cast it DOWN to a Cat and do a Cat action
		if(a2 instanceof Cat) {
			Cat cc2 = (Cat) a2;
			cc2.action();
		}
		
		// An Illegal Cast
		//String str = (String)a1;
		
		// Cannot store reference to BEing in an Animal
		//Animal a3 = new Being();
		
		// Animal cannot be cast to a Cat
		Animal a4 = new Animal();
		//((Cat)a4).action();
		
		// Upcast OK without Explicit cast from Cat to Animal
		Cat c4 = new Cat();
		Animal a5 = c4;
		a5.action();
		
		// Upcast OK with Explicit cast from Cat to Animal
		Animal a6 = (Animal)c4;
		a6.action();
		
		// Explicit Upcast, followed by Implicit Downcast
		Cat a7 = (Animal)c4;
		
	}
}
