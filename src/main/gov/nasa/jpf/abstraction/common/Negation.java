package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.predicate.common.Contradiction;
import gov.nasa.jpf.abstraction.predicate.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.common.Tautology;

import java.util.List;

public class Negation extends Predicate {
	public Predicate predicate;
	
	protected Negation(Predicate predicate) {
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
	public Predicate replace(AccessPath formerPath, Expression expression) {
		return create(predicate.replace(formerPath, expression));
	}
	
	public static Predicate create(Predicate predicate) {
		if (predicate == null) {
			return null;
		}
		if (predicate instanceof Negation) {
			return ((Negation) predicate).predicate;
		}
		if (predicate instanceof Tautology) {
			return Contradiction.create();
		}
		if (predicate instanceof Contradiction) {
			return Tautology.create();
		}
		
		return new Negation(predicate);
	}
}
