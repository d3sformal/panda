package gov.nasa.jpf.abstraction.predicate.grammar;

import java.util.ArrayList;
import java.util.List;

public abstract class Comparison extends Predicate {
	public Expression a;
	public Expression b;
	
	public Comparison(Expression a, Expression b) {
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
}
