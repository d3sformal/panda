package gov.nasa.jpf.abstraction.predicate.common;

public class PathIndexElement extends PathMiddleElement {
	public Expression index;
	
	public PathIndexElement(Expression index) {
		super(null);
		
		this.index = index;
	}
	
	@Override
	public String toString() {
		switch (AccessPath.policy) {
		case DOT_NOTATION:
			String ret = "[" + index.toString() + "]";
			
			if (next != null) {
				ret += next.toString();
			}
			
			return ret;
		case FUNCTION_NOTATION:
			String format = "%s";
			
			if (next != null) {
				format = String.format(format, next.toString());
			}

			return String.format(format, "aread(arr, %s, " + index.toString() + ")");
		default:
			return null;
		}
	}
}
