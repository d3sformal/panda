package gov.nasa.jpf.abstraction.common.access.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;
import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.Contradiction;
import gov.nasa.jpf.abstraction.common.Predicate;

/**
 * A common ancestor of all symbolic expressions that can stand alone (variables, package-class expression)
 * 
 * this contrasts with expressions such as object field read (@see gov.nasa.jpf.abstraction.common.access.ObjectFieldRead) that are not atomic enough in this sense
 */
public class DefaultRoot extends DefaultAccessExpression implements Root {

	private String name;
	
	protected DefaultRoot(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public final boolean isPrefixOf(AccessExpression expression) {
		return expression.getRoot().equals(this);
	}
	
	@Override
	public final boolean isSimilarToPrefixOf(AccessExpression expression) {
		return expression.getRoot().equals(this);
	}
	
	@Override
	public final AccessExpression cutTail() {
		return clone();
	}
	
	@Override
	public Root getRoot() {
		return this;
	}
	
	@Override
	public AccessExpression get(int depth) {
		if (depth == 1) {
			return this;
		}
		
		return null;
	}
	
	public static DefaultRoot create(String name) {
		if (name == null) {
			return null;
		}
		
		return new DefaultRoot(name);
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public List<AccessExpression> getSubAccessExpressions() {
		return new LinkedList<AccessExpression>();
	}

	@Override
	public DefaultRoot clone() {
		return create(getName());
	}

	@Override
	public int getLength() {
		return 1;
	}
	
	@Override
	public AccessExpression reRoot(AccessExpression newPrefix) {
		return newPrefix;
	}

	@Override
	public Expression update(AccessExpression expression, Expression newExpression) {
		if (equals(expression)) {
			return newExpression;
		}
		
		return clone();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Root) {
			Root r = (Root) o;
			
			return getName().equals(r.getName());
		}
		
		return false;
	}
	
	@Override
	public boolean isSimilarTo(AccessExpression expression) {
		return equals(expression);
	}
	
	@Override
	public int hashCode() {
		return ("root_" + getName()).hashCode();
	}

	@Override
	public AccessExpression replaceSubExpressions(Map<AccessExpression, Expression> replacements) {
		return clone();
	}

	@Override
	public Predicate preconditionForBeingFresh() {
		return Contradiction.create();
	}

}
