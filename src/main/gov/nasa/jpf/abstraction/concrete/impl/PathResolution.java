package gov.nasa.jpf.abstraction.concrete.impl;

import gov.nasa.jpf.abstraction.common.AccessExpression;
import gov.nasa.jpf.abstraction.concrete.VariableID;

import java.util.HashMap;
import java.util.Map;

public class PathResolution {
	public Map<AccessExpression, VariableID> processed;
	public Map<AccessExpression, VariableID> current;
	
	public PathResolution() {
		this(new HashMap<AccessExpression, VariableID>());
	}
	
	public PathResolution(Map<AccessExpression, VariableID> current) {
		this(new HashMap<AccessExpression, VariableID>(), current);
	}
	
	public PathResolution(Map<AccessExpression, VariableID> processed, Map<AccessExpression, VariableID> current) {
		this.processed = processed;
		this.current = current;
	}
}
