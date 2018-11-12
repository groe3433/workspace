package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import Calculator.Multiplication;
import model.Numbers;

class CalculatorTest {

	@Test
	void testA() {
		Multiplication m = new Multiplication();
		Numbers n = new Numbers();
		n.setNum1("2");
		n.setNum2("2");
		int testNum = m.multiplyNumbers(n);
		assertEquals(testNum, 4);
	}
	
	@Test
	void testB() {
		Multiplication m = new Multiplication();
		Numbers n = new Numbers();
		n.setNum1("2.25");
		n.setNum2("9.12");
		double testNum = m.multiplyDecimals(n);
	    assertEquals(testNum, 20.52);
	}
	
}
