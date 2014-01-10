package gov.nasa.jpf.abstraction.common.access.impl;

import java.util.List;

import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ObjectAccessExpression;
import gov.nasa.jpf.abstraction.common.access.Root;

/**
 * Expressions accessing objects (fields, array elements, array length):
 * 
 * o.f
 * a[i]
 * a.length
 */
public abstract class DefaultObjectAccessExpression extends DefaultAccessExpression implements ObjectAccessExpression {

	private AccessExpression expression;
	
	protected DefaultObjectAccessExpression(AccessExpression expression) {
		this.expression = expression;
	}

	@Override
	public void accept(PredicatesComponentVisitor visitor) {
		expression.accept(visitor);
	}

	@Override
	public final int getLength() {
		return getObject().getLength() + 1;
	}

	@Override
	public final AccessExpression cutTail() {
		return getObject().clone();
	}

	@Override
	public void setObject(AccessExpression expression) {
		this.expression = expression;
	}
	
	@Override
	public AccessExpression getObject() {
		return expression;
	}
	
	@Override
	public final AccessExpression get(int depth) {
		if (depth > getLength()) {
			return null;
		}
		if (depth == getLength()) {
			return this;
		}
		
		return getObject().get(depth);
	}
	
	@Override
	public abstract DefaultObjectAccessExpression clone();

	@Override
	public List<AccessExpression> getAccessSubExpressions() {
		return getObject().getAccessSubExpressions();
	}

	@Override
	public final Root getRoot() {
		return getObject().getRoot();
	}
}
