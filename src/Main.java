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
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class Main {
	
	public static final String CLASSPATH = System.getProperty("java.home") + File.pathSeparator +  "rt.jar";

	public static void main(String[] args) throws IOException {
		if (args.length < 1) 
			return;				//invalid amt of arguments
		
		ArrayList<InputStream> streams = new ArrayList<>();
		ArrayList<String> names = new ArrayList<>();
		
		if(args[0].endsWith(".jar")) {
			readJarEntries(args[0], streams, names);
		}
		else {
			getFilesInDir(args[0], streams, names);
		}
		
		parseAll(streams, names, args[0]);
		
		CountingVisitor result = CountingVisitor.getTheTing();
		List<String> typeNames = result.getJavaType();
		List<int[]> counts = result.getCounts();
		
		while(!typeNames.isEmpty()) {
			int[] curCount = counts.remove(0);
			System.out.println("Type: " + typeNames.remove(0) + " Declarations: " + curCount[0] + " References: " + curCount[1]);
		}
		
	}
	
	public static void parseAll(ArrayList<InputStream> streams, ArrayList<String> names, String srcPath) throws IOException {
		while(!streams.isEmpty()) {
			String source = readFileToString(streams.remove(0));
			
			ASTParser parser = ASTParser.newParser(AST.JLS8);
			parser.setResolveBindings(true);
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
	 
			parser.setBindingsRecovery(true);
	 
			Map<String, String> options = JavaCore.getOptions();
			parser.setCompilerOptions(options);
	 
			String unitName = names.remove(0);
			parser.setUnitName(unitName);
	 
			String[] sources = { srcPath }; 
			String[] classpath = {CLASSPATH};
	 
			parser.setEnvironment(classpath, sources, new String[] { "UTF-8"}, true);
			parser.setSource(source.toCharArray());
	 
			CompilationUnit cu = (CompilationUnit) parser.createAST(null);
			
			cu.accept(CountingVisitor.getTheTing());
		}
	}
	
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
	
	public static void readJarEntries(String pathname, ArrayList<InputStream> fin, ArrayList<String> names) throws IOException {
		JarFile jarfile = new JarFile(pathname);			//resource leak, cant close because we will be using the inputstreams
		Enumeration<JarEntry> entries = jarfile.entries();	//could possibly move jarfile to outside method and pass it in
		while(entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			if(entry.getName().endsWith(".java")) {
				fin.add(jarfile.getInputStream(entry));
				names.add(entry.getName());
			}
		}
		return;
	}
	
	public static void getFilesInDir(String pathname, ArrayList<InputStream> streams, ArrayList<String> names) throws IOException {
		File dir = new File(pathname);
		
		File[] miniFiles = dir.listFiles();
		
		for (File f : miniFiles) {
			if(f.isFile() && f.getName().endsWith(".java")) {
				streams.add(new FileInputStream(f));
				names.add(f.getName());
			}
			else if (f.isFile() && f.getName().endsWith(".jar"))
				readJarEntries(f.getCanonicalPath(), streams, names);
			else if (f.isDirectory())
				getFilesInDir(f.getCanonicalPath(), streams, names);
			
		}
		
		return;
	}
	
}
