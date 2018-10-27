package main;

import java.util.Locale;
import java.util.ResourceBundle;

import util.ArrayOperations;
import util.MathOperations;
import util.PrintOperations;
import util.StringManipulation;

public class miscmain {
	public static void main(String[] s) {
		setLocale();
		
		PrintOperations.printSTDOUT(StringManipulation.isPalindrome("racecar"));

		PrintOperations.printSTDOUT(StringManipulation.removeChar("racecar", 'e'));

		PrintOperations.printSTDOUT(StringManipulation.reverse("tac"));

		PrintOperations.printSTDOUT(MathOperations.isPrime(13));
		
		PrintOperations.printSTDOUT(MathOperations.fibonacci(10));
		
		PrintOperations.printSTDOUT(MathOperations.factorial(5));
		
		int[] nums = {4,1,4,2};
		int[] maxes = {3,5};
	    int [] temp = ArrayOperations.getMaxesArray(nums, maxes);
	    PrintOperations.printSTDOUT("My Maxes Array: ", temp);
	}
	
	private static void setLocale() {
		PrintOperations.printSTDOUT("@@@@@ Entering setLocale @@@@@");
		// Load Property Resource Bundle
		Locale locFR = new Locale("fr");
		Locale locEN = new Locale("en");
		ResourceBundle rbEN = ResourceBundle.getBundle("properties.Labels", locEN);
		ResourceBundle rbFR = ResourceBundle.getBundle("properties.Labels", locFR);
		PrintOperations.printSTDOUT(rbEN.getString("hello"));
		PrintOperations.printSTDOUT(rbFR.getString("hello"));
		PrintOperations.printSTDOUT("****************************************************************");
		PrintOperations.printSTDOUT("Advantages of using a resource bundle::");
		PrintOperations.printSTDOUT("** Be Easily localized, or translated, into different languages. ");
		PrintOperations.printSTDOUT("** Handle multiple locales at once. ");
		PrintOperations.printSTDOUT("** Be easily modified later to support even more locales. ");
		PrintOperations.printSTDOUT("****************************************************************");
		// Load Java Resource Bundle
		Locale locIT = new Locale("it", "CA");
		ResourceBundle rbIT = ResourceBundle.getBundle("properties.Labels", locIT);
		PrintOperations.printSTDOUT(rbIT.getObject("hello").toString());
		PrintOperations.printSTDOUT("@@@@@ Exiting setLocale @@@@@");
	}
}