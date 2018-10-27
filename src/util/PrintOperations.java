package util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class PrintOperations {
	public static void printSTDOUT(String results) {
		System.out.println(results);
	}
	
	public static void printSTDOUT(int results) {
		System.out.println(results);
	}
	
	public static void printSTDOUT(Boolean results) {
		System.out.println(results);
	}
	
	public static void printSTDOUT(int results, String text) {
		System.out.printf("%+04d", results);
		System.out.println(text);
	}

	public static void printSTDOUT(String results, String text) {
		System.out.format("%1$s + %2$s \n", results, text);
	}

	public static void printSTDOUT(String results, int[] unsortedNumList) {
		System.out.print(results);
		for(int i = 0; i < unsortedNumList.length; i++) {
			System.out.print(unsortedNumList[i]);
			if(i != (unsortedNumList.length - 1)) {
				System.out.print(",");
			}
		}
		System.out.print("\n");
	}

	public static void printSTDOUT(ArrayList<Object> arrayList) {
		for(int i = 0; i < arrayList.size(); i++) {
			System.out.print(arrayList.get(i));
			if(i != (arrayList.size() - 1)) {
				System.out.print(",");
			}
		}
		System.out.print("\n");
	}

	public static void printSTDOUT(String rootPath, String subPath, String fileName) {
		int spaces = 1;
		Path myPath = Paths.get(rootPath, subPath, fileName);
		for(Path p : myPath) {
			System.out.format("%" + spaces + "s%s%n", "", p);
			spaces += 2;
		}
	}
}
