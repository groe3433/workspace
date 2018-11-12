package Calculator;

import model.Numbers;

public class Calculator {
	public static void main(String [] args) {
		Multiplication m = new Multiplication();
		Numbers n = new Numbers();
		n.setNum1("2");
		n.setNum2("2");
		System.out.println(m.multiplyNumbers(n));
		Numbers n2 = new Numbers();
		n2.setNum1("2.25");
		n2.setNum2("9.12");
		System.out.println(m.multiplyDecimals(n2));
	}
}
