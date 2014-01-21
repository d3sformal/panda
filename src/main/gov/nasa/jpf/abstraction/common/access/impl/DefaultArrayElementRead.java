package gov.nasa.jpf.abstraction.common.access.impl;

import java.util.Map;

import gov.nasa.jpf.abstraction.common.ArrayExpression;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayAccessExpression;
import gov.nasa.jpf.abstraction.common.access.ArrayElementRead;
import gov.nasa.jpf.abstraction.common.access.ArrayElementWrite;
import gov.nasa.jpf.abstraction.common.access.meta.Arrays;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultArrays;
import gov.nasa.jpf.abstraction.common.impl.NullExpression;
import gov.nasa.jpf.abstraction.common.Conjunction;
import gov.nasa.jpf.abstraction.common.Contradiction;
import gov.nasa.jpf.abstraction.common.Equals;
import gov.nasa.jpf.abstraction.common.Predicate;

/**
 * Read of an array element aread(arr, a, i) ~ a[i]
 */
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
	public void accept(PredicatesComponentVisitor visitor) {
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
	public boolean isSimilarTo(AccessExpression expression) {
		if (expression instanceof ArrayElementRead) {
			ArrayElementRead r = (ArrayElementRead) expression;
			
			return getArray().isSimilarTo(r.getArray());
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return ("read_element_" + getArray().hashCode() + "_" + getIndex().hashCode()).hashCode();
	}
	
	@Override
	public AccessExpression replaceSubExpressions(Map<AccessExpression, Expression> replacements) {
		return create(getObject().replaceSubExpressions(replacements), getArrays().clone(), getIndex().replace(replacements));
	}
	

    /**
     * @see gov.nasa.jpf.abstraction.common.Predicate.update for an overall view
     */
	@Override
	public Expression update(AccessExpression expression, Expression newExpression) {
		Expression updated = getArray().update(expression, newExpression);

		// An array has been changed -> aread(awrite(...), ..., ...)
		if (expression instanceof ArrayExpression) {
			ArrayElementRead a = (ArrayElementRead) expression;
				
			if (updated instanceof AccessExpression) {
				AccessExpression updatedAccessExpression = (AccessExpression) updated;
				
				return create(updatedAccessExpression, DefaultArrayElementWrite.create(a.getArray(), a.getArrays().clone(), a.getIndex().clone(), newExpression), getIndex().update(expression, newExpression));
			}
				
			return UndefinedAccessExpression.create();
		}

		// This element is not affected by the update (a := b) so update recursively
		if (updated instanceof AccessExpression) {
			return create((AccessExpression) updated, getArrays().clone(), getIndex().update(expression, newExpression));
		}

		// If the update causes that this expression accesses element of null
		if (updated instanceof NullExpression) {
			return NullExpression.create();
		}

		throw new RuntimeException("Unrecognized expression " + updated + "(" + updated.getClass().getName() + ")");
	}

	@Override
	public Predicate getPreconditionForBeingFresh() {
		if (getArrays() instanceof ArrayElementWrite) {
			ArrayElementWrite w = (ArrayElementWrite) getArrays();
			
			return Conjunction.create(Equals.create(getArray(), w.getArray()), w.getPreconditionForBeingFresh());
		}
		
		return Contradiction.create();
	}
}
