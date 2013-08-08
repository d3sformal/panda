package gov.nasa.jpf.abstraction.common.access.impl;

import java.util.List;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayLengthWrite;
import gov.nasa.jpf.abstraction.common.access.meta.ArrayLengths;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultArrayLengths;

public class DefaultArrayLengthWrite extends DefaultArrayLengthExpression implements ArrayLengthWrite {

	private Expression newValue;

	protected DefaultArrayLengthWrite(AccessExpression array, Expression newValue) {
		this(array, DefaultArrayLengths.create(), newValue);
	}
	
	protected DefaultArrayLengthWrite(AccessExpression array, ArrayLengths arrayLengths, Expression newValue) {
		super(array, arrayLengths);
		
		this.newValue = newValue;
	}
	
	@Override
	public Expression getNewValue() {
		return newValue;
	}
	
	public static DefaultArrayLengthWrite create(AccessExpression array, Expression newValue) {
		if (array == null || newValue == null) {
			return null;
		}
		
		return new DefaultArrayLengthWrite(array, newValue);
	}
	
	public static DefaultArrayLengthWrite create(AccessExpression array, ArrayLengths arrayLengths, Expression newValue) {
		if (array == null || arrayLengths == null || newValue == null) {
			return null;
		}
		
		return new DefaultArrayLengthWrite(array, arrayLengths, newValue);
	}
	
	@Override
	public List<AccessExpression> getSubAccessExpressions() {
		List<AccessExpression> ret = super.getSubAccessExpressions();
		
		ret.addAll(newValue.getAccessExpressions());
		
		return ret;
	}
	
	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public DefaultArrayLengthWrite clone() {
		return create(getArray().clone(), getArrayLengths().clone(), getNewValue().clone());
	}
	
	@Override
	public AccessExpression reRoot(AccessExpression newPrefix) {
		return create(newPrefix, getArrayLengths().clone(), getNewValue().clone());
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof ArrayLengthWrite) {
			ArrayLengthWrite w = (ArrayLengthWrite) o;
			
			return getArrayLengths().equals(w.getArrayLengths()) && getArray().equals(w.getArray()) && getNewValue().equals(w.getNewValue());
		}
		
		return false;
	}
	
	@Override
	public AccessExpression replaceSubExpressions(AccessExpression expression, Expression newExpression) {
		return create(getObject().replaceSubExpressions(expression, newExpression), getArrayLengths().clone(), getNewValue().replace(expression, newExpression));
	}

}
