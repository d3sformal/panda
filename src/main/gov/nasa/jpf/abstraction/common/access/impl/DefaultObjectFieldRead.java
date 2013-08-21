package gov.nasa.jpf.abstraction.common.access.impl;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldWrite;
import gov.nasa.jpf.abstraction.common.access.meta.Field;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultField;
import gov.nasa.jpf.abstraction.concrete.AnonymousExpression;
import gov.nasa.jpf.abstraction.predicate.common.Contradiction;
import gov.nasa.jpf.abstraction.predicate.common.Predicate;

public class DefaultObjectFieldRead extends DefaultObjectFieldExpression implements ObjectFieldRead {

	protected DefaultObjectFieldRead(AccessExpression object, String name) {
		this(object, DefaultField.create(name));
	}
	
	protected DefaultObjectFieldRead(AccessExpression object, Field field) {
		super(object, field);
	}
	
	public static DefaultObjectFieldRead create(AccessExpression object, String name) {
		if (object == null || name == null) {
			return null;
		}
		
		return new DefaultObjectFieldRead(object, name);
	}
	
	public static DefaultObjectFieldRead create(AccessExpression object, Field field) {
		if (object == null || field == null) {
			return null;
		}
		
		return new DefaultObjectFieldRead(object, field);
	}
	
	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public DefaultObjectFieldRead clone() {
		return create(getObject().clone(), getField());
	}

	@Override
	public AccessExpression reRoot(AccessExpression newPrefix) {
		return create(newPrefix, getField().clone());
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof ObjectFieldRead) {
			ObjectFieldRead r = (ObjectFieldRead) o;
			
			return getObject().equals(r.getObject()) && getField().equals(r.getField());
		}
		
		return false;
	}
	
	@Override
	public boolean isSimilarTo(AccessExpression expression) {
		if (expression instanceof ObjectFieldRead) {
			ObjectFieldRead r = (ObjectFieldRead) expression;
			
			return getField().getName().equals(r.getField().getName()) && getObject().isSimilarTo(r.getObject());
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return ("read_field_" + getObject().hashCode() + "_" + getField().getName().hashCode()).hashCode();
	}
	
	@Override
	public AccessExpression replaceSubExpressions(AccessExpression expression, Expression newExpression) {
		return create(getObject().replaceSubExpressions(expression, newExpression), getField().clone());
	}
	
	@Override
	public Expression update(AccessExpression expression, Expression newExpression) {
		Expression updated = getObject().update(expression, newExpression);
		
		if (updated instanceof AnonymousExpression) {
			// Enclosing object replaced by a new object
			updated = DefaultFresh.create();
		}
		
		if (expression instanceof ObjectFieldRead) {
			ObjectFieldRead r = (ObjectFieldRead) expression;
			
			if (getField().getName().equals(r.getField().getName())) {				
				if (updated instanceof AccessExpression) {
					AccessExpression updatedAccessExpression = (AccessExpression) updated;
					
					return create(updatedAccessExpression, DefaultObjectFieldWrite.create(r.getObject(), r.getField().clone(), newExpression));
				}
				
				return UndefinedAccessExpression.create();
			}
		}
		
		return create((AccessExpression) updated, getField().clone());
	}

	@Override
	public Predicate preconditionForBeingFresh() {
		if (getField() instanceof ObjectFieldWrite) {
			ObjectFieldWrite w = (ObjectFieldWrite) getField();
			
			return w.preconditionForBeingFresh();
		}
		
		return Contradiction.create();
	}

}