package gov.nasa.jpf.abstraction.predicate.grammar.impl;

import gov.nasa.jpf.abstraction.predicate.grammar.AccessPath.NotationPolicy;
import gov.nasa.jpf.abstraction.predicate.grammar.AccessPathIndexElement;
import gov.nasa.jpf.abstraction.predicate.grammar.Expression;

public class DefaultAccessPathIndexElement extends DefaultAccessPathMiddleElement implements AccessPathIndexElement {
	private Expression index;
	
	public DefaultAccessPathIndexElement(Expression index) {
		this.index = index;
	}

	@Override
	public Expression getIndex() {
		return index;
	}

    @Override
    public boolean equals(Object o) {
        if (o instanceof DefaultAccessPathIndexElement) {
            DefaultAccessPathIndexElement i = (DefaultAccessPathIndexElement) o;

            return index.equals(i.index);
        }

        return false;
    }
	
	@Override
	public String toString(NotationPolicy policy) {
		switch (policy) {
		case DOT_NOTATION:
			String ret = "[" + index.toString(policy) + "]";
			
			if (getNext() != null) {
				ret += getNext().toString(policy);
			}
			
			return ret;
		case FUNCTION_NOTATION:
			String format = "%s";
			
			if (getNext() != null) {
				format = String.format(format, getNext().toString(policy));
			}

			return String.format(format, "aread(arr, %s, " + index.toString(policy) + ")");
		default:
			return null;
		}
	}
	
	@Override
	public DefaultAccessPathIndexElement clone() {
		DefaultAccessPathIndexElement clone = new DefaultAccessPathIndexElement(index);
		
		if (getNext() != null) {
			clone.setNext(getNext().clone());
			clone.getNext().setPrevious(clone);
		}
		
		return clone;
	}

}
