package gov.nasa.jpf.abstraction.common.access.impl;

import java.util.Map;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldRead;
import gov.nasa.jpf.abstraction.common.access.ObjectFieldWrite;
import gov.nasa.jpf.abstraction.common.access.meta.Field;
import gov.nasa.jpf.abstraction.common.access.meta.impl.DefaultField;
import gov.nasa.jpf.abstraction.common.impl.NullExpression;
import gov.nasa.jpf.abstraction.common.Contradiction;
import gov.nasa.jpf.abstraction.common.Predicate;

/**
 * Expressions fread(f, o) ~ o.f
 */
public class DefaultObjectFieldRead extends DefaultObjectFieldExpression implements ObjectFieldRead {

    private Integer hashCodeValue;

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
	public void accept(PredicatesComponentVisitor visitor) {
		visitor.visit(this);
	}

    @Override
    public DefaultObjectFieldRead createShallowCopy() {
        return create(getObject(), getField());
    }

	@Override
	public AccessExpression reRoot(AccessExpression newPrefix) {
		return create(newPrefix, getField());
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
        if (hashCodeValue == null) {
    		hashCodeValue = ("read_field_" + getObject().hashCode() + "_" + getField().getName().hashCode()).hashCode();
        }

        return hashCodeValue;
	}
	
	@Override
	public AccessExpression replaceSubExpressions(Map<AccessExpression, Expression> replacements) {
		AccessExpression newO = getObject().replaceSubExpressions(replacements);

		if (newO == getObject()) return this;
		else return create(newO, getField());
	}
	
    /**
     * @see gov.nasa.jpf.abstraction.common.Predicate.update for an overall view
     */
	@Override
	public Expression update(AccessExpression expression, Expression newExpression) {
        // perform updates in the prefix
        //   statement: a.f = b
        //   access expression: c[a.f].f
        //   expected result: fread(f, aread(c, b))
		Expression updated = getObject().update(expression, newExpression);

        // this is some path `... .f`
		if (expression instanceof ObjectFieldRead) {
			ObjectFieldRead r = (ObjectFieldRead) expression;
			
            // some other `... .f` has been updated
			if (getField().getName().equals(r.getField().getName())) {				
				if (updated instanceof AccessExpression) {
					AccessExpression updatedAccessExpression = (AccessExpression) updated;
		
					return create(updatedAccessExpression, DefaultObjectFieldWrite.create(r.getObject(), r.getField(), newExpression));
				}
				
                // propagate failure
				return UndefinedAccessExpression.create();
			}
		}

        // Something else (not a field `f`) has changed
		
        // updated prefix is a path and can be extended
        // a.b.c.d.e ... return a.b.c.d.e.f
		if (updated instanceof AccessExpression) {
			return create((AccessExpression) updated, getField());
		}
		
        // updated prefix is null
        // null.f ... return null
		if (updated instanceof NullExpression) {
			return NullExpression.create();
		}
		
		throw new RuntimeException("Unrecognized expression " + updated + "(" + updated.getClass().getName() + ")");
	}

	@Override
	public Predicate getPreconditionForBeingFresh() {
		if (getField() instanceof ObjectFieldWrite) {
			ObjectFieldWrite w = (ObjectFieldWrite) getField();
			
			return w.getPreconditionForBeingFresh();
		}
		
		return Contradiction.create();
	}

}
