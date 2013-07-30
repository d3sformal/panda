package gov.nasa.jpf.abstraction.concrete;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.vm.ElementInfo;

public class ArrayReference extends Reference {
	private Expression length;
	
	public ArrayReference(ElementInfo ei, Expression length) {
		super(ei);
		
		if (length != null) {
			this.length = length;
		} else {
			this.length = EmptyExpression.create();
		}
	}
	
	@Override
	public String toString() {	
		return super.toString() + " " + length.toString(AccessPath.NotationPolicy.DOT_NOTATION); 
	}
}
