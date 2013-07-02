package gov.nasa.jpf.abstraction.predicate.grammar.impl;

import gov.nasa.jpf.abstraction.predicate.grammar.AccessPath;
import gov.nasa.jpf.abstraction.predicate.grammar.AccessPathMiddleElement;
import gov.nasa.jpf.abstraction.predicate.grammar.AccessPathRootElement;

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
	public String toString() {
		switch (AccessPath.policy) {
		case DOT_NOTATION:
			String ret = name;
		
			if (getNext() != null) {
				ret += getNext().toString();
			}
		
			return ret;
		case FUNCTION_NOTATION:
			String format = "%s";
			
			if (getNext() != null) {
				format = String.format(format, getNext().toString());
			}

			return String.format(format, name);
		default:
			return null;
		}
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
	public Object clone() {
		DefaultAccessPathRootElement clone = new DefaultAccessPathRootElement(name);
		
		if (getNext() != null) {
			clone.setNext((AccessPathMiddleElement) getNext().clone());
		}
		
		return clone;
	}

}
