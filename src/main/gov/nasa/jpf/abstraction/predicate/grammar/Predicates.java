package gov.nasa.jpf.abstraction.predicate.grammar;

import java.util.List;

public class Predicates {
	public List<Context> contexts;
	
	public Predicates(List<Context> contexts) {
		this.contexts = contexts;
	}
	
	@Override
	public String toString() {
		String ret = "";
		
		for (Context c : contexts) {
			ret += c.toString() + "\n";
		}
		
		return ret;
	}
}
