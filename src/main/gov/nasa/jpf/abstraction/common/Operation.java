package gov.nasa.jpf.abstraction.common;

import java.util.ArrayList;
import java.util.List;

public abstract class Operation extends Expression {
	public Expression a;
	public Expression b;
	
	protected Operation(Expression a, Expression b) {
		this.a = a;
		this.b = b;
	}
	
	@Override
	public List<AccessPath> getPaths() {
		List<AccessPath> ret = new ArrayList<AccessPath>();
		
		ret.addAll(a.getPaths());
		ret.addAll(b.getPaths());
		
		return ret;
	}
	
	protected static boolean argumentsDefined(Expression a, Expression b) {
		return a != null && b != null;
	}
}
