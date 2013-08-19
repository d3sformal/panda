package gov.nasa.jpf.abstraction.common.access.impl;

import java.util.List;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayElementWrite;
import gov.nasa.jpf.abstraction.common.access.meta.Arrays;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultArrays;

public class DefaultArrayElementWrite extends DefaultArrayElementExpression implements ArrayElementWrite {

	private Expression newValue;

	protected DefaultArrayElementWrite(AccessExpression array, Expression index, Expression newValue) {
		this(array, DefaultArrays.create(), index, newValue);
	}
	
	protected DefaultArrayElementWrite(AccessExpression array, Arrays arrays, Expression index, Expression newValue) {
		super(array, arrays, index);
		
		this.newValue = newValue;
	}
	
	@Override
	public Expression getNewValue() {
		return newValue;
	}
	
	public static DefaultArrayElementWrite create(AccessExpression array, Expression index, Expression newValue) {
		if (array == null || index == null || newValue == null) {
			return null;
		}
		
		return new DefaultArrayElementWrite(array, index, newValue);
	}
	
	public static DefaultArrayElementWrite create(AccessExpression array, Arrays arrays, Expression index, Expression newValue) {
		if (array == null || arrays == null || index == null || newValue == null) {
			return null;
		}
		
		return new DefaultArrayElementWrite(array, arrays, index, newValue);
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
	public DefaultArrayElementWrite clone() {
		return create(getArray().clone(), getArrays().clone(), getIndex().clone(), newValue.clone());
	}
	
	@Override
	public AccessExpression reRoot(AccessExpression newPrefix) {
		return create(newPrefix, getArrays().clone(), getIndex().clone(), getNewValue().clone());
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof ArrayElementWrite) {
			ArrayElementWrite w = (ArrayElementWrite) o;
			
			return getArrays().equals(w.getArrays()) && getArray().equals(w.getArray()) && getIndex().equals(w.getIndex()) && getNewValue().equals(w.getNewValue());
		}
		
		return false;
	}
	
	@Override
	public boolean isSimilarTo(AccessExpression expression) {
		if (expression instanceof ArrayElementWrite) {
			ArrayElementWrite w = (ArrayElementWrite) expression;
			
			return getArrays().equals(w.getArrays()) && getArray().isSimilarTo(w.getArray()) && getNewValue().equals(w.getNewValue());
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return ("write_element_" + getArray().hashCode() + "_" + getIndex().hashCode() + "_" + getNewValue().hashCode()).hashCode();
	}
	
	@Override
	public AccessExpression replaceSubExpressions(AccessExpression expression, Expression newExpression) {
		return create(getObject().replaceSubExpressions(expression, newExpression), getArrays().clone(), getIndex().replace(expression, newExpression), getNewValue().replace(expression, newExpression));
	}

}
