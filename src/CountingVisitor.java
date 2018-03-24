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

    @Override
    public boolean visit(VariableDeclarationFragment node) {
        String typeSimpleName;

        if (node.getParent() instanceof VariableDeclarationStatement
                && !(node.getParent().getParent() instanceof Block)) {

            VariableDeclarationStatement declaration = (VariableDeclarationStatement) node.getParent();
            typeSimpleName = declaration.getType().resolveBinding().getQualifiedName();

            checkRef(typeSimpleName);

        }

        return true;
    }

    public boolean visit(TypeDeclaration node) {
        String name = node.resolveBinding().getQualifiedName();
        checkDeclarations(name);
        return true;
    }

    @Override
    public boolean visit(SimpleName node) {

        String name = node.getFullyQualifiedName();

        if (types.contains(name) && !(node.getParent() instanceof TypeDeclaration)) {
            int i = types.indexOf(name);

            if ((node.getParent().getParent() instanceof CompilationUnit)) {

                if (node.getParent() instanceof ImportDeclaration) { // check if it's an 'import' statement
                    counts.get(i)[1]++;
                }

            } else {
                counts.get(i)[1]++;
            }
        }

        if (node.getParent() instanceof TypeDeclaration) {

            if (node.getParent().getParent() instanceof TypeDeclaration) { // if this node is an inner-class
                // get the fullname of the innerclass with its parent, separate with a period (Foo.Bar)
                String parentClass = ((TypeDeclaration) node.getParent().getParent()).getName().toString();
                String innerClass = node.getFullyQualifiedName();
                String fullname = parentClass + "." + innerClass;

                checkDeclarations(fullname);
            }
        }
        return true;
    }

    @Override
    public boolean visit(PrimitiveType node) {
        String name = node.getPrimitiveTypeCode().toString();
        if(types.contains(name)){
            int i = types.indexOf(name);
            counts.get(i)[1]++;
        }
        return true;
    }
}
