package main;

public class TypeCounter {
	private static TypeCounter counter;
	
	private int all;
    private int nested;
    private int local;
    private int anonymous;
    private int annotation;
    private int enums;
    
	private TypeCounter() {
		all = nested= local = anonymous = annotation = enums = 0;
	}
	public static TypeCounter getInstance() {
		if(counter == null)
			counter = new TypeCounter();
		return counter;
	}
	public void incAll() {
		all++;
	}
	public void incNested() {
		nested++;
	}
	public void incLocal() {
		local++;
	}
	public void incAnonymous() {
		anonymous++;
	}
	public void incAnnotation() {
		annotation++;
	}
	public void incEnums() {
		enums++;
	}
	public String toString() {
		return "All:" + all + "\tNested:" + nested + "\tLocal: " + local +
				"\tAnonymous: " + anonymous + "\tAnnotation: " + annotation + "\tEnums: " + enums;
	}
}
