import static org.junit.Assert.assertEquals;
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
		System.out.println(names);
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
	
	
}
