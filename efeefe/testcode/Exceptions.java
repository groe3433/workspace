package testcode;

import java.sql.SQLException;

import testcode.util.Cat;

public class Exceptions {
	public static void main(String [] s) {
		String tmp = "";
		try {
			tmp = myClass();
		} catch (ArithmeticException e) {
			System.out.println("Humm...." + tmp);
		} finally {
			try {
				myClass2();
			} catch (ArithmeticException | SQLException e) {
				try {
					myClass3();
				} catch (Exception e1) {
					System.out.println("Finally Done Here??");
				} finally {
					System.out.println("Yes");
				}
			}
		}
	}

	private static void myClass3() throws Exception {
		try (Cat c = new Cat()) {
			System.out.println("CATS!!!!");
		}
	}

	private static void myClass2() throws ArithmeticException, SQLException {
		try {
			rain();
		} catch(ArithmeticException | SQLException ex) {
			throw ex;
		} catch(Exception e) {
			throw new ArithmeticException();
		}
	}

	private static void rain() throws Exception {
		System.out.println("Test");
		throw new Exception();
	}

	private static String myClass() throws ArithmeticException {
		int t;
		try {
			t = 2/0;
		} catch(ArithmeticException e) {
			throw new ArithmeticException();
		} 
		return ("I don't know what happened here..." + t);
	}
}


