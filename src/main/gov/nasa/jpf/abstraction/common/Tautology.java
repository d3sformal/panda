package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;

import java.util.Set;
import java.util.Map;

/**
 * A predicate with a constant truth value ~ true
 */
public class Tautology extends Predicate {

    private static Tautology instance;
	
	protected Tautology() {
	}

	@Override
	public void accept(PredicatesComponentVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public void addAccessExpressionsToSet(Set<AccessExpression> out) {
	}

	@Override
	public Predicate replace(Map<AccessExpression, Expression> replacements) {
		return this;
	}
	
	public static Predicate create() {
		//return new Tautology();
        if (instance == null) {
            instance = new Tautology();
        }

        return instance;
	}
	
	@Override
	public Predicate update(AccessExpression expression, Expression newExpression) {
		return this;
	}

}
