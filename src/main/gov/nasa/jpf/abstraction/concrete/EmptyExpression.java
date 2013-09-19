package gov.nasa.jpf.abstraction.concrete;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;
import gov.nasa.jpf.abstraction.common.Contradiction;
import gov.nasa.jpf.abstraction.common.Predicate;

/**
 * A dummy expression (may be used when the there would be no expression = null)
 */
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
	
	public static EmptyExpression create() {
		return new EmptyExpression();
	}
	
	@Override
	public EmptyExpression clone() {
		return create();
	}
	
	@Override
	public String toString() {
		return toString(Notation.policy);
	}

	@Override
	public String toString(Notation policy) {
    	return Notation.convertToString(this, policy);
	}

	@Override
	public Expression update(AccessExpression expression, Expression newExpression) {
		return clone();
	}

	@Override
	public Predicate preconditionForBeingFresh() {
		return Contradiction.create();
	}

	@Override
	public Expression replace(Map<AccessExpression, Expression> replacements) {
		return this;
	}

}
