package gov.nasa.jpf.abstraction.concrete;

import java.util.ArrayList;
import java.util.List;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.NotationPolicy;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;
import gov.nasa.jpf.abstraction.predicate.common.Contradiction;
import gov.nasa.jpf.abstraction.predicate.common.Predicate;

public class EmptyExpression implements Expression {
	
	protected EmptyExpression() {
	}

	@Override
	public List<AccessExpression> getAccessExpressions() {
		return new ArrayList<AccessExpression>();
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Expression replace(AccessExpression formerPath, Expression expression) {
		return this;
	}
	
	public static EmptyExpression create() {
		return new EmptyExpression();
	}
	
	@Override
	public EmptyExpression clone() {
		return create();
	}
	
	@Override
	public String toString() {
		return toString(NotationPolicy.policy);
	}

	@Override
	public String toString(NotationPolicy policy) {
    	return NotationPolicy.convertToString(this, policy);
	}

	@Override
	public Expression update(AccessExpression expression, Expression newExpression) {
		return clone();
	}

	@Override
	public Predicate preconditionForBeingFresh() {
		return Contradiction.create();
	}

}
