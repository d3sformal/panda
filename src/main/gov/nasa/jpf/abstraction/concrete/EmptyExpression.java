package gov.nasa.jpf.abstraction.concrete;

import java.util.ArrayList;
import java.util.List;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;

public class EmptyExpression extends Expression {
	
	protected EmptyExpression() {
	}

	@Override
	public List<AccessExpression> getAccessExpressions() {
		return new ArrayList<AccessExpression>();
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Expression replace(AccessExpression formerPath, Expression expression) {
		return this;
	}
	
	public static EmptyExpression create() {
		return new EmptyExpression();
	}
	
	@Override
	public EmptyExpression clone() {
		return create();
	}

}
