package gov.nasa.jpf.abstraction.predicate.common;

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
	public String toString() {
		switch (AccessPath.policy) {
		case DOT_NOTATION:
			String ret = "[" + index.toString() + "]";
			
			if (getNext() != null) {
				ret += getNext().toString();
			}
			
			return ret;
		case FUNCTION_NOTATION:
			String format = "%s";
			
			if (getNext() != null) {
				format = String.format(format, getNext().toString());
			}

			return String.format(format, "aread(arr, %s, " + index.toString() + ")");
		default:
			return null;
		}
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
	public Object clone() {
		DefaultAccessPathIndexElement clone = new DefaultAccessPathIndexElement(index);
		
		if (getNext() != null) {
			clone.setNext((AccessPathMiddleElement) getNext().clone());
		}
		
		return clone;
	}

}
