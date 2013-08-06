package gov.nasa.jpf.abstraction.common;

public class ArrayPath extends ObjectPath {
	
	protected ArrayPath(AccessPath path) {
		super(path);
	}

	@Override
	public Expression clone() {
		return create(path.clone());
	}
	
	public static ArrayPath create(AccessPath path) {
		if (path == null) {
			return null;
		}
		
		return new ArrayPath(path);
	}
}
