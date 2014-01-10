package gov.nasa.jpf.abstraction.common.access.impl;

import java.util.List;
import java.util.Map;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.Undefined;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.Root;

/**
 * An access expression produces in circumstances where there is no other valid result 
 */
public class UndefinedAccessExpression extends DefaultRoot implements Undefined {
	
	protected UndefinedAccessExpression() {
		super(null);
	}

	@Override
	public void accept(PredicatesComponentVisitor visitor) {
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
	public List<AccessExpression> getAccessSubExpressions() {
		return null;
	}

	@Override
	public AccessExpression replaceSubExpressions(Map<AccessExpression, Expression> replacements) {
		return clone();
	}

	@Override
	public Root getRoot() {
		return this;
	}

}
