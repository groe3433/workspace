package util.file;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFileAttributes;
import java.util.Locale;
import java.util.ResourceBundle;
import util.PrintOperations;

public class FileOperations {
	
	private String rootPath;
	private String subPath;
	private String fileName;
	private String systemType;
	private Path myPath;
	private Path myFile;
	
	public FileOperations(String systemType, String rootPath, String subPath, String fileName) {
		this.rootPath = rootPath;
		this.subPath = subPath;
		this.fileName = fileName;
		this.systemType = systemType;
		this.myPath = Paths.get(rootPath, subPath);
		this.myFile = Paths.get(rootPath, subPath, fileName);
	}

	public void createDirectory() {
		try {
			if(Files.exists(this.myPath)) {
				IOException ioe = new IOException("Directory Already Exists");
				throw ioe;
			} else {
				Files.createDirectory(this.myPath);
			}
		} catch (IOException ioe) {
			PrintOperations.printSTDOUT("!!!!! IOException :: " + ioe);
			ioe.printStackTrace();
		}
	}

	public void createFile() {
		try {
			if(Files.exists(this.myPath)) {
				if(Files.exists(this.myFile)) {
					IOException ioe = new IOException("File Already Exists");
					throw ioe;
				} else {
					Files.createFile(this.myFile);
				}
			} else {
				IOException ioe = new IOException("Directory Does Not Exist");
				throw ioe;
			}
		} catch (IOException ioe) {
			PrintOperations.printSTDOUT("!!!!! IOException " + ioe);
			ioe.printStackTrace();
		}
	}

	public void getDirList() {
		// instead of just a path variable, can override with optional regex to search for folders/files with a certain name. 
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.myPath)) {
			for(Path path : stream) {
				PrintOperations.printSTDOUT(path.getFileName().toString());
			}
		} catch (IOException ioe) {
			PrintOperations.printSTDOUT("!!!!! IOException " + ioe);
			ioe.printStackTrace();
		}
	}
	
	public void getDirRecusiveList() {
		FileOperationsRecusion dirs = new FileOperationsRecusion();
		try {
			Files.walkFileTree(this.myPath, dirs);
		} catch (IOException ioe) {
			PrintOperations.printSTDOUT("!!!!! IOException " + ioe);
			ioe.printStackTrace();
		}
	}
	
	public void getBasicFileAttributes() {
		try {
			BasicFileAttributes basic = Files.readAttributes(this.myPath, BasicFileAttributes.class);
			FileTime created = basic.creationTime();
			FileTime lastUploaded = basic.lastModifiedTime();
			FileTime lastAccess = basic.lastAccessTime();
			PrintOperations.printSTDOUT("Created :: " + created);
			PrintOperations.printSTDOUT("Modified :: " + lastUploaded);
			PrintOperations.printSTDOUT("Accessed :: " + lastAccess);
		} catch (IOException ioe) {
			PrintOperations.printSTDOUT("!!!!! IOException " + ioe);
			ioe.printStackTrace();
		}
	}

	public void getDosFileAttributes() {
		if(this.systemType.equals("dos")) {
			try {
				DosFileAttributes dos = Files.readAttributes(this.myPath, DosFileAttributes.class);
				PrintOperations.printSTDOUT("Is Hidden :: " + dos.isHidden());
				PrintOperations.printSTDOUT("Is Read Only :: " + dos.isReadOnly());
			} catch (IOException ioe) {
				PrintOperations.printSTDOUT("!!!!! IOException " + ioe);
				ioe.printStackTrace();
			}
		}
	}

	public void getPosixFileAttributes() {
		if(this.systemType.equals("posix")) {
			try {
				PosixFileAttributes posix = Files.readAttributes(this.myPath, PosixFileAttributes.class);
				PrintOperations.printSTDOUT("Permissions :: " + posix.permissions());
				PrintOperations.printSTDOUT("Group :: " + posix.group());
			} catch (IOException ioe) {
				PrintOperations.printSTDOUT("!!!!! IOException " + ioe);
				ioe.printStackTrace();
			}
		}
	}	
	
	public void setLocale() {
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
