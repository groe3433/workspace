package test1;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class test1 {
	public static void main(String s []) {
		int [] ar = {0, 3, 2, 6, 1};
		Random rnd = ThreadLocalRandom.current();
		for(int i = ar.length - 1; i > 0; i--) {
			int index = rnd.nextInt(i + 1);
			int a = ar[index];
			ar[index] = ar[i];
			ar[i] = a;
		}
	}
}
