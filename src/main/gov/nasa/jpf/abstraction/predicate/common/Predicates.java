package gov.nasa.jpf.abstraction.predicate.common;

import java.util.List;

public class Predicates implements PredicatesVisitable {
	public List<Context> contexts;
	
	public Predicates(List<Context> contexts) {
		this.contexts = contexts;
	}
	
	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public String toString() {
		PredicatesStringifier stringifier = AccessPath.getDefaultStringifier();
		
		accept(stringifier);
		
		return stringifier.getString();
	}
}
