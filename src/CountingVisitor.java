import java.util.ArrayList;
import java.util.List;

public class CountingVisitor extends org.eclipse.jdt.core.dom.ASTVisitor {

	private static CountingVisitor thatOneInstance = new CountingVisitor();
	
    private List<int[]> counts;
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
}
