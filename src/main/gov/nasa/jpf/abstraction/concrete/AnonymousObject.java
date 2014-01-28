package gov.nasa.jpf.abstraction.concrete;

import java.util.Set;
import java.util.Map;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultAccessExpression;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesComponentVisitor;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.Tautology;

/**
 * Represents a freshly allocated object (freshly allocated = not loaded from a variable)
 *
 * A copy of the object reference (created by DUP instruction) may have been stored.
 */
public class AnonymousObject extends DefaultAccessExpression implements Root, AnonymousExpression {
	
	private Reference reference;

	protected AnonymousObject(Reference reference) {
		this.reference = reference;
	}
	
	@Override
	public Reference getReference() {
		return reference;
	}
	
	@Override
	public void accept(PredicatesComponentVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public AnonymousObject clone() {
		return create(reference);
	}
	
	public static AnonymousObject create(Reference reference) {
		if (reference == null) {
			return null;
		}
		
		return new AnonymousObject(reference);
	}

	@Override
	public Expression update(AccessExpression expression, Expression newExpression) {
		return clone();
	}
	
	@Override
	public Predicate getPreconditionForBeingFresh() {
		return Tautology.create();
	}

	@Override
	public void addAccessSubExpressionsToSet(Set<AccessExpression> out) {
	}

	@Override
	public AccessExpression replaceSubExpressions(Map<AccessExpression, Expression> replacements) {
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

	@Override
	public int getLength() {
		return 1;
	}

	@Override
	public AccessExpression cutTail() {
		return clone();
	}

	@Override
	public AccessExpression reRoot(AccessExpression newPrefix) {
		return newPrefix;
	}

	@Override
	public boolean isSimilarTo(AccessExpression expression) {
		if (expression instanceof AnonymousObject) {
			AnonymousObject o = (AnonymousObject) expression;
			
			return getReference().equals(o.getReference());
		}
		
		return false;
	}

	@Override
	public String getName() {
		return "ref(" + reference + ")";
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof AnonymousObject) {
			AnonymousObject ao = (AnonymousObject) o;

			return getReference().getObjectRef() == ao.getReference().getObjectRef();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getReference().getObjectRef();
	}

}
