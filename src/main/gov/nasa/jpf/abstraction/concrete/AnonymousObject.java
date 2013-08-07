package gov.nasa.jpf.abstraction.concrete;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.ObjectExpression;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;
import gov.nasa.jpf.vm.ElementInfo;

import java.util.ArrayList;
import java.util.List;

public class AnonymousObject extends ObjectExpression implements AnonymousExpression {
	
	public ElementInfo ei;
	
	protected AnonymousObject(ElementInfo ei) {
		this.ei = ei;
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public List<AccessExpression> getAccessExpressions() {
		return new ArrayList<AccessExpression>();
	}

	@Override
	public Expression replace(AccessExpression formerPath, Expression expression) {
		return this;
	}

	@Override
	public Expression clone() {
		return create(ei);
	}
	
	public static AnonymousObject create(ElementInfo ei) {
		return new AnonymousObject(ei);
	}
	
	
	@Override
	public PartialVariableID generateVariableID() {
		return new PartialVariableID(new ObjectReference(ei));
	}

}
