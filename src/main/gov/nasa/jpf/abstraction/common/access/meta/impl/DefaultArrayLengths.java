package gov.nasa.jpf.abstraction.common.access.meta.impl;

import java.util.LinkedList;
import java.util.List;

import gov.nasa.jpf.abstraction.common.PredicatesVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.meta.ArrayLengths;

/**
 * The unmodified symbol "arrlen"
 */
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

	@Override
	public List<AccessExpression> getAccessSubExpressions() {
		return new LinkedList<AccessExpression>();
	}
}
