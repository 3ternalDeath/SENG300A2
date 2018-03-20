import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Main {

	public static void main(String[] args) throws IOException {
		if (args.length < 1) 
			return;				//invalid amt of arguments
		
		ArrayList<InputStream> streams = new ArrayList<>();
		
		if(args[0].endsWith(".jar")) {
			readJarEntries(args[0], streams);
		}
		else {
			getFilesInDir(args[0], streams);
		}
		
	}
	
	public static void readJarEntries(String pathname, ArrayList<InputStream> fin) throws IOException {
		JarFile jarfile = new JarFile(pathname);			//resource leak, cant close because we will be using the inputstreams
		Enumeration<JarEntry> entries = jarfile.entries();	//could possibly move jarfile to outside method and pass it in
		while(entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			if(entry.getName().endsWith(".java"))
				fin.add(jarfile.getInputStream(entry));
			
	
	public static void getFilesInDir(String pathname, ArrayList<InputStream> streams) throws IOException {
		File dir = new File(pathname);
		
		File[] miniFiles = dir.listFiles();
		
		for (File f : miniFiles) {
			if(f.isFile() && f.getName().endsWith(".java"))
				streams.add(new FileInputStream(f));
			else if (f.isDirectory()) {
				getFilesInDir(f.getAbsolutePath(), streams);
			}
		}
		
		return;
	}
	
}
