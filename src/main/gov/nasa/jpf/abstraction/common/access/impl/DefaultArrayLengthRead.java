package gov.nasa.jpf.abstraction.common.access.impl;

import gov.nasa.jpf.abstraction.common.ArrayExpression;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayLengthRead;
import gov.nasa.jpf.abstraction.common.access.meta.ArrayLengths;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultArrayLengths;
import gov.nasa.jpf.abstraction.concrete.AnonymousArray;

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
	public int hashCode() {
		return ("read_length_" + getObject().hashCode()).hashCode();
	}
	
	@Override
	public AccessExpression replaceSubExpressions(AccessExpression expression, Expression newExpression) {
		return create(getObject().replaceSubExpressions(expression, newExpression), getArrayLengths().clone());
	}
	
	@Override
	public Expression update(AccessExpression expression, Expression newExpression) {
		if (newExpression instanceof ArrayExpression) {			
			Expression updated = getObject().update(expression, newExpression);
				
			if (updated instanceof AccessExpression) {
				AccessExpression updatedAccessExpression = (AccessExpression) updated;
				
				if (newExpression instanceof AnonymousArray) {
					AnonymousArray aa = (AnonymousArray) expression;
					return create(updatedAccessExpression, DefaultArrayLengthWrite.create(getArray().clone(), getArrayLengths().clone(), aa.length));
				}
				return create(updatedAccessExpression, DefaultArrayLengthWrite.create(getArray().clone(), getArrayLengths().clone(), DefaultArrayLengthRead.create(expression, getArrayLengths().clone())));
			}
			if (updated instanceof AnonymousArray) {
				AnonymousArray updatedAnonymousArray = (AnonymousArray) updated;
				
				return create(getArray().clone(), DefaultArrayLengthWrite.create(expression, getArrayLengths().clone(), updatedAnonymousArray.length.clone()));
			}
			return UndefinedAccessExpression.create();
		}
		
		return clone();
	}
}
