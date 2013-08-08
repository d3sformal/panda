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

public class Class extends DefaultRoot implements ConcreteRoot {
	
	private ThreadInfo ti;
	private ElementInfo ei;
	
	protected Class(String name, ThreadInfo ti, ElementInfo ei) {
		super(name);
		
		this.ti = ti;
		this.ei = ei;
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
	
	public static Class create(String name, ThreadInfo threadInfo, ElementInfo elementInfo) {
		if (name == null || threadInfo == null || elementInfo == null) {
			return null;
		}
		
		return new Class(name, threadInfo, elementInfo);
	}
}
