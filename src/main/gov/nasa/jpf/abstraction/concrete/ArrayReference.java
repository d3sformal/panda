package gov.nasa.jpf.abstraction.concrete;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.NotationPolicy;
import gov.nasa.jpf.abstraction.common.impl.NullExpression;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteAccessExpression;
import gov.nasa.jpf.vm.ElementInfo;

public class ArrayReference extends ObjectReference {
	private Expression array;
	
	public ArrayReference(ElementInfo ei, Expression array) {
		super(ei);
		
		if (!(array instanceof ConcreteAccessExpression) && !(array instanceof AnonymousArray) && !(array instanceof NullExpression)) {
			throw new RuntimeException("Bad array reference '" + array + "'.");
		}
		
		this.array = array;
	}
	
	@Override
	public String toString() {	
		return super.toString() + " " + array.toString(NotationPolicy.DOT_NOTATION); 
	}
}
