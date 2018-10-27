package util.file;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;

import util.PrintOperations;

public class FileOperationsRecusion extends SimpleFileVisitor<Path> {
	
	private PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**/password/**.txt");
	
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
		// skip subtree on previsit
		if(dir.getFileName().toString().startsWith("temp1")) {
			return FileVisitResult.SKIP_SUBTREE;
		}
		PrintOperations.printSTDOUT("pre :: " + dir);
		return FileVisitResult.CONTINUE;
	}
	
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		// skip siblings of a certain parent
		if(file.getParent().getFileName().toString().equals("temp")) {
			return FileVisitResult.SKIP_SIBLINGS;
		}
		// PathMatcher check
		if(matcher.matches(file)) {
			PrintOperations.printSTDOUT("@@@@@ MATCHED :: " + file.getFileName().toString());
		}
		// skip subtrees given a specific type of name
		if(file.getFileName().toString().startsWith(".")) {
			return FileVisitResult.SKIP_SUBTREE;
		} 
		PrintOperations.printSTDOUT("file :: " + file.getFileName().toString());
		return FileVisitResult.CONTINUE;
	}
	
	public FileVisitResult visitFileFailed(Path file, IOException ioe) {
		PrintOperations.printSTDOUT("file failed :: " + file);
		return FileVisitResult.CONTINUE;
	}
	
	public FileVisitResult postVisitDirectory(Path dir, IOException ioe) {
		PrintOperations.printSTDOUT("post :: " + dir);
		return FileVisitResult.CONTINUE;
	}
}