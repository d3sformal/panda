package gov.nasa.jpf.abstraction.common.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gov.nasa.jpf.abstraction.common.Constant;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Notation;
import gov.nasa.jpf.abstraction.common.ObjectExpression;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.Contradiction;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.state.symbols.Universe;

public class NullExpression extends Constant implements ObjectExpression {
	
	protected NullExpression() {
		super(Universe.NULL);
	}

	@Override
	public List<AccessExpression> getAccessExpressions() {
		return new ArrayList<AccessExpression>();
	}

	@Override
	public NullExpression replace(Map<AccessExpression, Expression> replacements) {
		return clone();
	}

	@Override
	public NullExpression update(AccessExpression expression, Expression newExpression) {
		return clone();
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public NullExpression clone() {
		return create();
	}
	
	public static NullExpression create() {
		return new NullExpression();
	}

	@Override
	public Predicate getPreconditionForBeingFresh() {
		return Contradiction.create();
	}
}
