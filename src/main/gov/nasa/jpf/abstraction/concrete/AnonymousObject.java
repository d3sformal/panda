package gov.nasa.jpf.abstraction.concrete;

import java.util.ArrayList;
import java.util.List;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.Root;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultAccessExpression;
import gov.nasa.jpf.abstraction.common.Expression;
import gov.nasa.jpf.abstraction.common.PredicatesVisitor;
import gov.nasa.jpf.abstraction.predicate.common.Predicate;
import gov.nasa.jpf.abstraction.predicate.common.Tautology;

public class AnonymousObject extends DefaultAccessExpression implements Root, AnonymousExpression {
	
	private Reference reference;

	protected AnonymousObject(Reference reference) {
		this.reference = reference;
	}
	
	public Reference getReference() {
		return reference;
	}
	
	@Override
	public void accept(PredicatesVisitor visitor) {
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
	public Predicate preconditionForBeingFresh() {
		return Tautology.create();
	}

	@Override
	public List<AccessExpression> getSubAccessExpressions() {
		return new ArrayList<AccessExpression>();
	}

	@Override
	public AccessExpression replaceSubExpressions(AccessExpression expression, Expression newExpression) {
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
		return false;
	}

	@Override
	public String getName() {
		return "ref(" + reference + ")";
	}

}
