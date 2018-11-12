package Calculator;

import MathInterface.MathInterface;
import lib.MyMath;
import model.Numbers;

public class Multiplication implements MathInterface {

	public int multiplyNumbers(Numbers n) {
		return MyMath.multiplyNumbers(convertNum(n.getNum1()), convertNum(n.getNum2()));
	}
	
	public double multiplyDecimals(Numbers n) {
		double d = MyMath.multiplyDecimals(convertDecimal(n.getNum1()), convertDecimal(n.getNum2()));
		return d;
	}

	@Override
	public int convertNum(String temp) {
		return Integer.parseInt(temp);
	}

	@Override
	public double convertDecimal(String temp) {
		return Double.parseDouble(temp);
	}

}
