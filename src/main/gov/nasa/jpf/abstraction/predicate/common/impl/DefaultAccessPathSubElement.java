package gov.nasa.jpf.abstraction.predicate.common.impl;

import gov.nasa.jpf.abstraction.predicate.common.AccessPath;
import gov.nasa.jpf.abstraction.predicate.common.AccessPathSubElement;

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
	public String toString(AccessPath.NotationPolicy policy) {
		switch (policy) {
		case DOT_NOTATION:
			String ret = "." + name.toString();
			
			if (getNext() != null) {
				ret += getNext().toString(policy);
			}
			
			return ret;
		case FUNCTION_NOTATION:
			String format = "%s";
			
			if (getNext() != null) {
				format = String.format(format, getNext().toString(policy));
			}

			return String.format(format, "fread(" + name + ", %s)");
		default:
			return null;
		}
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

}
