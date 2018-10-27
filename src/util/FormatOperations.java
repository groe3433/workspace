package util;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FormatOperations {
	public static NumberFormat[] getNumberFormats(String strLocale, String strCountry) {
		Locale locIT = new Locale(strLocale, strCountry);
		NumberFormat [] nfa = new NumberFormat[6];
		nfa[0] = NumberFormat.getInstance();
		nfa[1] = NumberFormat.getInstance(locIT);
		nfa[2] = NumberFormat.getCurrencyInstance();
		nfa[3] = NumberFormat.getCurrencyInstance(locIT);
		return nfa;
	}

	public static DateFormat[] getDateFormats(String strLocale, String strCountry) {
		Locale locIT = new Locale(strLocale, strCountry);
		DateFormat [] dfa = new DateFormat[6];
		dfa[0] = DateFormat.getInstance();
		dfa[1] = DateFormat.getDateInstance();
		dfa[2] = DateFormat.getDateInstance(DateFormat.SHORT);
		dfa[3] = DateFormat.getDateInstance(DateFormat.MEDIUM);
		dfa[4] = DateFormat.getDateInstance(DateFormat.LONG);
		dfa[5] = DateFormat.getDateInstance(DateFormat.FULL, locIT);
		return dfa;
	}
}
