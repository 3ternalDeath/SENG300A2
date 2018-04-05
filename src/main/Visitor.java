package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.*;

//Modified ASTVisitor
/**
 * AST Visitor for only visiting Declarations
 * @author SeungBin, Yim
 */
public class Visitor extends ASTVisitor{
	Map<String, Integer[]> map = new HashMap<String, Integer[]>();
	//AllTypes	NestedTypes	LocalTypes	AnonymousTypes	AnnotationTypes	OtherTypes
	TypeCounter counter = TypeCounter.getInstance();
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
	private String getBindingName(ASTNode node) {
			if(node instanceof Type) {
				if(((Type) node).resolveBinding() == null) return node.toString();
				else if (((Type) node).resolveBinding().getQualifiedName().isEmpty())
					return ((Type) node).resolveBinding().getName();
				else
					return ((Type) node).resolveBinding().getQualifiedName();
			}
			else if(node instanceof AbstractTypeDeclaration){
				if(((AbstractTypeDeclaration) node).resolveBinding() == null) return node.toString();
				else if (((AbstractTypeDeclaration) node).resolveBinding().getQualifiedName().isEmpty())
					return ((AbstractTypeDeclaration) node).resolveBinding().getName();
				else
					return ((AbstractTypeDeclaration) node).resolveBinding().getQualifiedName();
			}
			return node.toString();
	}
	//--------------------------------------------------------------
	//References
	//Visits when there is a primitive type (int, char, ...)
	@Override
	public boolean visit(PrimitiveType node) {
		if(!node.toString().equals("void")) {
			countReference(getBindingName(node));
		}
		return super.visit(node);
	}
	//Visits when there is a SimpleType type (non-Primitive types like java.lang.String)
	@Override
	public boolean visit(SimpleType node) {
		String key = getBindingName(node);
		//Not sure though
		if(node.getParent().toString().endsWith("[]"))
			key += "[]";
		countReference(key);
		return super.visit(node);
	}
	@Override
	public boolean visit(MarkerAnnotation node) {
		countReference(getBindingName(node));
		return super.visit(node);
	}
	
	//--------------------------------------------------------------
	//Declarations
	//1-1. AnnotationType declaration
	@Override
	public boolean visit(AnnotationTypeDeclaration node) {
		countDeclaration(getBindingName(node));
		counter.incAnnotation();
		counter.incAll();
		return super.visit(node);
	}

	//2. Enum declaration
	@Override
	public boolean visit(EnumDeclaration node) {
		countDeclaration(getBindingName(node));
		counter.incEnums();
		counter.incAll();
		return super.visit(node);
	}
	//3-4. Class / Interface declaration
	@Override
	public boolean visit(TypeDeclaration node) {
		countDeclaration(getBindingName(node));
		
		if(node.getParent() instanceof TypeDeclaration)
			counter.incNested();
        if(node.getParent() instanceof MethodDeclaration)
        	counter.incLocal();
        
        counter.incAll();
		return super.visit(node);
	}
	//5. AnonymousClass Declaration
	@Override
	public boolean visit(AnonymousClassDeclaration node) {
		countDeclaration(getBindingName(node));
		counter.incAnonymous();
    	counter.incAll();
    	return super.visit(node);
    }
	//Import Declaration //should be counting as references
	@Override
	public boolean visit(ImportDeclaration node) {
		countReference(getBindingName(node));
		return super.visit(node);
	}
	
}
