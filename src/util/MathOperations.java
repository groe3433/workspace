package util;

public class MathOperations {
	// check if number is prime
	public static boolean isPrime(int number) {
		for (int i = 2; i < number; i++) {
			if (number % i == 0) {
				return false;
			}
		}
		return true;
	}

	// Fibonacci number is sum of previous two Fibonacci numbers fn= fn-1+ fn-2
	// first 10 Fibonacci numbers are 1, 1, 2, 3, 5, 8, 13, 21, 34, 55
	public static int fibonacci(int number) {
		if (number == 1 || number == 2) {
			return 1;
		}
		return fibonacci(number - 1) + fibonacci(number - 2);
	}
	
	// find factorial (n = n * n-1 * n-2 ... until n != 0)
	public static int factorial(int number){       
        if(number == 0){
            return 1;
        }
        return number*factorial(number -1); 
    }
}
