import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.TypeDeclaration;

public class CountingVisitor extends org.eclipse.jdt.core.dom.ASTVisitor {

	private static CountingVisitor thatOneInstance = new CountingVisitor();
	
    private List<int[]> counts;		//index 0, declarations; index 1, references
    private List<String> types;
    
    public static CountingVisitor getTheTing() {
    	return thatOneInstance;
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
    
    //ok gotta do the visits now
    
    public boolean visit(TypeDeclaration node) {
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
}
