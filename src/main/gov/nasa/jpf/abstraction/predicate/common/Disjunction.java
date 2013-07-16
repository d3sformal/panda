package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;

import java.util.ArrayList;
import java.util.List;

public class Disjunction extends Predicate {
	public Predicate a;
	public Predicate b;
	
	public Disjunction(Predicate a, Predicate b) {
		this.a = a;
		this.b = b;
	}

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
		return new Conjunction(a.replace(formerPath, expression), b.replace(formerPath, expression));
	}

}
