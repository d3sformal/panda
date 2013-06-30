package gov.nasa.jpf.abstraction.predicate.common;

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
	public String toString() {
		switch (AccessPath.policy) {
		case DOT_NOTATION:
			String ret = "." + name.toString();
			
			if (getNext() != null) {
				ret += getNext().toString();
			}
			
			return ret;
		case FUNCTION_NOTATION:
			String format = "%s";
			
			if (getNext() != null) {
				format = String.format(format, getNext().toString());
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
	public Object clone() {
		DefaultAccessPathSubElement clone = new DefaultAccessPathSubElement(name);
		
		if (getNext() != null) {
			clone.setNext((AccessPathMiddleElement) getNext().clone());
		}
		
		return clone;
	}

}
