package gov.nasa.jpf.abstraction.predicate.concrete;

import java.util.ArrayList;
import java.util.List;

import gov.nasa.jpf.abstraction.predicate.common.AccessPath;
import gov.nasa.jpf.abstraction.predicate.common.Expression;

public class EmptyExpression extends Expression {

	@Override
	public List<AccessPath> getPaths() {
		return new ArrayList<AccessPath>();
	}
	
	@Override
	public String toString(AccessPath.NotationPolicy policy) {
		return " ? ";
	}

}
