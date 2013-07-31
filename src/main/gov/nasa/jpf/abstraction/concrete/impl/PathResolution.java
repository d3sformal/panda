package gov.nasa.jpf.abstraction.concrete.impl;

import gov.nasa.jpf.abstraction.common.AccessPath;
import gov.nasa.jpf.abstraction.concrete.VariableID;

import java.util.HashMap;
import java.util.Map;

public class PathResolution {
	public Map<AccessPath, VariableID> processed;
	public Map<AccessPath, VariableID> current;
	
	public PathResolution() {
		this(new HashMap<AccessPath, VariableID>());
	}
	
	public PathResolution(Map<AccessPath, VariableID> current) {
		this(new HashMap<AccessPath, VariableID>(), current);
	}
	
	public PathResolution(Map<AccessPath, VariableID> processed, Map<AccessPath, VariableID> current) {
		this.processed = processed;
		this.current = current;
	}
}
