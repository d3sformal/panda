package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.Contradiction;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.impl.DefaultExpression;

import java.util.Set;
import java.util.Map;

/**
 * A common class for symbolic expressions of all the numerical constants present in the execution
 */
public class Constant extends DefaultExpression implements PrimitiveExpression {
	public Number value;
	
	protected Constant(Number value) {
		this.value = value;
	}

	@Override
	public void addAccessExpressionsToSet(Set<AccessExpression> out) {
	}

	@Override
	public void accept(PredicatesComponentVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Constant replace(Map<AccessExpression, Expression> replacements) {
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
	public Constant update(AccessExpression expression, Expression newExpression) {
		return this;
	}

	@Override
	public Predicate getPreconditionForBeingFresh() {
		return Contradiction.create();
	}
}
