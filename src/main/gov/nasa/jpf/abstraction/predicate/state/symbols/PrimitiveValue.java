package gov.nasa.jpf.abstraction.predicate.state.symbols;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PrimitiveValue extends Value {

	@Override
	public Map<AccessExpression, Set<Value>> resolve(AccessExpression prefix, int max) {
		Map<AccessExpression, Set<Value>> ret = new HashMap<AccessExpression, Set<Value>>();
		Set<Value> vals = new HashSet<Value>();
		
		if (max == 0) return ret;
		
		vals.add(this);
		ret.put(prefix, vals);
		
		return ret;
	}
	
	@Override
	public String toString() {
		return "primitive";
	}

	@Override
	public void build(int max) {
	}
}
