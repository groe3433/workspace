package util.regex;

import java.util.HashMap;

public class RegexDefinitions {
	
	private HashMap<String,String> regexValues = null;
	
	public RegexDefinitions() {
		regexValues = new HashMap<String,String>();
	}

	public HashMap<String, String> getRegexValues() {
		return regexValues;
	}

	public void setRegexValues(HashMap<String, String> regexValues) {
		this.regexValues = regexValues;
	}

	public void addRegexEntry(String key, String value) {
		regexValues.put(key, value);
	}
}