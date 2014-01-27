package gov.nasa.jpf.abstraction.common.access.meta.impl;

import java.util.LinkedList;
import java.util.List;

import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.meta.ArrayLengths;

/**
 * The unmodified symbol "arrlen"
 */
public class DefaultArrayLengths implements ArrayLengths {
    private static DefaultArrayLengths instance;

	public static DefaultArrayLengths create() {
		//return new DefaultArrayLengths();
        if (instance == null) {
            instance = new DefaultArrayLengths();
        }

        return instance;
	}

	@Override
	public void accept(PredicatesComponentVisitor visitor) {
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
		//return create();
        return this;
	}

	@Override
	public List<AccessExpression> getAccessSubExpressions() {
		return new LinkedList<AccessExpression>();
	}
}
