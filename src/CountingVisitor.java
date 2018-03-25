import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.*;

public class CountingVisitor extends org.eclipse.jdt.core.dom.ASTVisitor {

	private static CountingVisitor thatOneInstance = new CountingVisitor();
	
    private List<int[]> counts;		//index 0, declarations; index 1, references
    private List<String> types;
    
    public static CountingVisitor getTheTing() {
    	return thatOneInstance;
    }
    
    public static void reset() {
    	thatOneInstance = new CountingVisitor();
    }

    private CountingVisitor() {
        counts = new ArrayList<int[]>();
        types = new ArrayList<String>();
    }

    public List<int[]> getCounts() {
        return counts;
    }

    public List<String> getJavaType() {
        return types;
    }

    private void checkDeclarations(String typeName) {
        if(types.contains(typeName)) {
            int i = types.indexOf(typeName);
            counts.get(i)[0]++;
        }
        else {
            types.add(typeName);
            counts.add(new int[] {1, 0});
        }
    }
    
    private void checkRef(String name) {
    	if(types.contains(name)){
            int i = types.indexOf(name);
            counts.get(i)[1]++;
        }
        else {
        	types.add(name);
        	counts.add(new int[] {0, 1});
        }
    }
    
    public void preVisit(ASTNode node) {
    	System.out.println("Node: " + node.getClass());
    	if(node.getParent() != null)
    		System.out.println("Parent: " + node.getParent().getClass());
    	else
    		System.out.println("No Parent");
    	System.out.println(node);
    	System.out.println("-----------");
    }
    
    private boolean happened = false;
    public void postVisit(ASTNode node) {
    	if(types.contains("foo.C") && !happened) {
    		System.out.println("IT HAS HAPPENED " + node.getClass());
    		happened = true;
    	}
    }

    @Override
    public boolean visit(VariableDeclarationStatement node) {
        String typeName = node.getType().resolveBinding().getQualifiedName();

        checkRef(typeName);

        return true;
    }
    
    public boolean visit(FieldDeclaration node) {
        String name = node.getType().resolveBinding().getQualifiedName();
        checkRef(name);
    	return true;
    }
    
    public boolean visit(ParameterizedType node) {
        List nodes = node.typeArguments();
        for (Object n : nodes) {
            checkRef(((ASTNode)n).getClass().getSimpleName());
        }
        checkRef(node.resolveBinding().getQualifiedName());
    	return true;
    }

    public boolean visit(TypeDeclaration node) {
        String name = node.resolveBinding().getQualifiedName();
        checkDeclarations(name);
        return true;
    }
    public boolean visit(AnnotationTypeDeclaration node) {
    	String name = node.resolveBinding().getQualifiedName();
    	checkDeclarations(name);
    	return true;
    }
    public boolean visit(EnumConstantDeclaration node) {
    	String name = node.resolveConstructorBinding().getDeclaringClass().getQualifiedName();
    	checkDeclarations(name);
    	return true;
    }
    
    public boolean visit(ImportDeclaration node) {
    	String name = node.resolveBinding().getName();
    	checkRef(name);
    	return true;
    }
    
    public boolean visit(ClassInstanceCreation node) {
    	String name = node.resolveTypeBinding().getQualifiedName();
    	checkRef(name);
    	return true;
    }
    
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
    
    public boolean visit(SingleVariableDeclaration node) {
    	String name = node.getType().resolveBinding().getQualifiedName();
    	checkRef(name);
    	return true;
    }

    @Override
    public boolean visit(PrimitiveType node) {
        String name = node.getPrimitiveTypeCode().toString();
        if(!(node.getParent() instanceof SingleVariableDeclaration))	//because counted already
        	checkRef(name);
        return true;
    }
}
