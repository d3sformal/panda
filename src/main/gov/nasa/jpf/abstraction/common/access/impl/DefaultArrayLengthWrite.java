package gov.nasa.jpf.abstraction.common.access.impl;

import java.util.List;
import java.util.Map;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayLengthWrite;
import gov.nasa.jpf.abstraction.common.access.meta.ArrayLengths;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultArrayLengths;
import gov.nasa.jpf.abstraction.common.Predicate;

/**
 * Write to an array length: alengthupdate(arrlen, a, l) ~ a := new int[l]
 */
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
	public List<AccessExpression> getAccessSubExpressions() {
		List<AccessExpression> ret = super.getAccessSubExpressions();
		
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
	public boolean isSimilarTo(AccessExpression expression) {
		if (expression instanceof ArrayLengthWrite) {
			ArrayLengthWrite w = (ArrayLengthWrite) expression;
			
			return getArray().isSimilarTo(w.getArray());
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return ("write_length_" + getObject().hashCode() + "_" + getNewValue().hashCode()).hashCode();
	}
	
	@Override
	public AccessExpression replaceSubExpressions(Map<AccessExpression, Expression> replacements) {
		return create(getObject().replaceSubExpressions(replacements), getArrayLengths().clone(), getNewValue().replace(replacements));
	}
	
	@Override
	public Predicate getPreconditionForBeingFresh() {
		return getNewValue().getPreconditionForBeingFresh();
	}

}
