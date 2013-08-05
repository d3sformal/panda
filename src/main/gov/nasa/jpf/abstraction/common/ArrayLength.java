package gov.nasa.jpf.abstraction.common;

import java.util.List;

public class ArrayLength extends Expression {
	
	public AccessPath path;
	
	protected ArrayLength(AccessPath path) {
		this.path = path;
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public List<AccessPath> getPaths() {
		return path.getPaths();
	}

	@Override
	public Expression replace(AccessPath formerPath, Expression expression) {
		Expression ret = path.replace(formerPath, expression);
		
		if (ret instanceof AccessPath) {
			return create((AccessPath) ret);
		}
		
		return this;
	}

	@Override
	public Expression clone() {
		return create(path.clone());
	}
	
	public static ArrayLength create(AccessPath path) {
		if (path == null) {
			return null;
		}
		
		return new ArrayLength(path);
	}

}
