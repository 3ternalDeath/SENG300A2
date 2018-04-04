package io;

import java.io.File;
import java.util.List;
import java.util.zip.ZipException;

public class JavaSourceCollector {
	
	private String basepath;

	/**
	 * Constructor for a JavaSourceCollector with given pathname
	 * @param The full path of a directory or a JAR file want to process
	 */
	public JavaSourceCollector(String path) {
		this.basepath = path;
	}
	
	/**
	 * getSource() Method
	 * A getter method of JavaSourceCollector
	 * It returns a String List of source code within the given pathname recursively
	 * @return a String List of Java source code
	 * @throws IllegalStateException
	 * @throws IOException
	 * @throws ZipException
	 */
	public List<String> getSource() throws Exception{
		List<String> sources = null;
		try {
			File test = new File(this.basepath);
			if(test.exists()) {
				if(test.isDirectory()) {
					DirectoryVisitor dv = new DirectoryVisitor(this.basepath);
					sources = dv.getSource();
				}else {
					throw new IllegalStateException("The provided path is not a jar file.");
				}
			}else {
				throw new IllegalStateException("An invalid path is provided.");
			}
			
		} catch (ZipException e) {
			throw new ZipException("Error in opening jar file or the file is empty.");
		} catch (Exception e) {
			throw e;
		}
		return sources;
	}
}
