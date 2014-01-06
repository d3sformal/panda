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

public class NullExpression extends Constant implements ObjectExpression {
	
	protected NullExpression() {
		super(-1);
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
	public String toString(Notation policy) {
		return Notation.convertToString(this);
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
