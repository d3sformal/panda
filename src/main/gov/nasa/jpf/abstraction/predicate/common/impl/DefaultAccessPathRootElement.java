package gov.nasa.jpf.abstraction.predicate.common.impl;

import gov.nasa.jpf.abstraction.predicate.common.AccessPath;
import gov.nasa.jpf.abstraction.predicate.common.AccessPathRootElement;
import gov.nasa.jpf.abstraction.predicate.common.Expression;
import gov.nasa.jpf.abstraction.predicate.common.PredicatesVisitor;

public class DefaultAccessPathRootElement extends DefaultAccessPathElement implements AccessPathRootElement {
	private String name;
	
	public DefaultAccessPathRootElement(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}

    @Override
    public boolean equals(Object o) {
        if (o instanceof DefaultAccessPathRootElement) {
            DefaultAccessPathRootElement root = (DefaultAccessPathRootElement) o;

            return name.equals(root.name);
        }

        return false;
    }
	
	@Override
	public DefaultAccessPathRootElement clone() {
		DefaultAccessPathRootElement clone = new DefaultAccessPathRootElement(name);
		
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
	public DefaultAccessPathRootElement replace(AccessPath formerPath, Expression expression) {
		DefaultAccessPathRootElement replaced = new DefaultAccessPathRootElement(name);
		
		if (getNext() != null) {
			replaced.setNext(getNext().replace(formerPath, expression));
			replaced.getNext().setPrevious(replaced);
		}
		
		return replaced;
	}

}
