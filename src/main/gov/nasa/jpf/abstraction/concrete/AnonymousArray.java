package gov.nasa.jpf.abstraction.concrete;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;

public class AnonymousArray extends AnonymousObject {
	
	private Expression length;
	
	protected AnonymousArray(Reference reference, Expression length) {
		super(reference);
		
		this.length = length;
	}
	
	public Expression getArrayLength() {
		return length;
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public AnonymousArray clone() {
		return create(getReference(), length);
	}
	
	public static AnonymousArray create(Reference reference, Expression length) {
		if (reference == null) {
			return null;
		}

		return new AnonymousArray(reference, length);
	}

}
