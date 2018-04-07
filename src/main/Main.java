package main;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;

import io.JavaSourceCollector;

public class Main {
	/**
	 * Points to a base directory of a machine</br>
	 * Uses Testfiles folder in base directory as default </br>
	 * @author SeungBin, Yim
	 */
	private static String BASEDIR = System.getProperty("user.dir");
	/**
	 * Default Constructor, prints out all declarations and references from sources in BASEDIR </br>	 * 
	 * @author SeungBin, Yim
	 */
	public Main() {
		this(BASEDIR+"\\TestFiles");
	}
	/**
	 * Constructor, prints out all declarations and references from given directory </br>	 * 
	 * @author SeungBin, Yim
	 */
	public Main(String dir) {
		BASEDIR = dir;
		Map<String, Integer[]> finalMap = new HashMap<String, Integer[]>();
		JavaSourceCollector jsc = new JavaSourceCollector(dir);
		List<String> sourceList = null;
		try {
			sourceList = jsc.getSource();
		}catch (Exception e) { e.printStackTrace(); }
		
		for(String source : sourceList) {
			Map<String, Integer[]> SourceMap = visit(parse(source));
			for(String key : SourceMap.keySet())
				if(finalMap.get(key) == null)
					finalMap.put(key,  SourceMap.get(key));
				else {
					Integer[] value = finalMap.get(key);
					value[0] += SourceMap.get(key)[0];
					value[1] += SourceMap.get(key)[1];
					finalMap.put(key, value);
				}
		}
		
		//Sort keys
		SortedSet<String> keyset = new TreeSet<String>();
		keyset.addAll(finalMap.keySet());
		
		for(String truekey: keyset) {
			System.out.printf(
					"%s\nDeclarations found: %s;\treferences found: %s\n---\n",
					truekey, finalMap.get(truekey)[0], finalMap.get(truekey)[1] );
		}
		//Print
		System.out.println(TypeCounter.getInstance().toString());
	}
	/**
	 * Parses source String into AST Tree
	 * @param sourceCode
	 * 	Source String to be parsed by ASTParser
	 * @return
	 * 	Root node of a parsed AST
	 * @author SeungBin, Yim
	 */
	public ASTNode parse(String sourceCode) {
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(sourceCode.toCharArray());
		
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		
		parser.setUnitName("");
		
		parser.setEnvironment(null,
				new String[] {BASEDIR}, new String[]{"UTF-8"}, true);
		
		Map<String, String> options = JavaCore.getOptions();
		options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8); //or newer version
		parser.setCompilerOptions(options);
		
		return parser.createAST(null);
	}
	/**
	 * Visits AST tree, counting declarations and references
	 * @param node
	 * Root node of a AST
	 * @author SeungBin, Yim
	 */
	public Map<String, Integer[]> visit(ASTNode node) {
		Visitor vis = new Visitor();
		CompilationUnit cu = (CompilationUnit)node;
		cu.accept(vis);
		return vis.getMap();
	}
	
	//Program Entry Point
	public static void main(String[] args) {
		if(args.length == 0)
			new Main();
		else if(args.length == 1)
			new Main(args[0]);
		else
			System.out.println("Give no argument(Uses TestFiles Directory) or 1 argument(Directory)");
	}
}