package gov.nasa.jpf.abstraction.concrete;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.vm.ElementInfo;

public class ArrayReference extends Reference {
	private Expression array;
	
	public ArrayReference(ElementInfo ei, Expression array) {
		super(ei);
		
		if (!(array instanceof ConcretePath) && !(array instanceof AnonymousArray)) {
			throw new RuntimeException("Bad array reference '" + array + "'.");
		}
		
		this.array = array;
	}
	
	@Override
	public String toString() {	
		return super.toString() + " " + array.toString(AccessPath.NotationPolicy.DOT_NOTATION); 
	}
}
