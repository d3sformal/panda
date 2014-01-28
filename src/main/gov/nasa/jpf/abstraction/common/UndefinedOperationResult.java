package gov.nasa.jpf.abstraction.common;

import java.util.Map;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;

/**
 * A concrete instance of an undefined result
 */
public class UndefinedOperationResult extends Operation implements Undefined {

    private static UndefinedOperationResult instance;
	
	protected UndefinedOperationResult() {
		super(null, null);
	}

	@Override
	public void accept(PredicatesComponentVisitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public UndefinedOperationResult clone() {
		return this;
	}
	
	public static UndefinedOperationResult create() {
		//return new UndefinedOperationResult();
        if (instance == null) {
            instance = new UndefinedOperationResult();
        }

        return instance;
	}

	@Override
	public Expression replace(Map<AccessExpression, Expression> replacements) {
		return clone();
	}

	@Override
	public Expression update(AccessExpression expression, Expression newExpression) {
		return clone();
	}

}
