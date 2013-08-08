package gov.nasa.jpf.abstraction.common.access.impl;

import java.util.List;

import gov.nasa.jpf.abstraction.common.PredicatesVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ObjectAccessExpression;
import gov.nasa.jpf.abstraction.common.access.Root;

public abstract class DefaultObjectAccessExpression extends DefaultAccessExpression implements ObjectAccessExpression {

	private AccessExpression expression;
	
	protected DefaultObjectAccessExpression(AccessExpression expression) {
		this.expression = expression;
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
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
	public final void setObject(AccessExpression expression) {
		this.expression = expression;
	}
	
	@Override
	public final AccessExpression getObject() {
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
	public List<AccessExpression> getSubAccessExpressions() {
		return getObject().getSubAccessExpressions();
	}

	@Override
	public final Root getRoot() {
		return getObject().getRoot();
	}
}
