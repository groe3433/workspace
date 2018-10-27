package util;

public class StringManipulation {
	// check if a string is a palindrome
	public static boolean isPalindrome(String str) {
		if (str == null)
			return false;
		StringBuilder strBuilder = new StringBuilder(str);
		strBuilder.reverse();
		return strBuilder.toString().equals(str);
	}

	// remove given character from the String
	public static String removeChar(String str, char c) {
		if (str == null)
			return null;
		return str.replaceAll(Character.toString(c), "");
	}

	// reverse a string
	public static String reverse(String str) {
		StringBuilder strBuilder = new StringBuilder();
		char[] strChars = str.toCharArray();
		for (int i = strChars.length - 1; i >= 0; i--) {
			strBuilder.append(strChars[i]);
		}
		return strBuilder.toString();
	}
}
