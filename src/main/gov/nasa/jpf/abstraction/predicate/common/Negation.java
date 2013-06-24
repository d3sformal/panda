package gov.nasa.jpf.abstraction.predicate.common;

public class Negation extends Predicate {
	public Predicate predicate;
	
	public Negation(Predicate predicate) {
		this.predicate = predicate;
	}
	
	@Override
	public String toString() {
		return "not(" + predicate.toString() + ")";
	}
}
