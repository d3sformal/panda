package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;

public class Disjunction extends Formula {
	protected Disjunction(Predicate a, Predicate b) {
		super(a, b);
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Predicate replace(AccessExpression formerPath, Expression expression) {
		return create(a.replace(formerPath, expression), b.replace(formerPath, expression));
	}
	
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

}
