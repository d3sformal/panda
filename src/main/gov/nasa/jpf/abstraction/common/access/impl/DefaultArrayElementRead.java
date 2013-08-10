package gov.nasa.jpf.abstraction.common.access.impl;

import gov.nasa.jpf.abstraction.common.ArrayExpression;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayAccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.meta.Arrays;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultArrays;

public class DefaultArrayElementRead extends DefaultArrayElementExpression implements ArrayElementRead {

	protected DefaultArrayElementRead(AccessExpression array, Expression index) {
		this(array, DefaultArrays.create(), index);
	}
	
	protected DefaultArrayElementRead(AccessExpression array, Arrays arrays, Expression index) {
		super(array, arrays, index);
	}
	
	public static DefaultArrayElementRead create(AccessExpression array, Expression index) {
		if (array == null || index == null) {
			return null;
		}
		
		return new DefaultArrayElementRead(array, index);
	}
	
	public static DefaultArrayElementRead create(AccessExpression array, Arrays arrays, Expression index) {
		if (array == null || arrays == null || index == null) {
			return null;
		}
		
		return new DefaultArrayElementRead(array, arrays, index);
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public DefaultArrayElementRead clone() {
		return create(getArray().clone(), getArrays().clone(), getIndex().clone());
	}
	
	@Override
	public AccessExpression reRoot(AccessExpression newPrefix) {
		return create(newPrefix, getArrays().clone(), getIndex().clone());
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof ArrayElementRead) {
			ArrayElementRead r = (ArrayElementRead) o;
			
			return getArrays().equals(r.getArrays()) && getArray().equals(r.getArray()) && getIndex().equals(r.getIndex());
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return ("read_element_" + getArray().hashCode() + "_" + getIndex().hashCode()).hashCode();
	}
	
	@Override
	public AccessExpression replaceSubExpressions(AccessExpression expression, Expression newExpression) {
		return create(getObject().replaceSubExpressions(expression, newExpression), getArrays().clone(), getIndex().replace(expression, newExpression));
	}
	
	@Override
	public Expression update(AccessExpression expression, Expression newExpression) {
		if (newExpression instanceof ArrayExpression) {
			ArrayAccessExpression a = (ArrayAccessExpression) expression;
			
			Expression updated = getObject().update(expression, newExpression);
				
			if (updated instanceof AccessExpression) {
				AccessExpression updatedAccessExpression = (AccessExpression) updated;
				
				return create(updatedAccessExpression, DefaultArrayElementWrite.create(a, getArrays().clone(), getIndex().clone(), newExpression), getIndex().clone());
			}
				
			return UndefinedAccessExpression.create();
		}
		
		return clone();
	}
}
