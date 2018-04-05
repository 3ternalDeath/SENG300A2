package io;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DirectoryVisitor {
	
	private String sourceDirectory = null;
	
	/**
	 * Constructor for a DirectoryVisitor with given pathname
	 * @param Directory of the path want to process
	 */
	public DirectoryVisitor(String pathName) {
		this.sourceDirectory = pathName;
	}
	
	/**
	 * getSource() Method
	 * A getter method of DirectoryVisitor
	 * It returns a String List of source code within the given path recursively
	 * @return a String List of Java source code
	 */
	public List<String> getSource() throws Exception{
		List<String> sources = new ArrayList<String>();
		FileReader fr;
		try {
			File dir = new File(this.sourceDirectory);
			for(File entry: this.listAllFiles(dir)) {
				if(entry.getName().endsWith(".java")) {
					String content = "";
					fr = new FileReader(entry);
					BufferedReader reader = new BufferedReader(fr);
					String line = reader.readLine();
					while (line != null) {
						content += "\n"+ line;
						line = reader.readLine();
					}
					sources.add(content);
					reader.close();
				}else if(entry.isFile() && entry.getName().endsWith(".jar")){
					JARVisitor jv = new JARVisitor(entry.getAbsolutePath());
					sources.addAll(jv.getSource());
				}else if(entry.isDirectory()) {
					DirectoryVisitor dv = new DirectoryVisitor(entry.getAbsolutePath());
					sources.addAll(dv.getSource());
				}
			}
		} catch (IOException ioe) {
			throw ioe;
		}
		return sources;
	}
	
	private Collection<File> listAllFiles(File dir) {
	    Set<File> tree = new HashSet<File>();
	    if(dir==null || dir.listFiles()==null){
	        return tree;
	    }
	    for (File entry : dir.listFiles()) {
	        if (entry.isFile()) tree.add(entry);
	        else tree.addAll(this.listAllFiles(entry));
	    }
	    return tree;
	}

}
