package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.predicate.common.Contradiction;
import gov.nasa.jpf.abstraction.predicate.common.Predicate;

import java.util.ArrayList;
import java.util.List;

public class Constant implements PrimitiveExpression {
	public Number value;
	
	protected Constant(Number value) {
		this.value = value;
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
	public Constant replace(AccessExpression formerPath, Expression expression) {
		return this;
	}
	
	public static Constant create(int value) {
		return new Constant(value);
	}
	
	public static Constant create(float value) {
		return new Constant(value);
	}
	
	public static Constant create(long value) {
		return new Constant(value);
	}
	
	public static Constant create(double value) {
		return new Constant(value);
	}
	
	@Override
	public Constant clone() {
		return new Constant(value);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Constant) {
			Constant c = (Constant) o;
			
			return value.equals(c.value);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return value.hashCode();
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
	public Constant update(AccessExpression expression, Expression newExpression) {
		return clone();
	}

	@Override
	public Predicate preconditionForBeingFresh() {
		return Contradiction.create();
	}
}
