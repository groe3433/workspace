package util.regex;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexMatcher {
	
	public static int findMatch(String regex, String str) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		int count = 0;
		while(m.find()) {
			count++;
		}
		return count;
	}

	public static String findAndReplaceMatch(String regex, String str) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		while(m.find()) {
			str = m.replaceAll("Tester");
		}
		return str;
	}

	public static ArrayList<String> getTokensFromScanner(String patternList) {
		ArrayList<String> tokens = new ArrayList<String>();
		String s;
		
		Scanner s1 = new Scanner(patternList);
		while(s1.hasNext()) {
			s = s1.next();
			tokens.add(s);
		}
				
		return tokens;
	}

	public static ArrayList<String> getTokensFromStringTokenizer(String testStrTokenizer, String testStrTokenizerPattern) {
		ArrayList<String> tokens = new ArrayList<String>();

		StringTokenizer s1 = new StringTokenizer(testStrTokenizer, testStrTokenizerPattern);
		while(s1.hasMoreTokens()) {
			tokens.add(s1.nextToken());
		}
				
		return tokens;
	}
}