import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.core.dom.*;

public class CountingVisitor extends org.eclipse.jdt.core.dom.ASTVisitor {

	private static CountingVisitor thatOneInstance = new CountingVisitor();
	
    private List<int[]> counts;		// index 0, declarations; index 1, references
    private List<String> types;
    private int nested;
    private int local;
    private int anonymous;
    private int annotation;
    private int enums;
    private int para;
    
    public static CountingVisitor getTheTing() {		// return thatOneInstance
    	return thatOneInstance;
    }
    
    public static void reset() {						// reset thatOneInstance (clean counts, types) 
    	thatOneInstance = new CountingVisitor();
    }

    private CountingVisitor() {
        counts = new ArrayList<int[]>();
        types = new ArrayList<String>();
        nested = 0;
        local = 0;
        anonymous = 0;
        annotation = 0;
        enums = 0;
        para = 0;
    }

    public List<int[]> getCounts() {		// return counts list 
        return counts;
    }

    public List<String> getJavaType() {		// return types list
        return types;
    }

    private void checkDeclarations(String typeName) {
        if(types.contains(typeName)) {
            int i = types.indexOf(typeName);	// if this type is already contain in the list
            counts.get(i)[0]++;					// increment the count of this type
        }
        else {
            types.add(typeName);				//if this is a new type, store it in both type and count
            counts.add(new int[] {1, 0});
        }
    }
    
    private void checkRef(String name) {
    	if(types.contains(name)){
            int i = types.indexOf(name);		//if this type is already contain in the list, increment the count of this type
            counts.get(i)[1]++;
        }
        else {	
        	types.add(name);					//if this is a new type, store it in both type and count
        	counts.add(new int[] {0, 1});
        }
    }

    @Override
    // get the type name of the node
    // check it in reference
    public boolean visit(VariableDeclarationStatement node) {
        String typeName = node.getType().resolveBinding().getQualifiedName();

        checkRef(typeName);

        return true;
    }
    
    // get the name of the node
    // check it in reference
    public boolean visit(FieldDeclaration node) {
        String name = node.getType().resolveBinding().getQualifiedName();
        checkRef(name);
    	return true;
    }
    
    
    //
    public boolean visit(ParameterizedType node) {
        List<?> nodes = node.typeArguments();
        for (Object n : nodes) {
        	if(n instanceof ArrayType) {
        		checkRef(((ArrayType)n).resolveBinding().getQualifiedName());
        	}
        	else if(n instanceof WildcardType) {
        		checkRef(((WildcardType)n).resolveBinding().getQualifiedName());
        	}
        	else {
        		checkRef(((SimpleType)n).resolveBinding().getQualifiedName());
        	}
        }
        para++;
    	return true;
    }

    // check this node in declaration
    // if it is already in type, increment the count of the type
    // if it is not in type, store it in type
    public boolean visit(TypeDeclaration node) {
        String name = node.resolveBinding().getQualifiedName();
        checkDeclarations(name);
        ASTNode parent = node.getParent();
        if(parent instanceof TypeDeclaration)
        	nested++;
        if(parent instanceof MethodDeclaration)
        	local++;
        return true;
    }
    // annotation type
    public boolean visit(AnnotationTypeDeclaration node) {
    	String name = node.resolveBinding().getQualifiedName();
    	checkDeclarations(name);
    	annotation++;
    	return true;
    }
    // enum constant
    public boolean visit(EnumDeclaration node) {
    	String name = node.resolveBinding().getDeclaringClass().getQualifiedName();
    	checkDeclarations(name);
    	enums++;
    	return true;
    }
    
    public boolean visit(AnonymousClassDeclaration node) {
    	anonymous++;
    	return true;
    }
    
    
    // check this node in reference
    // if it is already in type, increment the count of the type
    // if it is not in type, store it in type
    public boolean visit(ImportDeclaration node) {
    	String name = node.resolveBinding().getName();
    	checkRef(name);
    	return true;
    }
    
    // class instance creation type
    public boolean visit(ClassInstanceCreation node) {
    	String name;
    	if(node.resolveTypeBinding() != null)
    		name = node.resolveTypeBinding().getQualifiedName();
    	else if(node.resolveConstructorBinding() != null)
    		name = node.resolveConstructorBinding().getName();
    	else
    		name = node.getType().resolveBinding().getQualifiedName();
    	checkRef(name);
    	return true;
    }
    
    // method declaration
    public boolean visit(MethodDeclaration node) {
    	String typeName = node.resolveBinding().getReturnType().getQualifiedName();
    	
    	if(node.resolveBinding().isConstructor()) {
    		typeName = node.resolveBinding().getDeclaringClass().getQualifiedName();
    		checkDeclarations(typeName);
    	}
    	else if(!(typeName.equals("void")))
    		checkRef(typeName);
    	return true;
    }
    
    // single variable declaration
    public boolean visit(SingleVariableDeclaration node) {
    	String name = node.getType().resolveBinding().getQualifiedName();
    	checkRef(name);
    	return true;
    }

    @Override
    // primitive type
    public boolean visit(PrimitiveType node) {
        String name = node.getPrimitiveTypeCode().toString();
        if(!(node.getParent() instanceof SingleVariableDeclaration))			//because counted already
        	if(!(node.getParent() instanceof FieldDeclaration))					//same
        		if(!(node.getParent() instanceof VariableDeclarationStatement))	//same
        			checkRef(name);												//is there even a point to this visit?
        																		//not realy, just keep it since diagrams are done alredy
        return true;
    }
}
