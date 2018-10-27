package testcode.util;

public class Cat implements AutoCloseable {
	public Cat() {
		throw new ArithmeticException();
	}

	@Override
	public void close() throws Exception {
		System.out.println("Closing Crap Cat Method...");
	}
}
