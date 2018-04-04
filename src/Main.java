import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class Main {

	public static final String CLASSPATH = System.getProperty("java.home") + File.separatorChar + "rt.jar";

	// taken an input 'path' from user
	public static void main(String[] args) {
		if (args.length < 1)		// if there is no input argument 
			return; 				// invalid amt of arguments

		Stack<InputStream> streams = new Stack<>();
		Stack<String> names = new Stack<>();
		try {
			if (args[0].endsWith(".jar")) {				// if input is a .jar file, read entries in files
				readJarEntries(args[0], streams, names);
			} else {									// else it is a directory, read file in it
				getFilesInDir(args[0], streams, names);
			}

			parseAll(streams, names, args[0]);
		} catch (IOException e) {
			System.err.println("No clue how this happened, maybe deleted file while program was running");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

		CountingVisitor result = CountingVisitor.getTheTing();
		List<String> typeNames = result.getJavaType();
		List<int[]> counts = result.getCounts();

		while (!typeNames.isEmpty()) {			// print the result
			int[] curCount = counts.remove(0);
			System.out.println(
					"Type: " + typeNames.remove(0) + " Declarations: " + curCount[0] + " References: " + curCount[1]);
		}
		System.out.println(result.finStuff());

	}

	public static void parseAll(Stack<InputStream> streams, Stack<String> names, String srcPath) throws IOException {
		while (!streams.isEmpty()) {
			String source = readFileToString(streams.pop());		// read the file one by one form the top of the stack

			ASTParser parser = ASTParser.newParser(AST.JLS9);		// create ASTParser
			parser.setResolveBindings(true);
			parser.setKind(ASTParser.K_COMPILATION_UNIT);

			parser.setBindingsRecovery(true);

			Map<String, String> options = JavaCore.getOptions();
			parser.setCompilerOptions(options);

			String unitName = names.pop();
			parser.setUnitName(unitName);

			String[] sources = { srcPath };
			String[] classpath = { CLASSPATH };

			parser.setEnvironment(classpath, sources, new String[] { "UTF-8" }, true);
			parser.setSource(source.toCharArray());

			CompilationUnit cu = (CompilationUnit) parser.createAST(null);

			cu.accept(CountingVisitor.getTheTing());
		}
	}
	
	// read an file as String as source for the parseAll function
	public static String readFileToString(InputStream stream) throws IOException {
		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

		char[] buf = new char[10];
		int numRead;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}

		reader.close();
		stream.close();

		return fileData.toString();
	}

	public static void readJarEntries(String pathname, List<InputStream> streams, List<String> names)
			throws IOException {
		JarFile jarfile = new JarFile(pathname); // resource leak, cant close because we will be using the inputstreams
		Enumeration<JarEntry> entries = jarfile.entries(); // could possibly move jarfile to outside method and pass it
															// in
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			if (entry.getName().endsWith(".java")) {
				streams.add(jarfile.getInputStream(entry));			// store .jar file into stack 'steam'
				names.add(entry.getName());
			}
		}
		return;
	}

	public static void getFilesInDir(String pathname, List<InputStream> streams, List<String> names)
			throws IOException {
		File dir = new File(pathname);

		if (!dir.isDirectory()) {							// test whether the file denoted by this path is a directory
			if (!dir.getName().endsWith(".jar")) {			// recheck if the input path is a .jar file 
				streams.add(new FileInputStream(pathname));
				names.add(dir.getName());
			} else
				readJarEntries(pathname, streams, names);
			return;
		}
		// if the path is a directory, then get the .java file name in a stack
		File[] miniFiles = dir.listFiles();

		for (File f : miniFiles) {
			if (f.isFile() && f.getName().endsWith(".java")) {		// check if it is .java files
				streams.add(new FileInputStream(f));
				names.add(f.getName());
			} else if (f.isFile() && f.getName().endsWith(".jar"))	// check if it is .jar file
				readJarEntries(f.getCanonicalPath(), streams, names);
			else if (f.isDirectory())								// check if it is an directory
				getFilesInDir(f.getCanonicalPath(), streams, names);

		}

		return;
	}

}
