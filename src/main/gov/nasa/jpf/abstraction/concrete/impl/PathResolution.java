package gov.nasa.jpf.abstraction.concrete.impl;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.concrete.VariableID;
import gov.nasa.jpf.vm.ThreadInfo;

import java.util.HashMap;
import java.util.Map;

public class PathResolution {
	public ThreadInfo threadInfo;
	public Map<AccessExpression, VariableID> processed;
	public Map<AccessExpression, VariableID> current;
	
	public PathResolution(ThreadInfo threadInfo) {
		this(threadInfo, new HashMap<AccessExpression, VariableID>());
	}
	
	public PathResolution(ThreadInfo threadInfo, Map<AccessExpression, VariableID> current) {
		this(threadInfo, new HashMap<AccessExpression, VariableID>(), current);
	}
	
	public PathResolution(ThreadInfo threadInfo, Map<AccessExpression, VariableID> processed, Map<AccessExpression, VariableID> current) {
		this.threadInfo = threadInfo;
		this.processed = processed;
		this.current = current;
	}
}
