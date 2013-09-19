package gov.nasa.jpf.abstraction.common.access.impl;

import java.util.Map;

import gov.nasa.jpf.abstraction.common.ArrayExpression;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayLengthRead;
import gov.nasa.jpf.abstraction.common.access.ArrayLengthWrite;
import gov.nasa.jpf.abstraction.common.access.meta.ArrayLengths;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultArrayLengths;
import gov.nasa.jpf.abstraction.concrete.AnonymousArray;
import gov.nasa.jpf.abstraction.common.Conjunction;
import gov.nasa.jpf.abstraction.common.Contradiction;
import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.Predicate;

/**
 * Read of an array length: alength(arrlen, a) ~ a.length
 */
public class DefaultArrayLengthRead extends DefaultArrayLengthExpression implements ArrayLengthRead {

	protected DefaultArrayLengthRead(AccessExpression array) {
		this(array, DefaultArrayLengths.create());
	}
	
	protected DefaultArrayLengthRead(AccessExpression array, ArrayLengths arrayLengths) {
		super(array, arrayLengths);
	}
	
	public static DefaultArrayLengthRead create(AccessExpression array) {
		if (array == null) {
			return null;
		}
		
		return new DefaultArrayLengthRead(array);
	}
	
	public static DefaultArrayLengthRead create(AccessExpression array, ArrayLengths arrayLengths) {
		if (array == null || arrayLengths == null) {
			return null;
		}
		
		return new DefaultArrayLengthRead(array, arrayLengths);
	}
	
	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public DefaultArrayLengthRead clone() {
		return create(getArray().clone(), getArrayLengths().clone());
	}

	@Override
	public AccessExpression reRoot(AccessExpression newPrefix) {
		return create(newPrefix, getArrayLengths().clone());
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof ArrayLengthRead) {
			ArrayLengthRead r = (ArrayLengthRead) o;
			
			return getArrayLengths().equals(r.getArrayLengths()) && getArray().equals(r.getArray());
		}
		
		return false;
	}
	
	@Override
	public boolean isSimilarTo(AccessExpression expression) {
		if (expression instanceof ArrayLengthRead) {
			ArrayLengthRead r = (ArrayLengthRead) expression;
			
			return getArray().isSimilarTo(r.getArray());
		}
		
		return false;
	}
	
	@Override
	public boolean isSimilarToPrefixOf(AccessExpression path) {
		if (path instanceof ArrayLengthRead) {
			ArrayLengthRead r = (ArrayLengthRead) path;
			
			return getObject().isSimilarToPrefixOf(r.getObject()) && getArray().isSimilarToPrefixOf(r.getArray());
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return ("read_length_" + getObject().hashCode()).hashCode();
	}
	
	@Override
	public AccessExpression replaceSubExpressions(Map<AccessExpression, Expression> replacements) {
		return create(getObject().replaceSubExpressions(replacements), getArrayLengths().clone());
	}
	
	@Override
	public Expression update(AccessExpression expression, Expression newExpression) {
		if (newExpression instanceof ArrayExpression) {			
			Expression updated = getObject().update(expression, newExpression);
				
			if (updated instanceof AccessExpression) {
				AccessExpression updatedAccessExpression = (AccessExpression) updated;
				
				if (newExpression instanceof AnonymousArray) {
					AnonymousArray aa = (AnonymousArray) newExpression;
					return create(updatedAccessExpression, DefaultArrayLengthWrite.create(updatedAccessExpression, getArrayLengths().clone(), aa.getArrayLength()));
				}
				return create(updatedAccessExpression, DefaultArrayLengthWrite.create(updatedAccessExpression, getArrayLengths().clone(), DefaultArrayLengthRead.create(expression, getArrayLengths().clone())));
			}
			if (updated instanceof AnonymousArray) {
				AnonymousArray updatedAnonymousArray = (AnonymousArray) updated;
				
				return create(getArray().clone(), DefaultArrayLengthWrite.create(expression, getArrayLengths().clone(), updatedAnonymousArray.getArrayLength().clone()));
			}
			return UndefinedAccessExpression.create();
		}
		
		return clone();
	}
	
	@Override
	public Predicate preconditionForBeingFresh() {
		if (getArrayLengths() instanceof ArrayLengthWrite) {
			ArrayLengthWrite w = (ArrayLengthWrite) getArrayLengths();
			
			return Conjunction.create(Equals.create(getArray(), w.getArray()), w.preconditionForBeingFresh());
		}
		
		return Contradiction.create();
	}
}
