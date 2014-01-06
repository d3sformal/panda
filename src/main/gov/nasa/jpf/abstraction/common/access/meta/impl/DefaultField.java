package gov.nasa.jpf.abstraction.common.access.meta.impl;

import java.util.LinkedList;
import java.util.List;

import gov.nasa.jpf.abstraction.common.PredicatesVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.meta.Field;

/**
 * An unmodified field
 */
public class DefaultField implements Field {
	public String name;
	
	protected DefaultField(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public static DefaultField create(String name) {
		if (name == null) {
			return null;
		}
		
		return new DefaultField(name);
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public DefaultField clone() {
		return create(getName());
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof DefaultField) {
			DefaultField f = (DefaultField) o;
			
			return getName().equals(f.getName());
		}
		
		return false;
	}
	
	@Override
	public List<AccessExpression> getAccessSubExpressions() {
		return new LinkedList<AccessExpression>();
	}
}
