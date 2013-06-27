package gov.nasa.jpf.abstraction.predicate.common;

public class AccessPathSubElement extends AccessPathMiddleElement {
	public String name;
	
	public AccessPathSubElement(String name) {
		super(null);

		this.name = name;
	}
	
	@Override
	public String toString() {
		switch (AccessPath.policy) {
		case DOT_NOTATION:
			String ret = "." + name.toString();
			
			if (next != null) {
				ret += next.toString();
			}
			
			return ret;
		case FUNCTION_NOTATION:
			String format = "%s";
			
			if (next != null) {
				format = String.format(format, next.toString());
			}

			return String.format(format, "fread(" + name + ", %s)");
		default:
			return null;
		}
	}

}
