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
    
    private boolean checkDeclarations(AbstractTypeDeclaration node) {
        String name = node.resolveBinding().getQualifiedName();
        if(types.contains(name)) {
            int i = types.indexOf(name);
            counts.get(i)[0]++;
            
        }
        else {
            types.add(name);
            counts.add(new int[] {1, 0});
        }
        return true;
    }
    
    private boolean checkRef(String name) {
    	if(types.contains(name)){
            int i = types.indexOf(name);
            counts.get(i)[1]++;
        }
        else {
        	types.add(name);
        	counts.add(new int[] {0, 1});
        }
    	
    	return true;
    }

    /////////////////////// declarations count ///////////////////////////
    
    public boolean visit(TypeDeclaration node) {
    	return checkDeclarations(node);
    }

    @Override
    public boolean visit(AnnotationTypeDeclaration node) {
        return checkDeclarations(node);
    }

    @Override
    public boolean visit(EnumDeclaration node) {
        return checkDeclarations(node);
    }

    /////////////////////// references count ///////////////////////////

    @Override
    public boolean visit(SimpleName node) {
        String name = node.getFullyQualifiedName();
        return checkRef(name);
    }

    @Override
    public boolean visit(PrimitiveType node) {
        String name = node.getPrimitiveTypeCode().toString();
        return checkRef(name);
    }
}
