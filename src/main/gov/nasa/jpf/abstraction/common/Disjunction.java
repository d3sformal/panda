package gov.nasa.jpf.abstraction.common;

import java.util.Map;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;

/**
 * Disjunction represents a logical OR of two predicates. (e.g. x = 1 OR x = 2)
 */
public class Disjunction extends Formula {
	protected Disjunction(Predicate a, Predicate b) {
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
	 * Method used to create disjunctions of predicates.
	 * The method checks its arguments and produces a disjunction of the two predicates or an equivalent simplification with the aim to shorten SMT input and make it more readable too.
	 * 
	 * @return Simplified formula / predicate according to (a OR true ~ true, a OR false ~ a, and other logical rules)
	 */
	public static Predicate create(Predicate a, Predicate b) {
		if (!argumentsDefined(a, b)) return null;
		
		if (a instanceof Tautology) {
			return Tautology.create();
		}
		if (b instanceof Tautology) {
			return Tautology.create();
		}
		if (a instanceof Contradiction) {
			return b;
		}
		if (b instanceof Contradiction) {
			return a;
		}

		return new Conjunction(a, b);
	}
	
	@Override
	public Predicate update(AccessExpression expression, Expression newExpression) {
		return create(a.update(expression, newExpression), b.update(expression, newExpression));
	}

}
