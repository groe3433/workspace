package main;

import util.file.FileOperations;

public class iomain {
	public static void main(String[] s) {
		// SystemType = posix (Unix/Linux)
		// SystemType = dos (Windows)
		String strSystemType = "posix";
		
		FileOperations fo = new FileOperations(strSystemType, "/Users/Administrator/Desktop", "MySampleDir", "MySampleFile.txt");
		
		fo.setLocale();
		fo.createDirectory();
		fo.createFile();		
		fo.getDirList();
		fo.getBasicFileAttributes();
		fo.getDosFileAttributes();
		fo.getPosixFileAttributes();

		FileOperations foo = new FileOperations(strSystemType, "/Users/Administrator/Desktop", "MySampleDir/temp", "MySampleFile1.txt");

		foo.createDirectory();	
		foo.createFile();
		
		FileOperations fooo = new FileOperations(strSystemType, "/Users/Administrator/Desktop", "MySampleDir/temp1", "MySampleFile2.txt");

		fooo.createDirectory();	
		fooo.createFile();
		
		FileOperations foooo = new FileOperations(strSystemType, "/Users/Administrator/Desktop", "MySampleDir/temp/temp123", "MySampleFile3.txt");

		foooo.createDirectory();	
		foooo.createFile();
		
		FileOperations foo1 = new FileOperations(strSystemType, "/Users/Administrator/Desktop", "MySampleDir", "MySampleFile.txt");

		foo1.getDirRecusiveList();
	}
}