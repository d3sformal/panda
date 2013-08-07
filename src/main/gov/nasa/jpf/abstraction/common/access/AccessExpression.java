package gov.nasa.jpf.abstraction.common.access;

import java.util.List;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesStringifier;
import gov.nasa.jpf.abstraction.common.impl.PredicatesDotStringifier;
import gov.nasa.jpf.abstraction.common.impl.PredicatesFunctionStringifier;

public abstract class AccessExpression extends Expression {
	public static enum NotationPolicy {
		DOT_NOTATION,
		FUNCTION_NOTATION
	}
	
	public static NotationPolicy policy = NotationPolicy.FUNCTION_NOTATION;

	public static PredicatesStringifier getStringifier(NotationPolicy policy) {
		switch (policy) {
		case DOT_NOTATION:
			return new PredicatesDotStringifier();
		case FUNCTION_NOTATION:
			return new PredicatesFunctionStringifier();
		}
		
		return null;
	}
	
	public abstract List<AccessExpression> getSubAccessExpressions();
	public List<AccessExpression> getAccessExpressions() {
		List<AccessExpression> ret = getSubAccessExpressions();
		
		ret.add(this);
		
		return ret;
	}
	
	public abstract Root getRoot();
	public AccessExpression getTail() {
		return this;
	}
	
	@Override
	public abstract AccessExpression clone();
	
	public boolean isPrefix(AccessExpression expression) {
		throw new RuntimeException("Not Yet Re-Implemented.");
	}
	
	public boolean isSimilarToPrefix(AccessExpression path) {
		throw new RuntimeException("Not Yet Re-Implemented.");
	}
	
	public boolean isProperPrefix(AccessExpression expression) {
		return getLength() < expression.getLength() && isPrefix(expression);
	}
	
	public abstract int getLength();
	public abstract AccessExpression cutTail();
}
