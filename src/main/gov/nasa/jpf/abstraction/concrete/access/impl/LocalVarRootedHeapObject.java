package gov.nasa.jpf.abstraction.concrete.access.impl;

import java.util.Map;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.common.access.impl.DefaultRoot;
import gov.nasa.jpf.abstraction.concrete.CompleteVariableID;
import gov.nasa.jpf.abstraction.concrete.VariableID;
import gov.nasa.jpf.abstraction.concrete.access.ConcreteRoot;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.jpf.vm.ThreadInfo;

public class LocalVarRootedHeapObject extends DefaultRoot implements ConcreteRoot {
	
	private ThreadInfo ti;
	private ElementInfo ei;
	private LocalVarInfo li;
	
	protected LocalVarRootedHeapObject(String name, ThreadInfo ti, ElementInfo ei, LocalVarInfo li) {
		super(name);
		
		this.ti = ti;
		this.ei = ei;
		this.li = li;
	}
	
	@Override
	public Map<AccessExpression, VariableID> partialResolve() {
		throw new RuntimeException("Not Yet Re-Implemented.");
	}

	@Override
	public Map<AccessExpression, VariableID> partialExhaustiveResolve() {
		throw new RuntimeException("Not Yet Re-Implemented.");
	}

	@Override
	public Map<AccessExpression, CompleteVariableID> resolve() {
		throw new RuntimeException("Not Yet Re-Implemented.");
	}
	
	public static LocalVarRootedHeapObject create(String name, ThreadInfo threadInfo, ElementInfo elementInfo, LocalVarInfo localVarInfo) {
		if (name == null || threadInfo == null || elementInfo == null || localVarInfo == null) {
			return null;
		}
		
		return new LocalVarRootedHeapObject(name, threadInfo, elementInfo, localVarInfo);
	}
}
