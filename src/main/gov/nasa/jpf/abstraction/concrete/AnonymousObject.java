package gov.nasa.jpf.abstraction.concrete;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;
import gov.nasa.jpf.vm.ElementInfo;

import java.util.ArrayList;
import java.util.List;

public class AnonymousObject extends AnonymousExpression {
	
	public ElementInfo ei;
	
	protected AnonymousObject(ElementInfo ei) {
		this.ei = ei;
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public List<AccessPath> getPaths() {
		return new ArrayList<AccessPath>();
	}

	@Override
	public Expression replace(AccessPath formerPath, Expression expression) {
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
