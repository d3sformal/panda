package gov.nasa.jpf.abstraction.common.access.meta.impl;

import java.util.LinkedList;
import java.util.List;

import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.meta.Arrays;

/**
 * The unmodified symbol "arr"
 */
public class DefaultArrays implements Arrays {
	public static DefaultArrays create() {
		return new DefaultArrays();
	}

	@Override
	public void accept(PredicatesComponentVisitor visitor) {
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
	
	@Override
	public List<AccessExpression> getAccessSubExpressions() {
		return new LinkedList<AccessExpression>();
	}
}
