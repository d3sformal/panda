package gov.nasa.jpf.abstraction.common.access.impl;

import gov.nasa.jpf.abstraction.common.PredicatesVisitor;
import gov.nasa.jpf.abstraction.common.access.Fresh;
import gov.nasa.jpf.abstraction.common.Predicate;
import gov.nasa.jpf.abstraction.common.Tautology;

/**
 * A special value representing a completely new object
 */
public class DefaultFresh extends DefaultRoot implements Fresh {
	protected DefaultFresh() {
		super("fresh");
	}
	
	public static DefaultFresh create() {
		return new DefaultFresh();
	}
	
	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public DefaultFresh clone() {
		return create();
	}
	
	@Override
	public boolean equals(Object o) {
		return false;
	}
	
	@Override
	public Predicate preconditionForBeingFresh() {
		return Tautology.create();
	}
}
