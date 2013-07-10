package gov.nasa.jpf.abstraction.predicate.common.impl;

import gov.nasa.jpf.abstraction.predicate.common.AccessPath;
import gov.nasa.jpf.abstraction.predicate.common.AccessPathSubElement;
import gov.nasa.jpf.abstraction.predicate.common.Expression;
import gov.nasa.jpf.abstraction.predicate.common.PredicatesVisitor;

public class DefaultAccessPathSubElement extends DefaultAccessPathMiddleElement implements AccessPathSubElement {
	private String name;
	
	public DefaultAccessPathSubElement(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

    @Override
    public boolean equals(Object o) {
        if (o instanceof DefaultAccessPathSubElement) {
            DefaultAccessPathSubElement sub = (DefaultAccessPathSubElement) o;

            return name.equals(sub.name);
        }

        return false;
    }
	
	@Override
	public DefaultAccessPathSubElement clone() {
		DefaultAccessPathSubElement clone = new DefaultAccessPathSubElement(name);
		
		if (getNext() != null) {
			clone.setNext(getNext().clone());
			clone.getNext().setPrevious(clone);
		}
		
		return clone;
	}
	

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public DefaultAccessPathSubElement replace(AccessPath formerPath, Expression expression) {
		DefaultAccessPathSubElement replaced = new DefaultAccessPathSubElement(name);
		
		if (getNext() != null) {
			replaced.setNext(getNext().replace(formerPath, expression));
			replaced.getNext().setPrevious(replaced);
		}
		
		return replaced;
	}

}
