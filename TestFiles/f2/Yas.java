import negation.Nope;
public interface Yas{
	public String hi(Nope n){
		if(n == Nope.No)
			return "hi";
	}
}