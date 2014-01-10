package gov.nasa.jpf.abstraction.common;

import java.util.Map;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;

/**
 * Conjunction represents a logical AND of two predicates. (e.g. x > 0 AND x < 10)
 */
public class Conjunction extends Formula {
	
	protected Conjunction(Predicate a, Predicate b) {
		super(a, b);
	}
	
	@Override
	public void accept(PredicatesComponentVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Predicate replace(Map<AccessExpression, Expression> replacements) {
		return create(a.replace(replacements), b.replace(replacements));
	}
	
	/**
	 * Method used to create conjunctions of predicates.
	 * The method checks its arguments and produces a conjunction of the two predicates or an equivalent simplification with the aim to shorten SMT input and make it more readable too.
	 * 
	 * @return Simplified formula / predicate according to (a AND true ~ a, a AND false ~ false, and other logical rules)
	 */
	public static Predicate create(Predicate a, Predicate b) {
		if (!argumentsDefined(a, b)) return null;
		
		if (a instanceof Tautology) {
			return b;
		}
		if (b instanceof Tautology) {
			return a;
		}
		if (a instanceof Contradiction) {
			return Contradiction.create();
		}
		if (b instanceof Contradiction) {
			return Contradiction.create();
		}

		return new Conjunction(a, b);
	}

	@Override
	public Predicate update(AccessExpression expression, Expression newExpression) {
		return create(a.update(expression, newExpression), b.update(expression, newExpression));
	}

}
