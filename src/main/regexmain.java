package main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import util.MathOperations;
import util.PrintOperations;
import util.regex.RegexDefinitions;
import util.regex.RegexMatcher;
import util.StringManipulation;

public class regexmain {
	public static void main(String[] s) {
		PrintOperations.printSTDOUT("@@@@@ Entering Main @@@@@");
		
		RegexDefinitions rd = new RegexDefinitions();
		
		String strText1 = "Test.2[fF]+[0-9a-zA-Z]+.abc";		
		String strPattern1 = "\\.";
		ArrayList<String> tokensFromStringTokenizer = getTokensFromStringTokenizer(strText1, strPattern1);
		populateHashMap(rd, tokensFromStringTokenizer);
		
		String strPattern2 = "\\d \\b \\s \\w .?st Te.?t";
		ArrayList<String> tokensFromScanner = getTokensFromScanner(strPattern2);	
		populateHashMap(rd, tokensFromScanner);
		
		String str = "Test rTest Test CAT dog Bird 12f3 Zap Zot Test 445 2F90 2ffui";
		SearchMap(rd, str);
		
		PrintOperations.printSTDOUT("@@@@@ Exiting Main @@@@@");
	}

	public static void SearchMap(RegexDefinitions rd, String str) {
		PrintOperations.printSTDOUT("@@@@@ Entering SearchMap @@@@@");
	    Iterator it = rd.getRegexValues().entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
			if(rd.getRegexValues().containsKey(pair.getKey().toString())) {
				if(pair.getKey().toString().equals("Test")) {
					String result = RegexMatcher.findAndReplaceMatch(pair.getKey().toString(), str);		
					PrintOperations.printSTDOUT(result, rd.getRegexValues().get(pair.getKey().toString()));
					str = result;
				} else {
					int result = RegexMatcher.findMatch(pair.getKey().toString(), str);		
					PrintOperations.printSTDOUT(result, rd.getRegexValues().get(pair.getKey().toString()));
				}
			} 
	    }
	    PrintOperations.printSTDOUT("@@@@@ Exiting SearchMap @@@@@");
	}

	private static void populateHashMap(RegexDefinitions rd, ArrayList<String> arrayList) {
		PrintOperations.printSTDOUT("@@@@@ Entering populateHashMap @@@@@");
		for(int i = 0; i < arrayList.size(); i++) {
			String key = arrayList.get(i).toString();
			rd.addRegexEntry(key, getHashValue(key));
		}
		PrintOperations.printSTDOUT("@@@@@ Exiting populateHashMap @@@@@");
	}

	private static ArrayList<String> getTokensFromScanner(String strPattern2) {
		PrintOperations.printSTDOUT("@@@@@ Entering getTokensFromScanner @@@@@");
		ArrayList<String> tokensFromScanner = RegexMatcher.getTokensFromScanner(strPattern2);		
		for(int i = 0; i < tokensFromScanner.size(); i++) {
			PrintOperations.printSTDOUT(tokensFromScanner.get(i).toString());
		}
		PrintOperations.printSTDOUT("_________________________________________");
		PrintOperations.printSTDOUT("@@@@@ Exiting getTokensFromScanner @@@@@");
		return tokensFromScanner;
	}

	private static ArrayList<String> getTokensFromStringTokenizer(String strText1, String strPattern1) {	
		PrintOperations.printSTDOUT("@@@@@ Entering getTokensFromStringTokenizer @@@@@");
		ArrayList<String> tokensFromStringTokenizer = RegexMatcher.getTokensFromStringTokenizer(strText1, strPattern1);		
		for(int i = 0; i < tokensFromStringTokenizer.size(); i++) {
			PrintOperations.printSTDOUT(tokensFromStringTokenizer.get(i).toString());
		}
		PrintOperations.printSTDOUT("_________________________________________");
		PrintOperations.printSTDOUT("@@@@@ Exiting getTokensFromStringTokenizer @@@@@");
		return tokensFromStringTokenizer;
	}
	
	public static String getHashValue(String key) {
		String value = "";
		switch (key) {
		case "Test":
			value = " occurence(s) of string: " + key + " replaced. ";
			break;
		case "\\s":
			value = " occurence(s) of white space. ";
			break;
		case "\\d":
			value = " occurence(s) of decimals. ";
			break;
		case "\\w":
			value = " occurence(s) of words. ";
			break;
		case "\\b":
			value = " occurence(s) of boundaries. ";
			break;
		case "2[fF]+[0-9a-zA-Z]+":
			value = " occurence(s) of strings that start with 2, followed by 1 or more f(F)'s, followed by 1 or more decimals/letters regardless of case. ";
			break;
		case "Z.[pt]":
			value = " occurence(s) of strings that start with Z, followed by 1 of anything, followed by a 'p' or 't'. ";
			break;
		case ".?st":
			value = " occurence(s) of strings that start with 0 or more anything, followed by 'st'. ";
			break;			
		}
		return value;
	}	
}