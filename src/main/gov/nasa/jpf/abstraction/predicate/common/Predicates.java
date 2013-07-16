package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.PredicatesStringifier;
import gov.nasa.jpf.abstraction.common.PredicatesVisitable;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;

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
