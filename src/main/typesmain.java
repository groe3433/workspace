package main;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;

import util.FormatOperations;
import util.PrintOperations;

public class typesmain {
	public static void main(String [] s) {
		NumberFormat [] nfa = FormatOperations.getNumberFormats("it", "IT");
		DateFormat [] dfa = FormatOperations.getDateFormats("it", "IT");
		
		Date d = new Date();
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, 1);
		
		Date d1 = c.getTime();
		
		PrintOperations.printSTDOUT((dfa[5].format(d1)).toString());
		
		float f1 = 100.101f;
		PrintOperations.printSTDOUT(nfa[3].format(f1));
	}
}