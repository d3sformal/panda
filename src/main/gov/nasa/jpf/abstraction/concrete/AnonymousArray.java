package gov.nasa.jpf.abstraction.concrete;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.impl.DefaultArrayExpression;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;
import gov.nasa.jpf.abstraction.predicate.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.common.Tautology;
import gov.nasa.jpf.vm.ElementInfo;

import java.util.List;

public class AnonymousArray extends DefaultArrayExpression implements AnonymousExpression {
	
	public ElementInfo ei;
	public Expression length;
	
	protected AnonymousArray(ElementInfo ei, Expression length) {
		this.ei = ei;
		this.length = length;
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public List<AccessExpression> getAccessExpressions() {
		return length.getAccessExpressions();
	}

	@Override
	public Expression replace(AccessExpression formerPath, Expression expression) {
		return this;
	}

	@Override
	public AnonymousArray clone() {
		return create(ei, length);
	}
	
	public static AnonymousArray create(ElementInfo ei, Expression length) {
		if (length == null) {
			return null;
		}

		return new AnonymousArray(ei, length);
	}

	@Override
	public Expression update(AccessExpression expression, Expression newExpression) {
		return clone();
	}

	@Override
	public Predicate preconditionForBeingFresh() {
		return Tautology.create();
	}

}
