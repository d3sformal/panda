package gov.nasa.jpf.abstraction.predicate.common;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.common.Expression;

import java.util.List;

public abstract class Operation extends Expression {
	public Expression a;
	public Expression b;
	
	public Operation(Expression a, Expression b) {
		this.a = a;
		this.b = b;
		
		this.paths.addAll(a.getPaths());
		this.paths.addAll(b.getPaths());
	}
	
	@Override
	public List<AccessPath> getPaths() {
		return paths;
	}
}
