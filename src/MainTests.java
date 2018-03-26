import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Stack;

import org.junit.Before;
import org.junit.Test;

public class MainTests {
	
	private static final String DIRPATH = "YOUR PATH HERE\\SENG300A2\\TestFiles";
	private List<InputStream> streams;
	private List<String> names;
	
	@Before
	public void setup() {
		CountingVisitor.reset();
		streams = new Stack<>();		// empty stack for streams
		names = new Stack<>();			// empty stack for name of files
	}
	
	@Test
	// expect it get the same number of files in .jar file, and have each .java file name in list names
	public void readinJar() throws IOException {
		String pathname = DIRPATH + File.separator + "f1" + File.separator + "yo.jar";
		Main.readJarEntries(pathname, streams, names);
		assertEquals(names.size(), streams.size());
		assertEquals(names.size(), 4);				//number of files in jar
		assertTrue(names.contains("C.java"));
		assertTrue(names.contains("D.java"));
		assertTrue(names.contains("F/H.java"));
		assertTrue(names.contains("foo/E.java"));
	}
	
	@Test
	// expect it get the same number of files in directory, and have each .java file name in list names
	public void readDir() throws IOException {
		String pathname = DIRPATH+ File.separator + "f1";
		Main.getFilesInDir(pathname, streams, names);
		assertEquals(names.size(), streams.size());
		assertEquals(names.size(), 6);				//number of files in directory
		assertTrue(names.contains("C.java"));
		assertTrue(names.contains("D.java"));
		assertTrue(names.contains("F/H.java"));
		assertTrue(names.contains("foo/E.java"));
		assertTrue(names.contains("A.java"));
		assertTrue(names.contains("B.java"));
	}
	@Test
	// expect readFileToString() as same as the thing contain in the .java file
	public void readability() throws IOException {
		String pathname = DIRPATH + File.separator + "f1" + File.separator + "A.java";
		Main.getFilesInDir(pathname, streams, names);
		assertEquals(names.size(), streams.size());
		assertEquals(names.size(), 1);
		assertEquals(Main.readFileToString(streams.get(0)), "class A {}\r\n" + "");
	}
	
	@Test
	public void wholeThing1() throws IOException {
		String pathname = DIRPATH+ File.separator + "f1";
		CountingVisitor v = CountingVisitor.getTheTing();
		int[] i;
		List<int[]> count;
		List<String> types;
		Main.getFilesInDir(pathname, streams, names);
		Main.parseAll((Stack<InputStream>)streams, (Stack<String>)names, pathname);
		count = v.getCounts();
		types = v.getJavaType();
		assertEquals(count.size(), types.size());
		assertEquals(count.size(), 8);
		
		assertTrue(types.contains("A"));
		i = count.get(types.indexOf("A"));
		assertEquals(i[0], 1);
		assertEquals(i[1], 0);
		
		assertTrue(types.contains("B"));
		i = count.get(types.indexOf("B"));
		assertEquals(i[0], 1);
		assertEquals(i[1], 9);
		
		assertTrue(types.contains("C"));
		i = count.get(types.indexOf("C"));
		assertEquals(i[0], 1);
		assertEquals(i[1], 5);
		
		assertTrue(types.contains("D"));
		i = count.get(types.indexOf("D"));
		assertEquals(i[0], 1);
		assertEquals(i[1], 0);
		
		assertTrue(types.contains("D.E"));
		i = count.get(types.indexOf("D.E"));
		assertEquals(i[0], 1);
		assertEquals(i[1], 2);
		
		//it should not be there
		assertFalse(types.contains("E"));
		
		assertTrue(types.contains("foo.E"));
		i = count.get(types.indexOf("foo.E"));
		assertEquals(i[0], 1);
		assertEquals(i[1], 4);
		
		assertFalse(types.contains("F"));
		
		assertTrue(types.contains("int"));
		i = count.get(types.indexOf("int"));
		assertEquals(i[0], 0);
		assertEquals(i[1], 1);
	}
	
	@Test
	public void wholeThing2() throws IOException {
		String pathname = DIRPATH+ File.separator + "f2";
		CountingVisitor v = CountingVisitor.getTheTing();
		int[] i;
		List<int[]> count;
		List<String> types;
		Main.getFilesInDir(pathname, streams, names);
		Main.parseAll((Stack<InputStream>)streams, (Stack<String>)names, pathname);
		count = v.getCounts();
		types = v.getJavaType();
		assertEquals(count.size(), types.size());
		assertEquals(count.size(), 7);
		
		assertTrue(types.contains("int"));
		i = count.get(types.indexOf("int"));
		assertEquals(i[0], 0);
		assertEquals(i[1], 3);
		
		assertTrue(types.contains("Yas"));
		i = count.get(types.indexOf("Yas"));
		assertEquals(i[0], 1);
		assertEquals(i[1], 0);
		
		assertTrue(types.contains("java.lang.String"));
		i = count.get(types.indexOf("java.lang.String"));
		assertEquals(i[0], 0);
		assertEquals(i[1], 4);
		
		assertTrue(types.contains("Stuff"));
		i = count.get(types.indexOf("Stuff"));
		assertEquals(i[0], 2);
		assertEquals(i[1], 0);
		
		assertTrue(types.contains("Stuff.OK"));
		i = count.get(types.indexOf("Stuff.OK"));
		assertEquals(i[0], 1);
		assertEquals(i[1], 3);
		
		assertTrue(types.contains("List"));
		i = count.get(types.indexOf("List"));
		assertEquals(i[0], 0);
		assertEquals(i[1], 1);
		
		assertTrue(types.contains("negation.Nope"));
		i = count.get(types.indexOf("negation.Nope"));
		assertEquals(i[0], 1);
		assertEquals(i[1], 2);
	}
	
	@Test
	public void wholeThing3() throws IOException {
		String pathname = DIRPATH+ File.separator + "f3";
		CountingVisitor v = CountingVisitor.getTheTing();
		int[] i;
		List<int[]> count;
		List<String> types;
		Main.getFilesInDir(pathname, streams, names);
		Main.parseAll((Stack<InputStream>)streams, (Stack<String>)names, pathname);
		count = v.getCounts();
		types = v.getJavaType();
		assertEquals(count.size(), types.size());
		assertEquals(count.size(), 8);
		
		assertTrue(types.contains("A"));
		i = count.get(types.indexOf("A"));
		assertEquals(i[0], 1);
		assertEquals(i[1], 0);
		
		assertTrue(types.contains("B"));
		i = count.get(types.indexOf("B"));
		assertEquals(i[0], 1);
		assertEquals(i[1], 9);
		
		assertTrue(types.contains("C"));
		i = count.get(types.indexOf("C"));
		assertEquals(i[0], 1);
		assertEquals(i[1], 5);
		
		assertTrue(types.contains("D"));
		i = count.get(types.indexOf("D"));
		assertEquals(i[0], 1);
		assertEquals(i[1], 0);
		
		assertTrue(types.contains("D.E"));
		i = count.get(types.indexOf("D.E"));
		assertEquals(i[0], 1);
		assertEquals(i[1], 2);
		
		//it should not be there
		assertFalse(types.contains("E"));
		
		assertTrue(types.contains("foo.E"));
		i = count.get(types.indexOf("foo.E"));
		assertEquals(i[0], 1);
		assertEquals(i[1], 4);
		
		assertFalse(types.contains("F"));
		
		assertTrue(types.contains("int"));
		i = count.get(types.indexOf("int"));
		assertEquals(i[0], 0);
		assertEquals(i[1], 1);
	}
	
}
