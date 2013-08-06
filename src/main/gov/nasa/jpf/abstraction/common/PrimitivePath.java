package gov.nasa.jpf.abstraction.common;

import java.util.List;

public class PrimitivePath extends PrimitiveExpression {
	
	protected AccessPath path;
	
	protected PrimitivePath(AccessPath path) {
		this.path = path;
	}

	@Override
	public void accept(PredicatesVisitor visitor) {
		path.accept(visitor); 
	}
	
	@Override
	public Expression replace(AccessPath formerPath, Expression expression) {
		return path.replace(formerPath, expression);
	}
	
	@Override
	public List<AccessPath> getPaths() {
		return path.getPaths();
	}
	
	@Override
	public Expression clone() {
		return create(path.clone());
	}
	
	public static PrimitivePath create(AccessPath path) {
		if (path == null) {
			return null;
		}
		
		return new PrimitivePath(path);
	}

}
