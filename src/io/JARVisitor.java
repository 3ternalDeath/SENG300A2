package io;

import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


public class JARVisitor {
	
	private String sourcepath = null;
	
	/**
	 * Constructor for a JARVisitor with given pathname
	 * @param Absolute Path of the JAR file want to process
	 */
	public JARVisitor(String pathName) {
		this.sourcepath = pathName;
	}
	
	/**
	 * getSource() Method
	 * A getter method of JARVisitor
	 * It returns a String List of source code within the given jar file recursively
	 * @return a String List of Java source code
	 */
	public List<String> getSource() throws Exception{
		List<String> sources = new ArrayList<String>();
		try {
			JarFile sourcejar = new JarFile(this.sourcepath);
			Enumeration<JarEntry> entries = sourcejar.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if(entry.getName().endsWith(".java") && !entry.isDirectory()) {
					String content = "";
				    InputStream is = sourcejar.getInputStream(entry);
				    InputStreamReader isr = new InputStreamReader(is);
				    BufferedReader reader = new BufferedReader(isr);
				    String line = reader.readLine();
					while (line != null) {
						content +=  "\n"+ line;
						line = reader.readLine();
					}
					sources.add(content);
				    reader.close();
				}
			}
			sourcejar.close();
		} catch (IOException ioe){
			throw ioe;
		} finally {
		return sources;
		}
	}
}
