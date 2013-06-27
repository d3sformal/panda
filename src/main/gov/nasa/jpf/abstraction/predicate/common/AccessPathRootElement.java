package gov.nasa.jpf.abstraction.predicate.common;

public class AccessPathRootElement extends AccessPathElement {
	public String name;
	
	public AccessPathRootElement(String name) {
		super(null);

		this.name = name;
	}
	
	@Override
	public String toString() {
		switch (AccessPath.policy) {
		case DOT_NOTATION:
			String ret = name;
		
			if (next != null) {
				ret += next.toString();
			}
		
			return ret;
		case FUNCTION_NOTATION:
			String format = "%s";
			
			if (next != null) {
				format = String.format(format, next.toString());
			}

			return String.format(format, name);
		default:
			return null;
		}
	}
}
