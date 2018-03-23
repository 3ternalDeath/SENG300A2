import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.junit.Before;
import org.junit.Test;

public class MainTests {
	
	private static final String DIRPATH = "C:\\Users\\abmis\\Documents\\GitHub\\SENG300A2\\TestFiles";
	private List<InputStream> streams;
	private List<String> names;
	
	@Before
	public void setup() {
		CountingVisitor.reset();
		streams = new Stack<>();
		names = new Stack<>();
	}
	
	@Test
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
	public void readDir() throws IOException {
		String pathname = DIRPATH+ File.separator + "f1";
		Main.getFilesInDir(pathname, streams, names);
		assertEquals(names.size(), streams.size());
		assertEquals(names.size(), 6);
		assertTrue(names.contains("C.java"));
		assertTrue(names.contains("D.java"));
		assertTrue(names.contains("F/H.java"));
		assertTrue(names.contains("foo/E.java"));
		assertTrue(names.contains("A.java"));
		assertTrue(names.contains("B.java"));
	}
	@Test
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
		for (String t : types){
			System.out.println(t);
		}
		assertEquals(count.size(), 9);
		
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
		int i;
		List<int[]> count;
		List<String> types;
		Main.getFilesInDir(pathname, streams, names);
		Main.parseAll((Stack<InputStream>)streams, (Stack<String>)names, pathname);
		count = v.getCounts();
		types = v.getJavaType();
		assertEquals(count.size(), types.size());
		assertEquals(count.size(), 9);
	}
	
}
