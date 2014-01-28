package gov.nasa.jpf.abstraction.common.access.impl;

import java.util.Set;
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

    private static UndefinedAccessExpression instance;
	
	protected UndefinedAccessExpression() {
		super(null);
	}

	@Override
	public void accept(PredicatesComponentVisitor visitor) {
		visitor.visit((Undefined) this);
	}

    @Override
    public UndefinedAccessExpression createShallowCopy() {
        return this;
    }
	
	public static UndefinedAccessExpression create() {
		//return new UndefinedAccessExpression();
        if (instance == null) {
            instance = new UndefinedAccessExpression();
        }

        return instance;
	}

	@Override
	public void addAccessSubExpressionsToSet(Set<AccessExpression> out) {
	}

	@Override
	public AccessExpression replaceSubExpressions(Map<AccessExpression, Expression> replacements) {
		return this;
	}

	@Override
	public Root getRoot() {
		return this;
	}

}
