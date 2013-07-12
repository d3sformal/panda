package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.Expression;

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
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Negation replace(AccessPath formerPath, Expression expression) {
		return new Negation(predicate.replace(formerPath, expression));
	}
}
