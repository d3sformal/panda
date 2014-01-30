package gov.nasa.jpf.abstraction.common;

import java.util.Map;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.Undefined;
import gov.nasa.jpf.abstraction.concrete.AnonymousExpression;

/**
 * Predicate on equality of two symbolic expressions 
 */
public class Equals extends Comparison {
	protected Equals(Expression a, Expression b) {
		super(a, b);
	}

	@Override
	public void accept(PredicatesComponentVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Predicate replace(Map<AccessExpression, Expression> replacements) {
		Expression newA = a.replace(replacements);
		Expression newB = b.replace(replacements);

		if (newA == a && newB == b) return this; 
		else return create(newA, newB);  
	}
	
	public static Predicate create(Expression a, Expression b) {
		if (!argumentsDefined(a, b)) return null;
		
		if (a instanceof Undefined) return Contradiction.create();
		if (b instanceof Undefined) return Contradiction.create();
		
		return new Equals(a, b);
	}
	
	@Override
	public Predicate update(AccessExpression expression, Expression newExpression) {
        /**
         * a := new Object
         * a = b ... false
         */
		if (a instanceof AccessExpression) {
			AccessExpression ae = (AccessExpression) a;
			
			if (expression.equals(ae) && newExpression instanceof AnonymousExpression && !a.equals(b)) {
				return Contradiction.create();
			}
		}
		
        /**
         * a := new Object
         * b = a ... false
         */
		if (b instanceof AccessExpression) {
			AccessExpression be = (AccessExpression) b;
			
			if (expression.equals(be) && newExpression instanceof AnonymousExpression && !a.equals(b)) {
				return Contradiction.create();
			}
		}

		Expression newA = a.update(expression, newExpression);
		Expression newB = b.update(expression, newExpression);

		if (newA == a && newB == b) return this;
		else return create(newA, newB); 
	}
}
