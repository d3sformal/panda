package gov.nasa.jpf.abstraction.common.access.meta.impl;

import gov.nasa.jpf.abstraction.common.PredicatesVisitor;
import gov.nasa.jpf.abstraction.common.access.meta.ArrayLengths;

public class DefaultArrayLengths implements ArrayLengths {
	public static DefaultArrayLengths create() {
		return new DefaultArrayLengths();
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof DefaultArrayLengths) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public DefaultArrayLengths clone() {
		return create();
	}
}
