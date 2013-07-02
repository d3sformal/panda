package gov.nasa.jpf.abstraction.predicate.grammar;

import java.util.List;

public class Negation extends Predicate {
	public Predicate predicate;
	
	public Negation(Predicate predicate) {
		this.predicate = predicate;
	}
	
	@Override
	public List<AccessPath> getPaths() {
		return predicate.getPaths();
	}
	
	@Override
	public String toString() {
		return "not(" + predicate.toString() + ")";
	}
}
