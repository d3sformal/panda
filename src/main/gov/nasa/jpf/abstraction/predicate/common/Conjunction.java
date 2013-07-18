package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;

public class Conjunction extends Junction {
	
	protected Conjunction(Predicate a, Predicate b) {
		super(a, b);
	}
	
	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Predicate replace(AccessPath formerPath, Expression expression) {
		return create(a.replace(formerPath, expression), b.replace(formerPath, expression));
	}
	
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

}
