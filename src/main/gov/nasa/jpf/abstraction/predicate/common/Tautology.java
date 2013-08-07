package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;

import java.util.ArrayList;
import java.util.List;

public class Tautology extends Predicate {
	
	protected Tautology() {
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public List<AccessExpression> getPaths() {
		return new ArrayList<AccessExpression>();
	}

	@Override
	public Predicate replace(AccessExpression formerPath, Expression expression) {
		return this;
	}
	
	public static Predicate create() {
		return new Tautology();
	}

}
