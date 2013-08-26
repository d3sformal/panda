package gov.nasa.jpf.abstraction.predicate.state.symbols;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultObjectFieldRead;
import gov.nasa.jpf.abstraction.concrete.Reference;

public class ClassObject extends Object {
	
	public ClassObject(Reference reference) {
		super(reference);
	}

	@Override
	public Map<AccessExpression, Set<Value>> resolve(AccessExpression prefix, int max) {
		Map<AccessExpression, Set<Value>> ret = new HashMap<AccessExpression, Set<Value>>();
		
		if (max == 0) return ret;
		
		for (String name : fields.keySet()) {
			AccessExpression fieldPath = DefaultObjectFieldRead.create(prefix, name);
			
			for (Value value : fields.get(name).getValues()) {
				Map<AccessExpression, Set<Value>> resolution = value.resolve(fieldPath, max - 1);
				
				for (AccessExpression path : resolution.keySet()) {
					if (ret.containsKey(path)) {
						ret.get(path).addAll(resolution.get(path));
					} else {
						ret.put(path, resolution.get(path));
					}
				}
			}
		}
		
		return ret;
	}

}
