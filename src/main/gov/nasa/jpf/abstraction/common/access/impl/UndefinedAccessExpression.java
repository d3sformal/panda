package gov.nasa.jpf.abstraction.common.access.impl;

import java.util.List;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;
import gov.nasa.jpf.abstraction.common.Undefined;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.Root;

public class UndefinedAccessExpression extends DefaultRoot implements Undefined {
	
	protected UndefinedAccessExpression() {
		super(null);
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit((Undefined) this);
	}
	
	@Override
	public UndefinedAccessExpression clone() {
		return create();
	}
	
	public static UndefinedAccessExpression create() {
		return new UndefinedAccessExpression();
	}

	@Override
	public List<AccessExpression> getSubAccessExpressions() {
		return null;
	}

	@Override
	public AccessExpression replaceSubExpressions(AccessExpression expression, Expression newExpression) {
		return clone();
	}

	@Override
	public Root getRoot() {
		return this;
	}

}