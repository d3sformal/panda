package gov.nasa.jpf.abstraction.concrete;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;
import gov.nasa.jpf.vm.ElementInfo;

import java.util.List;

public class AnonymousArray extends AnonymousExpression {
	
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
	public List<AccessPath> getPaths() {
		return length.getPaths();
	}

	@Override
	public Expression replace(AccessPath formerPath, Expression expression) {
		return this;
	}

	@Override
	public Expression clone() {
		return create(ei, length);
	}
	
	public static AnonymousArray create(ElementInfo ei, Expression length) {
		if (length == null) {
			return null;
		}

		return new AnonymousArray(ei, length);
	}
	
	@Override
	public PartialVariableID generateVariableID() {
		return new PartialVariableID(new ArrayReference(ei, this));
	}

}
