package gov.nasa.jpf.abstraction.common.impl;

import java.util.Set;
import java.util.Map;

import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.ObjectExpression;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.Contradiction;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.state.symbols.Universe;

public class NullExpression extends Constant implements ObjectExpression {

    private static NullExpression instance;
	
	protected NullExpression() {
		super(Universe.NULL);
	}

	@Override
	public void addAccessExpressionsToSet(Set<AccessExpression> out) {
	}

	@Override
	public NullExpression replace(Map<AccessExpression, Expression> replacements) {
		return this;
	}

	@Override
	public NullExpression update(AccessExpression expression, Expression newExpression) {
		return this;
	}

	@Override
	public void accept(PredicatesComponentVisitor visitor) {
		visitor.visit(this);
	}
	
	public static NullExpression create() {
        if (instance == null) {
            instance = new NullExpression();
        }

        return instance;
	}

	@Override
	public Predicate getPreconditionForBeingFresh() {
		return Contradiction.create();
	}
}
