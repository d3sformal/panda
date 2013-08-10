package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.Negation;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;

public class Implication extends Disjunction {
	
	public Predicate a;
	public Predicate b;

	protected Implication(Predicate a, Predicate b) {
		super(Negation.create(a), b);
		
		this.a = a;
		this.b = b;
	}
	
	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}
	
	public static Predicate create(Predicate a, Predicate b) {
		if (!argumentsDefined(a, b)) return null;
		
		if (a instanceof Tautology) {
			return b;
		}
		if (a instanceof Contradiction) {
			return Tautology.create();
		}
		
		return new Implication(a, b);
	}
	
	@Override
	public Predicate update(AccessExpression expression, Expression newExpression) {
		return create(a.update(expression, newExpression), b.update(expression, newExpression));
	}

}
