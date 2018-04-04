package main;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.*;

//Modified ASTVisitor
/**
 * AST Visitor for only visiting Declarations
 * @author SeungBin, Yim
 */
public class Visitor extends ASTVisitor{
	Map<String, Integer[]> map = new HashMap<String, Integer[]>();
	
	public Map<String, Integer[]> getMap(){
		return map;
	}
	private void countReference(String key) {
		Integer[] count = map.get(key);
		if(count != null) count[1]++;
		else count = new Integer[] {0,1};
		map.put(key, count);
	}
	private void countDeclaration(String key) {
		Integer[] count = map.get(key);
		if(count != null) count[0]++;
		else count = new Integer[] {1,0};
		map.put(key, count);
	}
	//--------------------------------------------------------------
	//References
	//Visits when there is a primitive type (int, char, ...)
	@Override
	public boolean visit(PrimitiveType node) {
		if(!node.toString().equals("void"))
			countReference(node.resolveBinding().getQualifiedName());
		return super.visit(node);
	}
	//Visits when there is a SimpleType type (non-Primitive types like java.lang.String)
	@Override
	public boolean visit(SimpleType node) {
		String key = node.resolveBinding().getQualifiedName();
		//Not sure though
		if(node.getParent().toString().endsWith("[]"))
			key += "[]";
		countReference(key);
		return super.visit(node);
	}
	@Override
	public boolean visit(MarkerAnnotation node) {
		countReference(node.resolveTypeBinding().getQualifiedName());
		return super.visit(node);
	}
	
	//--------------------------------------------------------------
	//Declarations
	//1-1. AnnotationType declaration
	@Override
	public boolean visit(AnnotationTypeDeclaration node) {
		countDeclaration(node.resolveBinding().getQualifiedName());
		return super.visit(node);
	}

	//2. Enum declaration
	@Override
	public boolean visit(EnumDeclaration node) {
		countDeclaration(node.resolveBinding().getQualifiedName());
		return super.visit(node);
	}
	//3-4. Class / Interface declaration
	@Override
	public boolean visit(TypeDeclaration node) {
		String key;
		if(node.resolveBinding().getQualifiedName().isEmpty())
			key= node.resolveBinding().getName();
		else
			key = node.resolveBinding().getQualifiedName();
		countDeclaration(key);
		return super.visit(node);
	}
	//5. Import Declaration
	@Override
	public boolean visit(ImportDeclaration node) {
		countDeclaration(node.resolveBinding().getName());
		return super.visit(node);
	}
	
}
