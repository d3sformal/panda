package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A predicate with a constant truth value ~ false
 */
public class Contradiction extends Predicate {
	
	protected Contradiction() {
		this.hashCodeValue = toString(Notation.DOT_NOTATION).hashCode();
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public List<AccessExpression> getPaths() {
		return new ArrayList<AccessExpression>();
	}

	@Override
	public Predicate replace(Map<AccessExpression, Expression> replacements) {
		return this;
	}
	
	public static Predicate create() {
		return new Contradiction();
	}
	
	@Override
	public Predicate update(AccessExpression expression, Expression newExpression) {
		return create();
	}

}
