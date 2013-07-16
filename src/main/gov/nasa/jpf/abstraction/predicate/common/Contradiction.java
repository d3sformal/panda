package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;

import java.util.List;

public class Contradiction extends Predicate {

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public List<AccessPath> getPaths() {
		return new ArrayList<AccessPath>();
	}

	@Override
	public Predicate replace(AccessPath formerPath, Expression expression) {
		return this;
	}

}
