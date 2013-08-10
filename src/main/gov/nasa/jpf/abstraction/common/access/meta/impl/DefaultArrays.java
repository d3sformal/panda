package gov.nasa.jpf.abstraction.common.access.meta.impl;

import gov.nasa.jpf.abstraction.common.PredicatesVisitor;
import gov.nasa.jpf.abstraction.common.access.meta.Arrays;

public class DefaultArrays implements Arrays {
	public static DefaultArrays create() {
		return new DefaultArrays();
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof DefaultArrays) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public DefaultArrays clone() {
		return create();
	}
}
