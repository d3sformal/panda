package gov.nasa.jpf.abstraction.concrete;

import java.util.Set;
import java.util.Map;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.Contradiction;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.impl.DefaultExpression;

/**
 * A dummy expression (may be used when there would be no expression = null)
 */
public class EmptyExpression extends DefaultExpression {
	
	protected EmptyExpression() {
	}

	@Override
	public void addAccessExpressionsToSet(Set<AccessExpression> out) {
	}

	@Override
	public void accept(PredicatesComponentVisitor visitor) {
		visitor.visit(this);
	}
	
	public static EmptyExpression create() {
		return new EmptyExpression();
	}
	
	@Override
	public EmptyExpression clone() {
		return create();
	}
	
	@Override
	public Expression update(AccessExpression expression, Expression newExpression) {
		return clone();
	}

	@Override
	public Predicate getPreconditionForBeingFresh() {
		return Contradiction.create();
	}

	@Override
	public Expression replace(Map<AccessExpression, Expression> replacements) {
		return this;
	}

}
