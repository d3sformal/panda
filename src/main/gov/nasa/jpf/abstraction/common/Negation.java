package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.Contradiction;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.Tautology;

import java.util.List;
import java.util.Map;

/**
 * Negation of a predicate.
 * 
 * Can be used to express !=, >= from = and <, and other.
 */
public class Negation extends Predicate {
	public Predicate predicate;
	
	protected Negation(Predicate predicate) {
		this.predicate = predicate;
	}
	
	@Override
	public List<AccessExpression> getPaths() {
		return predicate.getPaths();
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Predicate replace(Map<AccessExpression, Expression> replacements) {
		return create(predicate.replace(replacements));
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

	@Override
	public Predicate update(AccessExpression expression, Expression newExpression) {
		return create(predicate.update(expression, newExpression));
	}
}
